/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.aop.Aop;
import com.jfinal.kit.Prop;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.cron4j.ITask;
import it.sauronsoftware.cron4j.ProcessTask;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;

import java.util.ArrayList;
import java.util.List;


public class JbootCron4jPlugin implements IPlugin {

    private List<TaskInfo> taskInfoList = new ArrayList<>();
    public static final String defaultConfigName = "cron4j";

    public JbootCron4jPlugin() {

    }

    public JbootCron4jPlugin(String configFile) {
        this(new Prop(configFile), defaultConfigName);
    }

    public JbootCron4jPlugin(Prop configProp) {
        this(configProp, defaultConfigName);
    }

    public JbootCron4jPlugin(String configFile, String configName) {
        this(new Prop(configFile), configName);
    }

    public JbootCron4jPlugin(Prop configProp, String configName) {
        try {
            addTask(configProp, configName);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * 考虑添加对 ProcessTask 的配置支持，目前不支持 ProcessTask 对象的构造方法的参数配置
     * 对于 ProcessTask 型的任务调度，建议对 ProcessTask 的创建使用 java 代码
     */
    private void addTask(Prop configProp, String configName) throws Exception {
        String configNameValue = configProp.get(configName);
        if (StrKit.isBlank(configNameValue)) {
            throw new IllegalArgumentException("The value of configName: " + configName + " can not be blank.");
        }
        String[] taskNameArray = configNameValue.trim().split(",");
        for (String taskName : taskNameArray) {
            if (StrKit.isBlank(taskName)) {
                throw new IllegalArgumentException("taskName can not be blank.");
            }
            taskName = taskName.trim();

            String taskCron = configProp.get(taskName + ".cron");
            if (StrKit.isBlank(taskCron)) {
                throw new IllegalArgumentException(taskName + ".cron" + " not found.");
            }
            taskCron = taskCron.trim();

            String taskClass = configProp.get(taskName + ".class");
            if (StrKit.isBlank(taskClass)) {
                throw new IllegalArgumentException(taskName + ".class" + " not found.");
            }
            taskClass = taskClass.trim();

            Object taskObj = Class.forName(taskClass).newInstance();
            if (!(taskObj instanceof Runnable) && !(taskObj instanceof Task)) {
                throw new IllegalArgumentException("Task 必须是 Runnable、ITask、ProcessTask 或者 Task 类型");
            }

            boolean taskDaemon = configProp.getBoolean(taskName + ".daemon", false);
            boolean taskEnable = configProp.getBoolean(taskName + ".enable", true);
            taskInfoList.add(new JbootCron4jPlugin.TaskInfo(taskCron, taskObj, taskDaemon, taskEnable));
        }
    }

    public JbootCron4jPlugin addTask(String cron, Runnable task, boolean daemon, boolean enable) {
        taskInfoList.add(new JbootCron4jPlugin.TaskInfo(cron, task, daemon, enable));
        return this;
    }

    public JbootCron4jPlugin addTask(String cron, Runnable task, boolean daemon) {
        return addTask(cron, task, daemon, true);
    }

    public JbootCron4jPlugin addTask(String cron, Runnable task) {
        return addTask(cron, task, false, true);
    }

    public JbootCron4jPlugin addTask(String cron, ProcessTask processTask, boolean daemon, boolean enable) {
        taskInfoList.add(new JbootCron4jPlugin.TaskInfo(cron, processTask, daemon, enable));
        return this;
    }

    public JbootCron4jPlugin addTask(String cron, ProcessTask processTask, boolean daemon) {
        return addTask(cron, processTask, daemon, true);
    }

    public JbootCron4jPlugin addTask(String cron, ProcessTask processTask) {
        return addTask(cron, processTask, false, true);
    }

    public JbootCron4jPlugin addTask(String cron, Task task, boolean daemon, boolean enable) {
        taskInfoList.add(new JbootCron4jPlugin.TaskInfo(cron, task, daemon, enable));
        return this;
    }

    public JbootCron4jPlugin addTask(String cron, Task task, boolean daemon) {
        return addTask(cron, task, daemon, true);
    }

    public JbootCron4jPlugin addTask(String cron, Task task) {
        return addTask(cron, task, false, true);
    }

    @Override
    public boolean start() {
        for (JbootCron4jPlugin.TaskInfo taskInfo : taskInfoList) {
            taskInfo.schedule();
        }
        for (JbootCron4jPlugin.TaskInfo taskInfo : taskInfoList) {
            taskInfo.start();
        }
        return true;
    }

    @Override
    public boolean stop() {
        for (JbootCron4jPlugin.TaskInfo taskInfo : taskInfoList) {
            taskInfo.stop();
        }
        return true;
    }

    private static class TaskInfo {
        Scheduler scheduler;

        String cron;
        Object task;
        boolean daemon;
        boolean enable;

        TaskInfo(String cron, Object task, boolean daemon, boolean enable) {
            if (StrKit.isBlank(cron)) {
                throw new IllegalArgumentException("cron 不能为空.");
            }
            if (task == null) {
                throw new IllegalArgumentException("task 不能为 null.");
            }

            Aop.inject(task);

            this.cron = cron.trim();
            this.task = task;
            this.daemon = daemon;
            this.enable = enable;
        }

        void schedule() {
            if (enable) {
                scheduler = new Scheduler();
                if (task instanceof Runnable) {
                    scheduler.schedule(cron, (Runnable) task);
                } else if (task instanceof Task) {
                    scheduler.schedule(cron, (Task) task);
                } else {
                    scheduler = null;
                    throw new IllegalStateException("Task 必须是 Runnable、ITask、ProcessTask 或者 Task 类型");
                }
                scheduler.setDaemon(daemon);
            }
        }

        void start() {
            if (enable) {
                scheduler.start();
            }
        }

        void stop() {
            if (enable) {
                if (task instanceof ITask) {   // 如果任务实现了 ITask 接口，则回调 ITask.stop() 方法
                    ((ITask) task).stop();
                }
                scheduler.stop();
            }
        }
    }
}
