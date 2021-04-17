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
package io.jboot.components.rpc.dubbo;

import io.jboot.components.rpc.JbootrpcBase;
import io.jboot.components.rpc.JbootrpcReferenceConfig;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.utils.StrUtil;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.lang.reflect.Method;

public class JbootDubborpc extends JbootrpcBase {

    @Override
    public void onStart() {
        DubboUtil.initDubbo();
    }

    @Override
    public void onStop() {
        DubboUtil.stopDubbo();
    }

    @Override
    public <T> T onServiceCreate(Class<T> interfaceClass, JbootrpcReferenceConfig config) {
        ReferenceConfig<T> reference = DubboUtil.toReferenceConfig(config);
        reference.setInterface(interfaceClass);

        String directUrl = rpcConfig.getUrl(interfaceClass.getName());
        if (StrUtil.isNotBlank(directUrl)) {
            reference.setUrl(directUrl);
        }

        String consumer = rpcConfig.getConsumer(interfaceClass.getName());
        if (consumer != null) {
            reference.setConsumer(DubboUtil.getConsumer(consumer));
        }

        //copy consumer config to Refercence Config
        copyDefaultConsumerConfig(reference);


        if (reference.getGroup() == null) {
            reference.setGroup(rpcConfig.getGroup(interfaceClass.getName()));
        }

        if (reference.getVersion() == null) {
            reference.setVersion(rpcConfig.getVersion(interfaceClass.getName()));
        }


        return reference.get();
    }

    private <T> void copyDefaultConsumerConfig(ReferenceConfig<T> reference) {
        ConsumerConfig defaultConfig = reference.getConsumer();
        if (defaultConfig == null) {
            return;
        }

        Method[] consumeMethods = ConsumerConfig.class.getMethods();
        for (Method method : consumeMethods) {
            if (method.getName().startsWith("get")) {
                Class<?> returnType = method.getReturnType();
                try {
                    String settterMethodName = "set" + method.getName().substring(3);
                    Method referSetterMethod = ReferenceConfig.class.getMethod(settterMethodName, returnType);
                    Object data = method.invoke(defaultConfig);
                    if (data != null) {
                        referSetterMethod.invoke(reference, data);
                    }
                } catch (Exception e) {
                    // doNothing
                }
            }
        }
    }


    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, JbootrpcServiceConfig config) {
        ServiceConfig<T> service = DubboUtil.toServiceConfig(config);
        service.setInterface(interfaceClass);
        service.setRef((T) object);

        String provider = rpcConfig.getProvider(interfaceClass.getName());
        if (provider != null) {
            service.setProvider(DubboUtil.getProvider(provider));
        }

        if (service.getGroup() == null) {
            service.setGroup(rpcConfig.getGroup(interfaceClass.getName()));
        }

        if (service.getVersion() == null) {
            service.setVersion(rpcConfig.getVersion(interfaceClass.getName()));
        }

        service.export();
        return true;
    }

}
