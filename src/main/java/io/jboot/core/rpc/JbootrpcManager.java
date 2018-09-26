/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.rpc;

import io.jboot.Jboot;
import io.jboot.core.mq.JbootmqMessageListener;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.core.rpc.dubbo.JbootDubborpc;
import io.jboot.core.rpc.local.JbootLocalrpc;
import io.jboot.core.rpc.motan.JbootMotanrpc;
import io.jboot.core.rpc.zbus.JbootZbusrpc;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.event.JbootEventListener;
import io.jboot.exception.JbootException;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassKits;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StrUtils;

import java.io.Serializable;
import java.util.List;


public class JbootrpcManager {

    private static JbootrpcManager manager = new JbootrpcManager();


    public static JbootrpcManager me() {
        return manager;
    }


    private Jbootrpc jbootrpc;
    private JbootrpcConfig config = Jboot.config(JbootrpcConfig.class);


    public Jbootrpc getJbootrpc() {
        if (jbootrpc == null) {
            jbootrpc = createJbootrpc();
        }
        return jbootrpc;
    }

    static Class[] default_excludes = new Class[]{JbootEventListener.class, JbootmqMessageListener.class, Serializable.class};


    public void init() {

        getJbootrpc().onInitBefore();

        List<Class> classes = ClassScanner.scanClass(true);
        if (ArrayUtils.isNullOrEmpty(classes)) {
            return;
        }

        for (Class clazz : classes) {
            JbootrpcService rpcService = (JbootrpcService) clazz.getAnnotation(JbootrpcService.class);
            if (rpcService == null) {
                continue;
            }

            Class[] inters = clazz.getInterfaces();
            if (inters == null || inters.length == 0) {
                throw new JbootException(String.format("class[%s] has no interface, can not use @JbootrpcService", clazz));
            }

            //对某些系统的类 进行排除，例如：Serializable 等
            Class[] excludes = ArrayUtils.concat(default_excludes, rpcService.exclude());
            for (Class inter : inters) {
                boolean isContinue = false;
                for (Class ex : excludes) {
                    if (ex.isAssignableFrom(inter)) {
                        isContinue = true;
                        break;
                    }
                }
                if (isContinue) {
                    continue;
                }
                getJbootrpc().serviceExport(inter, Jboot.bean(clazz), new JbootrpcServiceConfig(rpcService));
            }
        }

        getJbootrpc().onInited();
    }


    private Jbootrpc createJbootrpc() {

        switch (config.getType()) {
            case JbootrpcConfig.TYPE_MOTAN:
                return new JbootMotanrpc();
            case JbootrpcConfig.TYPE_LOCAL:
                return new JbootLocalrpc();
            case JbootrpcConfig.TYPE_DUBBO:
                return new JbootDubborpc();
            case JbootrpcConfig.TYPE_ZBUS:
                return new JbootZbusrpc();
            default:
                return JbootSpiLoader.load(Jbootrpc.class, config.getType());
        }
    }

    private JbootrpcHystrixFallbackListener fallbackListener = null;

    public JbootrpcHystrixFallbackListener getHystrixFallbackListener() {

        if (fallbackListener != null) {
            return fallbackListener;
        }

        if (!StrUtils.isBlank(config.getHystrixFallbackListener())) {
            fallbackListener = ClassKits.newInstance(config.getHystrixFallbackListener());

        }

        if (fallbackListener == null) {
            fallbackListener = new JbootrpcHystrixFallbackListenerDefault();
        }

        return fallbackListener;
    }

}
