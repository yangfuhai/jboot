/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.schedule;

import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.component.redis.JbootRedis;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 分布式任务
 * @Description: 在分布式应用中，处理分布式应用，基于redis。
 * 特点： 1、简单，无需依赖数据库。
 * 2、高可用，不存在单点故障
 * 3、一致性，在集群环境中，只有一个任务在执行。
 * 4、Failover，支持故障转移
 * @Package io.jboot.schedule
 */
public abstract class JbootDistributedRunnable implements Runnable {

    private static final Log LOG = Log.getLog(JbootDistributedRunnable.class);

    private JbootRedis redis;
    private int expire = 50; // 单位秒
    private String key;


    public JbootDistributedRunnable() {

        this.redis = Jboot.me().getRedis();
        this.key = "jbootRunnable:" + this.getClass().getName();

        if (redis == null) {
            throw new NullPointerException("redis is null, please config redis info in jboot.properties");
        }
    }


    @Override
    public void run() {
        Long result = null;

        for (int i = 0; i < 6; i++) {

            result = redis.setnx(key, "locked");

            //error
            if (result == null) {
                quietSleep();
            }

            //setnx fail
            else if (result == 0) {
                Long ttl = redis.ttl(key);
                if (ttl == null || ttl <= 0 || ttl > expire) {
                    //防止死锁
                    reset();
                } else {
                    // 休息 2 秒钟，重新去抢，因为可能别的设置好后，但是却执行失败了
                    quietSleep();
                }
            }

            //set success
            else if (result == 1) {
                break;
            }
        }


        //抢了5次都抢不到，证明已经被别的应用抢走了
        if (result == null || result == 0) {
            return;
        }

        //抢到了，但是设置超时时间设置失败，删除后，让分布式的其他app去抢
        Long expireResult = redis.expire(key, 50);
        if (expireResult == null && expireResult <= 0) {
            reset();
            return;
        }

        try {
            boolean runSuccess = execute();

            //run()执行失败，让别的分布式应用APP去执行
            //如果run()执行的时间很长（超过30秒）,那么别的分布式应用可能也抢不到了，只能等待下次轮休
            //作用：故障转移
            if (!runSuccess) {
                reset();
            }
        }

        // 如果 run() 执行异常，让别的分布式应用APP去执行
        // 作用：故障转移
        catch (Throwable ex) {
            LOG.error(ex.toString(), ex);
            reset();
        }
    }


    /**
     * 重置分布式的key
     */
    private void reset() {
        redis.del(key);
    }

    public static void quietSleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract boolean execute();
}
