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
import io.jboot.utils.ClassScanner;
import io.jboot.utils.ClassUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class JbootScheduleManager {

    private static final Log LOG = Log.getLog(JbootScheduleManager.class);

    private static JbootScheduleManager manager = new JbootScheduleManager();
    private JbootCron4jPlugin cron4jPlugin;
    private ScheduledThreadPoolExecutor fixedScheduler;
    private JbooScheduleConfig config;

    private Map<Class,Runnable> scheduleRunnableCache = new ConcurrentHashMap<>();

    public JbootScheduleManager() {
        config = Jboot.config(JbooScheduleConfig.class);
        fixedScheduler = new ScheduledThreadPoolExecutor(config.getPoolSize());

        File cron4jProperties = new File(PathKit.getRootClassPath(), config.getCron4jFile());
        cron4jPlugin = cron4jProperties.exists()
                ? new JbootCron4jPlugin(new Prop(config.getCron4jFile()))
                : new JbootCron4jPlugin();

    }


    public static final JbootScheduleManager me() {
        return manager;
    }

    public void init() {
        initSchedules();
//        initCron4jPlugin();
        cron4jPlugin.start();
    }

    public void stop() {
        fixedScheduler.shutdownNow();
        cron4jPlugin.stop();
    }

    private void initSchedules() {
        List<Class<Runnable>> runnableClass = ClassScanner.scanSubClass(Runnable.class, true);
        if (runnableClass != null)
            for (Class<Runnable> rc : runnableClass) addSchedule(rc);
    }

    public void addSchedule(Class<? extends Runnable> runnableClass) {
        FixedDelay fixedDelayJob = runnableClass.getAnnotation(FixedDelay.class);
        if (fixedDelayJob != null) {
            Runnable runnable = ClassUtil.newInstance(runnableClass);
            Runnable executeRunnable = runnableClass.getAnnotation(EnableDistributedRunnable.class) == null
                    ? runnable
                    : new JbootDistributedRunnable(runnable, fixedDelayJob.period());
            try {
                scheduleRunnableCache.put(runnableClass,executeRunnable);
                fixedScheduler.scheduleWithFixedDelay(executeRunnable, fixedDelayJob.initialDelay(), fixedDelayJob.period(), TimeUnit.SECONDS);
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
        }

        FixedRate fixedRateJob = runnableClass.getAnnotation(FixedRate.class);
        if (fixedRateJob != null) {
            Runnable runnable = ClassUtil.newInstance(runnableClass);
            Runnable executeRunnable = runnableClass.getAnnotation(EnableDistributedRunnable.class) == null
                    ? runnable
                    : new JbootDistributedRunnable(runnable, fixedRateJob.period());
            try {
                scheduleRunnableCache.put(runnableClass,executeRunnable);
                fixedScheduler.scheduleAtFixedRate(executeRunnable, fixedRateJob.initialDelay(), fixedRateJob.period(), TimeUnit.SECONDS);
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
        }


        Cron cron = runnableClass.getAnnotation(Cron.class);
        if (cron != null) {
            String value = AnnotationUtil.get(cron.value());
            Runnable runnable = ClassUtil.newInstance(runnableClass);
            Runnable executeRunnable = runnableClass.getAnnotation(EnableDistributedRunnable.class) == null
                    ? runnable
                    : new JbootDistributedRunnable(runnable);
            scheduleRunnableCache.put(runnableClass,executeRunnable);
            cron4jPlugin.addTask(value, executeRunnable, cron.daemon());
        }
    }

    public void removeSchedule(Class<? extends Runnable> removeClass) {
        Runnable runnable = scheduleRunnableCache.get(removeClass);
        if (runnable != null){
            fixedScheduler.remove(runnable);
            scheduleRunnableCache.remove(removeClass);
        }

        //不支持 cron4jPlugin 的remove
    }

    public Map<Class, Runnable> getScheduleRunnableCache() {
        return scheduleRunnableCache;
    }

    public JbootCron4jPlugin getCron4jPlugin() {
        return cron4jPlugin;
    }

    public ScheduledThreadPoolExecutor getFixedScheduler() {
        return fixedScheduler;
    }

    //    private void initCron4jPlugin() {
//        List<Class> cronClasses = ClassScanner.scanClassByAnnotation(Cron.class, true);
//        for (Class clazz : cronClasses) {
//            Cron cron = (Cron) clazz.getAnnotation(Cron.class);
//            String value = AnnotationUtil.get(cron.value());
//            if (Runnable.class.isAssignableFrom(clazz)) {
//                Runnable runnable = (Runnable) ClassUtil.newInstance(clazz);
//                Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new JbootDistributedRunnable(runnable);
//                cron4jPlugin.addTask(value, executeRunnable, cron.daemon());
//            } else if (ProcessTask.class.isAssignableFrom(clazz)) {
//                cron4jPlugin.addTask(value, (ProcessTask) ClassUtil.newInstance(clazz), cron.daemon());
//            } else if (Task.class.isAssignableFrom(clazz)) {
//                cron4jPlugin.addTask(value, (Task) ClassUtil.newInstance(clazz), cron.daemon());
//            } else {
//                throw new RuntimeException("annotation Cron can not use for class : " + clazz);
//            }
//        }
//    }


}
