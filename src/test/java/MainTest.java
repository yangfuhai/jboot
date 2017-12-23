import com.google.common.util.concurrent.RateLimiter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainTest {

    public static void main(String[] args) {

        RateLimiter rateLimiter = RateLimiter.create(2);

        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < 10; i++) {
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("执行中...." + rateLimiter.tryAcquire());
                }
            });
        }
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (Runnable runnable : tasks) {
//            System.out.println("等待时间：" + rateLimiter.acquire());
            threadPool.execute(runnable);
        }
    }




}






