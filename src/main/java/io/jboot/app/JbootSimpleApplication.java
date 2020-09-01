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
package io.jboot.app;

import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.plugin.IPlugin;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.core.JbootCoreConfig;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/24
 */
public class JbootSimpleApplication {

    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition STOP = LOCK.newCondition();

    public static void main(String[] args) {
        run(args);
    }

    public static void setBootArg(String key, Object value) {
        JbootConfigManager.setBootArg(key, value);
    }

    public static void run(String[] args) {

        JbootApplicationConfig appConfig = ApplicationUtil.getAppConfig(args);
        ApplicationUtil.printBannerInfo(appConfig);
        ApplicationUtil.printApplicationInfo(appConfig);
        ApplicationUtil.printClassPath();

        JbootCoreConfig coreConfig = new JbootCoreConfig();
        new RPCServer(coreConfig).start();
    }


    static class RPCServer extends Thread {

        private final JbootCoreConfig coreConfig;
        private final Plugins plugins = new Plugins();
        private final Interceptors interceptors = new Interceptors();

        public RPCServer(JbootCoreConfig coreConfig) {
            this.coreConfig = coreConfig;
            doInit();
        }

        private void doInit() {
            //aop interceptors
            coreConfig.configInterceptor(interceptors);

            //plugins
            coreConfig.configPlugin(plugins);
            startPlugins();

            //on start
            coreConfig.onStart();
        }

        private void startPlugins() {
            List<IPlugin> pluginList = plugins.getPluginList();
            if (pluginList == null) {
                return;
            }

            for (IPlugin plugin : pluginList) {
                try {
                    if (plugin.start() == false) {
                        String message = "Plugin start error: " + plugin.getClass().getName();
                        throw new RuntimeException(message);
                    }
                } catch (Exception e) {
                    String message = "Plugin start error: " + plugin.getClass().getName() + ". \n" + e.getMessage();
                    throw new RuntimeException(message, e);
                }
            }
        }

        @Override
        public void run() {
            initShutdownHook();
            await();
        }

        private void await() {
            try {
                LOCK.lock();
                STOP.await();
            } catch (InterruptedException e) {
                System.err.println("jboot rpc application has stopped, interrupted by other thread!");
            } finally {
                LOCK.unlock();
            }
        }

        private void initShutdownHook() {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    coreConfig.onStop();
                } catch (Exception e) {
                    System.err.println("jboot rpc stop exception : " + e.toString());
                }

                System.err.println("jboot rpc application exit, all service stopped.");
                try {
                    LOCK.lock();
                    STOP.signal();
                } finally {
                    LOCK.unlock();
                }
            }, "jboot-rpc-application-hook"));
        }
    }

}
