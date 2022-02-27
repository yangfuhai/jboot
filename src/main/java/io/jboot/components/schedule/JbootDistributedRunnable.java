/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.components.schedule;

import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.QuietlyUtil;
import io.jboot.utils.StrUtil;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 分布式任务
 * @Description: 在分布式应用中，处理分布式应用，基于redis。
 * <p>
 * 特点：
 * 1、简单，无需依赖数据库。
 * 2、高可用，不存在单点故障
 * 3、一致性，在集群环境中，只有一个任务在执行。
 * 4、Failover，支持故障转移
 */
public class JbootDistributedRunnable implements Runnable {

    private static final Log LOG = Log.getLog(JbootDistributedRunnable.class);

    private JbootRedis redis;

    // 锁的过期时间，单位毫秒，默认 1 分钟，
    // 因此，配置的定时任务，每次执行任务的时间间隔，必须大于 1 分钟以上
    private int expire = 60 * 1000;
    private String key;
    private Runnable runnable;


    public JbootDistributedRunnable(Runnable runnable, String key, int expireSeconds) {
        this.runnable = runnable;

        if (StrUtil.isNotBlank(key)) {
            this.key = key;
        } else {
            this.key = "jboot-distributed-key:" + ClassUtil.getUsefulClass(runnable.getClass()).getName();
        }

        if (expireSeconds > 0) {
            this.expire = expireSeconds * 1000;
        }

        this.redis = Jboot.getRedis();

        if (redis == null) {
            LOG.warn("Redis is null, Can not use @EnableDistributedRunnable in your class: "
                    + ClassUtil.getUsefulClass(runnable.getClass()).getName()
                    + ", Please config redis info in jboot.properties");
        }

    }


    @Override
    public void run() {
        if (redis == null) {
            // 当未配置 redis 的时候，默认使用本地任务的方式进行执行
            runnable.run();
            return;
        }


        Long setTimeMillis = System.currentTimeMillis();

        //要设置的值
        String setValue = setTimeMillis + ":" + StrUtil.uuid();


        boolean locked = false;

        for (int i = 0; i < 5; i++) {

            //setnx: 只在键 key 不存在的情况下， 将键 key 的值设置为 value， 若键 key 已经存在， 则 SETNX 命令不做任何动作。
            //result: 设置成功，返回 1，设置失败，返回 0
            Long result = redis.setnx(key, setValue);

            //error 一般不会出现这种情况，除非是网络异常等原因
            if (result == null) {
                quietlySleep();
                continue;
            }


            //setnx success 设置成功
            if (result == 1) {
                String value = redis.get(key);

                //在分布式的场景下，可能自己设置成功后，又被其他节点删除重新设置的情况
                //需要判断是否是当前节点（或者线程）设置的
                if (setValue.equals(value)) {
                    locked = true;
                    break;
                }
                // 可能被其他节点删除，或重置了
                else {
                    quietlySleep();
                    continue;
                }
            }

            //setnx fail，可能已经被其他 server 优先设置了，也有可能是 自己的server 在上一次任务里设置了
            else if (result == 0) {

                String value = null;

                try {
                    value = redis.get(key);
                } catch (Exception ex) {
                    LogKit.logNothing(ex);
                }


                //获取不到，一般不会出现这种情况，除非是网络异常等原因
                //或者是使用了已经存在的 key，但是此 key 已经有其他序列化方式的值导致异常
                if (value == null) {
                    reset();
                    quietlySleep();
                    continue;
                }

                String[] split = value.split(":");

                //被其他节点，或者手动操作 redis 的方式给设置了这个key值
                if (split.length != 2) {
                    reset();
                    continue;
                }

                //获取设置的时间
                long savedTimeMillis = 0;

                try {
                    savedTimeMillis = Long.parseLong(split[0]);
                } catch (NumberFormatException ex) {
                    LogKit.logNothing(ex);
                }

                //redis 存储的内容有问题，可能是被手动设置 redis 的方式设置了这个 key 值
                if (savedTimeMillis == 0) {
                    reset();
                    continue;
                }

                if ((System.currentTimeMillis() - savedTimeMillis) > expire) {
                    //若设置锁的时间以及过期了
                    //说明是上一次任务配置的，此时需要删除这个过期的 key，然后重新去抢
                    reset();
                }
                // 若锁没有过期，休息后重新去抢，因为抢走的线程可能会重新释放
                else {
                    quietlySleep();
                }
            }

        }

        //抢了5次都抢不到，证明已经被别的应用抢走了
        if (!locked) {
            return;
        }


        try {
            runnable.run();
        }

        // 如果 run() 执行异常，让别的分布式应用APP去执行
        // 但如果 run() 执行的时间很长（超过30秒），而且失败了，那么别的分布式应用可能也抢不到了，只能等待下次任务
        // 作用：故障转移
        catch (Throwable ex) {
            LOG.error(ex.toString(), ex);
            reset();
        }
    }


    /**
     * 重置分布式的 key
     */
    private void reset() {
        try {
            redis.del(key);
        } catch (Exception ex) {
            LogKit.logNothing(ex);
        }
    }


    public void quietlySleep() {
        int millis = 2000;
        if (this.expire <= 2000) {
            millis = 100;
        } else if (this.expire <= 5000) {
            millis = 500;
        } else if (this.expire <= 300000) {
            millis = 1000;
        }

        QuietlyUtil.quietlySleep(millis);
    }


}
