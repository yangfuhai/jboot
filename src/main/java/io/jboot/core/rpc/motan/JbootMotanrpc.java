/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.core.rpc.motan;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.*;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import io.jboot.Jboot;
import io.jboot.core.rpc.JbootrpcBase;
import io.jboot.core.rpc.JbootrpcConfig;
import io.jboot.exception.JbootException;
import io.jboot.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JbootMotanrpc extends JbootrpcBase {


    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private JbootrpcConfig jbootrpcConfig;

    private static final Map<String, Object> singletons = new ConcurrentHashMap<>();

    public JbootMotanrpc() {

        jbootrpcConfig = Jboot.config(JbootrpcConfig.class);


        /**
         * 注册中心的调用模式
         */
        if (jbootrpcConfig.isRegistryCallMode()) {
            registryConfig = new RegistryConfig();
            registryConfig.setRegProtocol(jbootrpcConfig.getRegistryType());
            registryConfig.setAddress(jbootrpcConfig.getRegistryAddress());
            registryConfig.setName(jbootrpcConfig.getRegistryName());
        }


        /**
         * 直连调用模式
         */
        if (jbootrpcConfig.isRedirectCallMode()) {
            if (StringUtils.isBlank(jbootrpcConfig.getDirectUrl())) {
                throw new JbootException("directUrl must not be null if you use redirect call mode，please config jboot.rpc.directUrl value");
            }
        }


        protocolConfig = new ProtocolConfig();
        protocolConfig.setId("motan");
        protocolConfig.setName("motan");
        protocolConfig.setSerialization("jboot");
        protocolConfig.setFilter("jbootOpentracing");

    }


    @Override
    public <T> T serviceObtain(Class<T> serviceClass, String group, String version) {

        String key = String.format("%s:%s:%s", serviceClass.getName(), group, version);

        T object = (T) singletons.get(key);
        if (object != null) {
            return object;
        }

        RefererConfig<T> refererConfig = new RefererConfig<T>();

        // 设置接口及实现类
        refererConfig.setInterface(serviceClass);

        // 配置服务的group以及版本号
        refererConfig.setGroup(group);
        refererConfig.setVersion(version);
        refererConfig.setRequestTimeout(jbootrpcConfig.getRequestTimeOut());
        refererConfig.setProtocol(protocolConfig);
        refererConfig.setProxy(jbootrpcConfig.getProxy());
        refererConfig.setFilter("jbootOpentracing");

        /**
         * 注册中心模式
         */
        if (jbootrpcConfig.isRegistryCallMode()) {
            refererConfig.setRegistry(registryConfig);
        }
        /**
         * 直连模式
         */
        else if (jbootrpcConfig.isRedirectCallMode()) {
            refererConfig.setDirectUrl(jbootrpcConfig.getDirectUrl());
        }

        object = refererConfig.getRef();

        if (object != null) {
            singletons.put(key, object);
        }
        return object;
    }


    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, String group, String version, int port) {

        synchronized (this) {

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);

            ServiceConfig<T> motanServiceConfig = new ServiceConfig<T>();
            if (jbootrpcConfig.isRegistryCallMode()) {
                motanServiceConfig.setRegistry(registryConfig);
            }

            motanServiceConfig.setProtocol(protocolConfig);

            // 设置接口及实现类
            motanServiceConfig.setInterface(interfaceClass);
            motanServiceConfig.setRef((T) object);

            // 配置服务的group以及版本号
            motanServiceConfig.setGroup(group);
            motanServiceConfig.setVersion(version);
            motanServiceConfig.setFilter("jbootOpentracing");

            motanServiceConfig.setShareChannel(true);
            motanServiceConfig.setExport(String.format("motan:%s", port));
            motanServiceConfig.export();

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
        }

        return true;
    }


}
