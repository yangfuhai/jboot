package distributedtask;

import io.jboot.schedule.JbootDistributedRunnable;
import io.jboot.schedule.annotation.Cron4jTask;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 分布式任务
 * @Description: 每2分钟执行一次
 * @Package distributedtask
 */
@Cron4jTask(cron = "*/2 * * * *")
public class MyTask extends JbootDistributedRunnable {

    @Override
    public boolean run() {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return true;
    }


}
