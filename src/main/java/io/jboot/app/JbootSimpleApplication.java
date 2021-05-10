/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.core.JFinal;
import com.jfinal.plugin.IPlugin;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.core.JbootCoreConfig;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
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

        long startTimeMillis = System.currentTimeMillis();

        JbootApplicationConfig appConfig = ApplicationUtil.getAppConfig(args);
        ApplicationUtil.printBannerInfo(appConfig);
        ApplicationUtil.printApplicationInfo(appConfig);
        ApplicationUtil.printClassPath();

        JbootCoreConfig coreConfig = new JbootCoreConfig();
        new SimpleServer(coreConfig, startTimeMillis).start();
    }


    static class SimpleServer extends Thread {

        private final JbootCoreConfig coreConfig;
        private final long startTimeMillis;
        private final Plugins plugins = new Plugins();
        private final Interceptors interceptors = new Interceptors();

        public SimpleServer(JbootCoreConfig coreConfig, long startTimeMillis) {
            this.coreConfig = coreConfig;
            this.startTimeMillis = startTimeMillis;

            doInitJFinalPathKit();
            doInitCoreConfig();
        }


        private void doInitJFinalPathKit() {
            try {
                Class<?> c = JbootSimpleApplication.class.getClassLoader().loadClass("com.jfinal.kit.PathKit");
                Method setWebRootPath = c.getMethod("setWebRootPath", String.class);
                String webRootPath = PathKitExt.getWebRootPath();
                setWebRootPath.invoke(null, webRootPath);

                // -------
                Method setRootClassPath = c.getMethod("setRootClassPath", String.class);
                String rootClassPath = PathKitExt.getRootClassPath();
                setRootClassPath.invoke(null, rootClassPath);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        private void doInitCoreConfig() {

            //constants
            coreConfig.configConstant(JFinal.me().getConstants());

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
            String seconds = new DecimalFormat("#.#").format((System.currentTimeMillis() - startTimeMillis) / 1000F);
            System.out.println("JbootApplication has started in " + seconds + " seconds. Welcome To The Jboot World (^_^)\n\n");

            initShutdownHook();
            startAwait();
        }


        private void initShutdownHook() {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nJbootApplication shutdown, please wait ...... ");
                try {
                    coreConfig.onStop();
                } catch (Exception e) {
                    System.out.println("JbootApplication shutdown exception: " + e.toString());
                }
                System.out.println("JbootApplication has exited, all services stopped.");
                try {
                    LOCK.lock();
                    STOP.signal();
                } finally {
                    LOCK.unlock();
                }
            }, "jboot-simple-application-hook"));
        }


        private void startAwait() {
            try {
                LOCK.lock();
                STOP.await();
            } catch (InterruptedException e) {
                System.out.println("JbootApplication has stopped, interrupted by other thread!");
            } finally {
                LOCK.unlock();
            }
        }


    }

}
