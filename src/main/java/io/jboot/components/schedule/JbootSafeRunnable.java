package io.jboot.components.schedule;

import com.jfinal.log.Log;

/**
 * 使用 try catch 包裹业务代码，防止业务现场抛出异常导致 ScheduledThreadPoolExecutor 终止调度
 *
 * @author orangej
 * @since 2021-9-26
 */
public class JbootSafeRunnable implements Runnable {
    private static final Log LOG = Log.getLog(JbootSafeRunnable.class);

    private Runnable job;

    public JbootSafeRunnable(Runnable job) {
        this.job = job;
    }

    @Override
    public void run() {
        try {
            job.run();
        } catch (Throwable ex) {
            LOG.error(ex.toString(), ex);
        }
    }
}
