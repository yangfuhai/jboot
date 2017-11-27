/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.core.rpc.JbootrpcBase;


public class JbootZbusrpc extends JbootrpcBase {

    JbootServiceBootstrap serviceBootstrap;
    JbootClientBootstrap clientBootstrap;

    public JbootZbusrpc() {
        serviceBootstrap = new JbootServiceBootstrap();
        clientBootstrap = new JbootClientBootstrap();
        clientBootstrap.serviceAddress(getConfig().getRegistryAddress());
    }

    @Override
    public <T> T serviceObtain(Class<T> serviceClass, String group, String version) {
        return clientBootstrap.serviceObtain(serviceClass, group, version);
    }

    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, String group, String version, int port) {
        serviceBootstrap.addModule(interfaceClass, object, group, version);
        return true;
    }

    @Override
    public void onInited() {
        try {
            String[] hostPort = getConfig().getRegistryAddress().split(":");

            serviceBootstrap.host(hostPort[0]);
            serviceBootstrap.port(Integer.valueOf(hostPort[1]));
            serviceBootstrap.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
