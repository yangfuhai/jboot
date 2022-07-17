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
package io.jboot.components.rpc;


import io.jboot.Jboot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JbootrpcBase implements Jbootrpc {

    protected static final Map<String, Object> serviceCache = new ConcurrentHashMap<>();
    protected static JbootrpcConfig rpcConfig = Jboot.config(JbootrpcConfig.class);
    private boolean started = false;


    @Override
    public <T> T serviceObtain(Class<T> interfaceClass, JbootrpcReferenceConfig config) {
        String key = buildKey(interfaceClass, config);
        T service = (T) serviceCache.get(key);
        if (service == null) {
            synchronized (this) {
                service = (T) serviceCache.get(key);
                if (service == null) {
                    // onStart 方法是在 app 启动完成后，Jboot 主动去调用的
                    // 但是，在某些场景可能存在没有等 app 启动完成就去获取 Service 的情况
                    // 此时，需要主动先调用下 onStart 方法
                    invokeOnStartIfNecessary();

                    service = onServiceCreate(interfaceClass, config);
                    if (service != null) {
                        serviceCache.put(key, service);
                    }
                }
            }
        }
        return service;
    }

    protected String buildKey(Class<?> interfaceClass, JbootrpcReferenceConfig config) {
        return interfaceClass.getName() + "@" + config.hashCode();
    }

    protected synchronized void invokeOnStartIfNecessary() {
        if (!started) {
            onStart();
            setStarted(true);
        }
    }


    public abstract <T> T onServiceCreate(Class<T> serviceClass, JbootrpcReferenceConfig config);

    @Override
    public void onStart() {

    }


    @Override
    public void onStop() {

    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
