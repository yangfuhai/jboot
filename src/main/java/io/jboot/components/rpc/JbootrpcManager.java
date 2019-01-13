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
package io.jboot.components.rpc;

import com.jfinal.aop.Aop;
import io.jboot.Jboot;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.mq.JbootmqMessageListener;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.components.rpc.dubbo.JbootDubborpc;
import io.jboot.components.rpc.local.JbootLocalrpc;
import io.jboot.components.rpc.motan.JbootMotanrpc;
import io.jboot.components.rpc.zbus.JbootZbusrpc;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootException;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.ClassScanner;

import java.io.Serializable;
import java.util.List;


public class JbootrpcManager {

    private static JbootrpcManager manager = new JbootrpcManager();

    public static JbootrpcManager me() {
        return manager;
    }

    private Jbootrpc jbootrpc;
    private JbootrpcConfig config = Jboot.config(JbootrpcConfig.class);
    private boolean inited = false;

    public Jbootrpc getJbootrpc() {
        if (jbootrpc == null) {
            jbootrpc = createJbootrpc();
        }
        return jbootrpc;
    }

    private static Class[] default_excludes = new Class[]{
            JbootEventListener.class,
            JbootmqMessageListener.class,
            Serializable.class
    };


    public void init() {

        getJbootrpc().onInitBefore();

        if (config.isAutoExportEnable()) {
            autoExportRPCBean();
        }

        getJbootrpc().onInited();
    }

    private void autoExportRPCBean() {
        List<Class> classes = ClassScanner.scanClassByAnnotation(RPCBean.class, true);
        if (ArrayUtil.isNullOrEmpty(classes)) {
            return;
        }

        for (Class clazz : classes) {
            RPCBean rpcBean = (RPCBean) clazz.getAnnotation(RPCBean.class);
            Class[] inters = clazz.getInterfaces();
            if (inters == null || inters.length == 0) {
                throw new JbootException(String.format("class[%s] has no interface, can not use @RPCBean", clazz));
            }

            //对某些系统的类 进行排除，例如：Serializable 等
            Class[] excludes = ArrayUtil.concat(default_excludes, rpcBean.exclude());
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
                getJbootrpc().serviceExport(inter, Aop.get(clazz), new JbootrpcServiceConfig(rpcBean));
            }
        }
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


}
