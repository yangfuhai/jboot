package io.jboot.test.distributed;

import io.jboot.components.schedule.annotation.EnableDistributedRunnable;
import io.jboot.components.schedule.annotation.FixedDelay;

@FixedDelay(period = 30)
@EnableDistributedRunnable(redisKey = "aaa",expireSeconds = 30)
public class Runable1 implements Runnable {
    int i =0;
    @Override
    public void run() {
        if (i++ >= 3){
            throw new IllegalStateException("messss...");
        }
        System.err.println("Runable1.running.........."+this);
    }
}
