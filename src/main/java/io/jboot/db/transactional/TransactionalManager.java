/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.db.transactional;

import com.jfinal.aop.Invocation;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.NamedThreadFactory;
import io.jboot.utils.StrUtil;

import java.util.Map;
import java.util.concurrent.*;

public class TransactionalManager {

    private static TransactionalManager instance = new TransactionalManager();

    public static TransactionalManager me() {
        return instance;
    }


    private Map<String, ExecutorService> executorServiceMap = new ConcurrentHashMap<>();
    private ThreadFactory threadFactory = new NamedThreadFactory("Transactional", true);

    //当未配置线程池名称的时候，是否使用 threadFactory 来执行
    private boolean runDefaultWithoutConfigName = false;


    public void addExecutorService(String name, ExecutorService service) {
        executorServiceMap.put(name, service);
    }

    public ExecutorService getExecutorService(String name) {
        return executorServiceMap.get(name);
    }

    public void removeExecutorService(String name) {
        executorServiceMap.remove(name);
    }

    public Map<String, ExecutorService> getExecutorServiceMap() {
        return executorServiceMap;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public boolean isRunDefaultWithoutConfigName() {
        return runDefaultWithoutConfigName;
    }

    public void setRunDefaultWithoutConfigName(boolean runDefaultWithoutConfigName) {
        this.runDefaultWithoutConfigName = runDefaultWithoutConfigName;
    }

    public Future<Boolean> execute(Invocation inv, String byName, Callable<Boolean> callable) {
        if (StrUtil.isBlank(byName)) {
            FutureTask<Boolean> task = new FutureTask<>(callable);
            threadFactory.newThread(task).start();
            return task;
        }


        ExecutorService executorService = executorServiceMap.get(byName);
        if (executorService != null) {
            return executorService.submit(callable);
        }

        if (!runDefaultWithoutConfigName) {
            throw new IllegalStateException("Can not find threadPoolName: \"" + byName + "\" for @Transactional() in method: "
                    + ClassUtil.buildMethodString(inv.getMethod())
                    + ".\n Please invoke TransactionalManager.me().addExecutorService() to configure transactional threadPool on application started.");
        }


        FutureTask<Boolean> task = new FutureTask<>(callable);
        threadFactory.newThread(task).start();
        return task;
    }


}
