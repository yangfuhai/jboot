/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.components.schedule.annotation.Cron;
import io.jboot.components.schedule.annotation.EnableDistributedRunnable;
import io.jboot.components.schedule.annotation.FixedDelay;
import io.jboot.components.schedule.annotation.FixedRate;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.ClassScanner;
import it.sauronsoftware.cron4j.ProcessTask;
import it.sauronsoftware.cron4j.Task;

import java.io.File;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class JbootScheduleManager {

    private static final Log LOG = Log.getLog(JbootScheduleManager.class);

    private static JbootScheduleManager manager;
    private JbootCron4jPlugin cron4jPlugin;
    private ScheduledThreadPoolExecutor fixedScheduler;
    private JbooScheduleConfig config;

    public JbootScheduleManager() {
        config = Jboot.config(JbooScheduleConfig.class);
        fixedScheduler = new ScheduledThreadPoolExecutor(config.getPoolSize());

        File cron4jProperties = new File(PathKit.getRootClassPath(), config.getCron4jFile());
        cron4jPlugin = cron4jProperties.exists()
                ? new JbootCron4jPlugin(new Prop(config.getCron4jFile()))
                : new JbootCron4jPlugin();

    }


    public static final JbootScheduleManager me() {
        if (manager == null) {
            manager = ClassUtil.singleton(JbootScheduleManager.class);
        }
        return manager;
    }

    public void init() {
        initScheduledThreadPoolExecutor();
        initCron4jPlugin();
        cron4jPlugin.start();
    }

    public void stop(){
        fixedScheduler.shutdownNow();
        cron4jPlugin.stop();
    }

    private void initScheduledThreadPoolExecutor() {
        List<Class> fixedDelayClasses = ClassScanner.scanClassByAnnotation(FixedDelay.class, true);
        for (Class clazz : fixedDelayClasses) {
            if (!Runnable.class.isAssignableFrom(clazz)) {
                throw new RuntimeException(clazz.getName() + " must implements Runnable");
            }
            FixedDelay fixedDelayJob = (FixedDelay) clazz.getAnnotation(FixedDelay.class);
            Runnable runnable = (Runnable) ClassUtil.newInstance(clazz);
            Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new JbootDistributedRunnable(runnable, fixedDelayJob.period());
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
            Runnable runnable = (Runnable) ClassUtil.newInstance(clazz);
            Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new JbootDistributedRunnable(runnable, fixedDelayJob.period());
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
            String value = AnnotationUtil.get(cron.value());
            if (Runnable.class.isAssignableFrom(clazz)) {
                Runnable runnable = (Runnable) ClassUtil.newInstance(clazz);
                Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new JbootDistributedRunnable(runnable);
                cron4jPlugin.addTask(value, executeRunnable, cron.daemon());
            } else if (ProcessTask.class.isAssignableFrom(clazz)) {
                cron4jPlugin.addTask(value, (ProcessTask) ClassUtil.newInstance(clazz), cron.daemon());
            } else if (Task.class.isAssignableFrom(clazz)) {
                cron4jPlugin.addTask(value, (Task) ClassUtil.newInstance(clazz), cron.daemon());
            } else {
                throw new RuntimeException("annotation Cron can not use for class : " + clazz);
            }
        }
    }


}
