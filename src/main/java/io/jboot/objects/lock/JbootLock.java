package io.jboot.objects.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/28
 */
public interface JbootLock extends Lock {

    @Override
    void lock();

    @Override
    void unlock();

    @Override
    boolean tryLock();

    @Override
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
}
