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

import io.jboot.app.config.JbootConfigManager;
import io.jboot.core.JbootCoreConfig;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/24
 */
public class JbootRpcApplication {

    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition STOP = LOCK.newCondition();

    public static void main(String[] args) {
        run(args);
    }

    public static void setBootArg(String key, Object value) {
        JbootConfigManager.me().setBootArg(key, value);
    }

    public static void run(String[] args) {
        JbootConfigManager.me().parseArgs(args);
        JbootCoreConfig coreConfig = new JbootCoreConfig();
        new RPCServer(coreConfig).start();
    }



    static class RPCServer extends Thread {

        private JbootCoreConfig coreConfig;

        public RPCServer(JbootCoreConfig coreConfig) {
            this.coreConfig = coreConfig;
            doInit();
        }

        private void doInit(){
            this.coreConfig.onStart();
        }

        @Override
        public void run() {
            addHook(coreConfig);
            await();
        }

        private  void await() {
            try {
                LOCK.lock();
                STOP.await();
            } catch (InterruptedException e) {
                System.err.println("jboot rpc application has stopped, interrupted by other thread!");
            } finally {
                LOCK.unlock();
            }
        }

        private void addHook(JbootCoreConfig config) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    config.onStop();
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
