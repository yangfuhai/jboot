/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.schedule;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import io.jboot.Jboot;
import io.jboot.exception.JbootException;
import io.jboot.schedule.annotation.Cron;
import io.jboot.schedule.annotation.EnableDistributedRunnable;
import io.jboot.schedule.annotation.FixedDelay;
import io.jboot.schedule.annotation.FixedRate;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;
import it.sauronsoftware.cron4j.ProcessTask;
import it.sauronsoftware.cron4j.Task;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class JbootScheduleManager {

    private static final Log LOG = Log.getLog(JbootScheduleManager.class);

    private static JbootScheduleManager manager;
    private Cron4jPlugin cron4jPlugin;
    private ScheduledThreadPoolExecutor fixedScheduler;
    private JbooScheduleConfig config;

    public JbootScheduleManager() {
        config = Jboot.config(JbooScheduleConfig.class);
        fixedScheduler = new ScheduledThreadPoolExecutor(config.getPoolSize());

        Prop prop = null;
        try {
            prop = PropKit.use(config.getCron4jFile());
        } catch (Throwable ex) {
        }

        cron4jPlugin = prop == null ? new Cron4jPlugin() : new Cron4jPlugin(prop);
    }


    public static final JbootScheduleManager me() {
        if (manager == null) {
            manager = ClassNewer.singleton(JbootScheduleManager.class);
        }
        return manager;
    }

    public void init() {
        initScheduledThreadPoolExecutor();
        initCron4jPlugin();
        cron4jPlugin.start();
    }


    private void initScheduledThreadPoolExecutor() {
        List<Class> fixedDelayClasses = ClassScanner.scanClassByAnnotation(FixedDelay.class, true);
        for (Class clazz : fixedDelayClasses) {
            if (!Runnable.class.isAssignableFrom(clazz)) {
                throw new RuntimeException(clazz.getName() + " must implements Runnable");
            }
            FixedDelay fixedDelayJob = (FixedDelay) clazz.getAnnotation(FixedDelay.class);
            Runnable runnable = (Runnable) ClassNewer.newInstance(clazz);
            Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new JbootDistributedRunnable(runnable);
            try {
                fixedScheduler.scheduleWithFixedDelay(executeRunnable, fixedDelayJob.initialDelay(), fixedDelayJob.period(), TimeUnit.SECONDS);
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
        }

        List<Class> fixedRateClasses = ClassScanner.scanClassByAnnotation(FixedRate.class, true);
        for (Class clazz : fixedRateClasses) {
            if (!Runnable.class.isAssignableFrom(clazz)) {
                throw new RuntimeException(clazz.getName() + " must implements Runnable");
            }
            FixedRate fixedDelayJob = (FixedRate) clazz.getAnnotation(FixedRate.class);
            Runnable runnable = (Runnable) ClassNewer.newInstance(clazz);
            Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new JbootDistributedRunnable(runnable);
            try {
                fixedScheduler.scheduleAtFixedRate(executeRunnable, fixedDelayJob.initialDelay(), fixedDelayJob.period(), TimeUnit.SECONDS);
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
        }
    }


    private void initCron4jPlugin() {
        List<Class> cronClasses = ClassScanner.scanClassByAnnotation(Cron.class, true);
        for (Class clazz : cronClasses) {
            Cron cron = (Cron) clazz.getAnnotation(Cron.class);
            if (Runnable.class.isAssignableFrom(clazz)) {
                Runnable runnable = (Runnable) ClassNewer.newInstance(clazz);
                Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new JbootDistributedRunnable(runnable);
                cron4jPlugin.addTask(cron.value(), executeRunnable, cron.daemon());
            } else if (ProcessTask.class.isAssignableFrom(clazz)) {
                cron4jPlugin.addTask(cron.value(), (ProcessTask) ClassNewer.newInstance(clazz), cron.daemon());
            } else if (Task.class.isAssignableFrom(clazz)) {
                cron4jPlugin.addTask(cron.value(), (Task) ClassNewer.newInstance(clazz), cron.daemon());
            } else {
                throw new JbootException("annotation Cron can not use for class : " + clazz);
            }
        }
    }


}
