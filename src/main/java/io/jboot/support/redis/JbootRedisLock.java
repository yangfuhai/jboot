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
package io.jboot.support.redis;

import io.jboot.Jboot;
import io.jboot.utils.QuietlyUtil;

/**
 * Created by michael.
 * <p>
 * Redis 分布式锁
 * <p>
 * 使用方法：
 * <p>
 * JbootRedisLock lock = new JbootRedisLock("lockName");
 * try{
 * boolean acquire = lock.acquire();
 * if(acquire){
 * // do your something
 * }
 * }finally {
 * lock.release();
 * }
 * <p>
 * 使用方法2：
 * JbootRedisLock lock = new JbootRedisLock("lockName");
 * lock.runIfAcquired(new Runnable(){
 * <p>
 * public void run() {
 * //do your something
 * }
 * });
 */
public class JbootRedisLock {

    // 60 秒 expireMsecs 锁持有超时，防止线程在入锁以后，无限的执行下去，让锁无法释放
    long expireMsecs = 1000 * 60;

    // 锁等待超时
    long timeoutMsecs = 0;

    private final String lockName;
    private boolean locked = false;
    private JbootRedis redis;

    /**
     * 创建redis分布式锁
     *
     * @param lockName 锁的名称
     */
    public JbootRedisLock(String lockName) {
        if (lockName == null) {
            throw new NullPointerException("lockName must not be null.");
        }
        this.lockName = lockName;
        this.redis = Jboot.getRedis();
    }

    /**
     * 创建redis分布式锁
     *
     * @param lockName     锁名称
     * @param timeoutMsecs 获取锁的时候，等待时长
     */
    public JbootRedisLock(String lockName, long timeoutMsecs) {
        if (lockName == null) {
            throw new NullPointerException("lockName must not be null.");
        }
        this.lockName = lockName;
        this.timeoutMsecs = timeoutMsecs;
        this.redis = Jboot.getRedis();
    }


    /**
     * @param lockName     锁名称
     * @param timeoutMsecs 获取锁的时候，等待时长
     * @param expireMsecs  超时时长
     */
    public JbootRedisLock(String lockName, long timeoutMsecs, long expireMsecs) {
        if (lockName == null) {
            throw new NullPointerException("lockName must not be null.");
        }
        this.lockName = lockName;
        this.timeoutMsecs = timeoutMsecs;
        this.expireMsecs = expireMsecs;

        this.redis = Jboot.getRedis();
    }


    public long getTimeoutMsecs() {
        return timeoutMsecs;
    }

    public void setTimeoutMsecs(long timeoutMsecs) {
        this.timeoutMsecs = timeoutMsecs;
    }

    public long getExpireMsecs() {
        return expireMsecs;
    }

    public void setExpireMsecs(long expireMsecs) {
        this.expireMsecs = expireMsecs;
    }

    public void runIfAcquired(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable must not null!");
        }
        try {
            if (acquire()) {
                runnable.run();
            }
        } finally {
            //执行完毕，释放锁
            release();
        }
    }


    /**
     * 获取锁
     *
     * @return true：活动锁了 ， false ：没获得锁。 如果设置了timeoutMsecs，那么这个方法可能被延迟 timeoutMsecs 毫秒。
     */
    public boolean acquire() {
        long timeout = timeoutMsecs;
        do {
            try {
                //超时时间
                long expiredTime = System.currentTimeMillis() + expireMsecs + 1;

                Long result = redis.setnx(lockName, expiredTime);

                if (result == null) {
                    continue;
                }

                // lock acquired
                if (result == 1) {
                    locked = true;
                    return true;
                }

                //之前的保存时间
                Long savedValue = redis.get(lockName);

                //这个锁已经过期了，此时可以去设置新的key
                if (savedValue != null && savedValue < System.currentTimeMillis()) {


                    //获取上一个锁到期时间，并设置现在的锁到期时间，
                    //只有一获个线程才能取上一个线上的设置时间，因为jedis.getSet是同步的
                    Long oldValue = redis.getSet(lockName, expiredTime);


                    if (oldValue != null && oldValue.equals(savedValue)) {
                        //如果这个时候，多个线程恰好都到了这里
                        //只有一个线程的设置值和当前值相同，他才有权利获取锁
                        //lock acquired
                        locked = true;
                        return true;
                    }
                }
            } finally {
                if (timeout > 0) {
                    timeout -= 100;
                    QuietlyUtil.sleepQuietly(100);
                }
            }
        } while (timeout > 0);

        return false;
    }


    /**
     * 是否获得 锁 了
     *
     * @return
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * 释放 锁
     */
    public void release() {
        if (!isLocked()) {
            return;
        }
        if (Jboot.getRedis().del(lockName) > 0) {
            locked = false;
        }
    }
}
