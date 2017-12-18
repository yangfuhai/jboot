package distributedtask;

import io.jboot.schedule.annotation.EnableDistributedRunnable;
import io.jboot.schedule.annotation.FixedDelay;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 分布式任务
 * @Description: 每1分钟执行一次
 * @Package distributedtask
 */
//@Cron("*/1 * * * *")
@FixedDelay(period = 5)
@EnableDistributedRunnable
public class MyTask implements Runnable {

    @Override
    public void run() {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
