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
package io.jboot.components.rpc.motan;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.*;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import io.jboot.components.rpc.JbootrpcBase;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.kits.StringKits;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JbootMotanrpc extends JbootrpcBase {


    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;

    private static final Map<String, Object> singletons = new ConcurrentHashMap<>();

    public JbootMotanrpc() {

        registryConfig = new RegistryConfig();
        registryConfig.setCheck(String.valueOf(getRpcConfig().isRegistryCheck()));

        /**
         * 注册中心的调用模式
         */
        if (getRpcConfig().isRegistryCallMode()) {

            registryConfig.setRegProtocol(getRpcConfig().getRegistryType());
            registryConfig.setAddress(getRpcConfig().getRegistryAddress());
            registryConfig.setName(getRpcConfig().getRegistryName());
        }

        /**
         * 直连模式
         */
        else if (getRpcConfig().isRedirectCallMode()) {
            registryConfig.setRegProtocol("local");
        }


        protocolConfig = new ProtocolConfig();
        protocolConfig.setId("motan");
        protocolConfig.setName("motan");

        if (StringKits.isNotBlank(getRpcConfig().getProxy())) {
            protocolConfig.setFilter(getRpcConfig().getProxy());
        }

        if (StringKits.isNotBlank(getRpcConfig().getSerialization())) {
            protocolConfig.setSerialization(getRpcConfig().getSerialization());
        }

    }


    @Override
    public <T> T serviceObtain(Class<T> serviceClass, JbootrpcServiceConfig serviceConfig) {

        String key = String.format("%s:%s:%s", serviceClass.getName(), serviceConfig.getGroup(), serviceConfig.getVersion());

        T object = (T) singletons.get(key);
        if (object != null) {
            return object;
        }

        RefererConfig<T> refererConfig = new RefererConfig<T>();

        // 设置接口及实现类
        refererConfig.setProtocol(protocolConfig);
        refererConfig.setInterface(serviceClass);
        refererConfig.setCheck(String.valueOf(getRpcConfig().isConsumerCheck()));

        initInterface(refererConfig, serviceConfig);

        /**
         * 注册中心模式
         */
        if (getRpcConfig().isRegistryCallMode()) {
            refererConfig.setRegistry(registryConfig);
        }

        /**
         * 直连模式
         */
        else if (getRpcConfig().isRedirectCallMode()) {
            if (StringKits.isBlank(getRpcConfig().getDirectUrl())) {
                throw new JbootIllegalConfigException("directUrl must not be null if you use redirect call mode，please config jboot.rpc.directUrl value");
            }
            refererConfig.setDirectUrl(getRpcConfig().getDirectUrl());
        }


        object = refererConfig.getRef();

        if (object != null) {
            singletons.put(key, object);
        }
        return object;
    }


    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, JbootrpcServiceConfig serviceConfig) {

        synchronized (this) {

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);

            ServiceConfig<T> motanServiceConfig = new ServiceConfig<T>();
            motanServiceConfig.setRegistry(registryConfig);
            motanServiceConfig.setProtocol(protocolConfig);

            // 设置接口及实现类
            motanServiceConfig.setInterface(interfaceClass);
            motanServiceConfig.setRef((T) object);

            if (StringKits.isNotBlank(getRpcConfig().getHost())) {
                motanServiceConfig.setHost(getRpcConfig().getHost());
            }


            motanServiceConfig.setShareChannel(true);
            motanServiceConfig.setExport(String.format("motan:%s", serviceConfig.getPort()));
            motanServiceConfig.setCheck(String.valueOf(getRpcConfig().isProviderCheck()));

            initInterface(motanServiceConfig, serviceConfig);


            motanServiceConfig.export();

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
        }

        return true;
    }


    private static void initInterface(AbstractInterfaceConfig interfaceConfig, JbootrpcServiceConfig config) {

        interfaceConfig.setGroup(config.getGroup());
        interfaceConfig.setVersion(config.getVersion());
        interfaceConfig.setRequestTimeout(config.getTimeout());


        if (config.getActives() != null) {
            interfaceConfig.setActives(config.getActives());
        }

        if (config.getAsync() != null) {
            interfaceConfig.setAsync(config.getAsync());
        }

        if (config.getRetries() != null) {
            interfaceConfig.setRetries(config.getRetries());
        }

        if (config.getCheck() != null) {
            interfaceConfig.setCheck(config.getCheck().toString());
        }

        if (StringKits.isNotBlank(config.getProxy())) {
            interfaceConfig.setProxy(config.getProxy());
        }

        if (StringKits.isNotBlank(config.getFilter())) {
            interfaceConfig.setFilter(config.getFilter());
        }
    }


}
