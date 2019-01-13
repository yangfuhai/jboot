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
import io.jboot.utils.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JbootMotanrpc extends JbootrpcBase {


    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;

    private static final Map<String, Object> singletons = new ConcurrentHashMap<>();

    public JbootMotanrpc() {
        registryConfig = new RegistryConfig();
        registryConfig.setCheck(String.valueOf(getConfig().isRegistryCheck()));

        /**
         * 注册中心的调用模式
         */
        if (getConfig().isRegistryCallMode()) {

            registryConfig.setRegProtocol(getConfig().getRegistryType());
            registryConfig.setAddress(getConfig().getRegistryAddress());
            registryConfig.setName(getConfig().getRegistryName());
        }

        /**
         * 直连模式
         */
        else if (getConfig().isDirectCallMode()) {
            registryConfig.setRegProtocol("local");
        }


        protocolConfig = new ProtocolConfig();
        protocolConfig.setId("motan");
        protocolConfig.setName("motan");

        if (StrUtil.isNotBlank(getConfig().getProxy())) {
            protocolConfig.setFilter(getConfig().getProxy());
        }

        if (StrUtil.isNotBlank(getConfig().getSerialization())) {
            protocolConfig.setSerialization(getConfig().getSerialization());
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
        refererConfig.setCheck(String.valueOf(getConfig().isConsumerCheck()));

        initInterface(refererConfig, serviceConfig);

        /**
         * 注册中心模式
         */
        if (getConfig().isRegistryCallMode()) {
            refererConfig.setRegistry(registryConfig);
        }

        /**
         * 直连模式
         */
        else if (getConfig().isDirectCallMode()) {
            if (StrUtil.isBlank(getConfig().getDirectUrl())) {
                throw new JbootIllegalConfigException("directUrl must not be blank if you use direct call mode，please config jboot.rpc.directUrl value");
            }
            refererConfig.setDirectUrl(getConfig().getDirectUrl());
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

            if (StrUtil.isNotBlank(getConfig().getHost())) {
                motanServiceConfig.setHost(getConfig().getHost());
            }


            motanServiceConfig.setShareChannel(true);
            motanServiceConfig.setExport(String.format("motan:%s", serviceConfig.getPort()));
            motanServiceConfig.setCheck(String.valueOf(getConfig().isProviderCheck()));

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

        if (StrUtil.isNotBlank(config.getProxy())) {
            interfaceConfig.setProxy(config.getProxy());
        }

        if (StrUtil.isNotBlank(config.getFilter())) {
            interfaceConfig.setFilter(config.getFilter());
        }
    }


}
