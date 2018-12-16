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
package io.jboot.core.rpc.zbus;

import io.jboot.Jboot;
import io.jboot.core.rpc.JbootrpcBase;
import io.jboot.core.rpc.JbootrpcServiceConfig;
import io.jboot.kits.StrUtils;


public class JbootZbusrpc extends JbootrpcBase {

    JbootServiceBootstrap serviceBootstrap;
    JbootClientBootstrap clientBootstrap;

    JbootZbusRpcConfig zbusConfig = Jboot.config(JbootZbusRpcConfig.class);

    public JbootZbusrpc() {
        if (StrUtils.isBlank(zbusConfig.getServiceName())) {
            throw new NullPointerException("please config jboot.rpc.zbus.serviceName in your properties.");
        }

        serviceBootstrap = new JbootServiceBootstrap();
        clientBootstrap = new JbootClientBootstrap();
        clientBootstrap.serviceAddress(getRpcConfig().getRegistryAddress());
        clientBootstrap.serviceName(zbusConfig.getServiceName());
        if (StrUtils.isNotBlank(zbusConfig.getServiceToken())) {
            clientBootstrap.serviceToken(zbusConfig.getServiceToken());
        }

    }

    @Override
    public <T> T serviceObtain(Class<T> serviceClass, JbootrpcServiceConfig serviceConfig) {
        String group = StrUtils.isBlank(serviceConfig.getGroup()) ? getRpcConfig().getDefaultGroup() : serviceConfig.getGroup();
        return clientBootstrap.serviceObtain(serviceClass, group, serviceConfig.getVersion());
    }

    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, JbootrpcServiceConfig serviceConfig) {
        serviceBootstrap.addModule(interfaceClass, object, serviceConfig.getGroup(), serviceConfig.getVersion());
        return true;
    }

    @Override
    public void onInited() {
        try {
            serviceBootstrap.serviceAddress(getRpcConfig().getRegistryAddress());
            serviceBootstrap.serviceName(zbusConfig.getServiceName());
            if (StrUtils.isNotBlank(zbusConfig.getServiceToken())) {
                serviceBootstrap.serviceToken(zbusConfig.getServiceToken());
            }
            serviceBootstrap.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
