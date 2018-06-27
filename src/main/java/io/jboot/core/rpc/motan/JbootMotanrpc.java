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
package io.jboot.core.rpc.motan;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import io.jboot.core.rpc.JbootrpcBase;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StringUtils;

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

        if (StringUtils.isNotBlank(getRpcConfig().getProxy())) {
            protocolConfig.setFilter(getRpcConfig().getProxy());
        } else {
            protocolConfig.setFilter("jbootHystrix,jbootOpentracing");
        }


        if (StringUtils.isNotBlank(getRpcConfig().getSerialization())) {
            protocolConfig.setSerialization(getRpcConfig().getSerialization());
        }

    }


    @Override
    public <T> T serviceObtain(Class<T> serviceClass, String group, String version) {

        if (StringUtils.isBlank(group)) {
            group = getRpcConfig().getDefaultGroup();
        }

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
        refererConfig.setRequestTimeout(getRpcConfig().getRequestTimeOut());
        refererConfig.setProtocol(protocolConfig);

        if (StringUtils.isNotBlank(getRpcConfig().getProxy())) {
            refererConfig.setProxy(getRpcConfig().getProxy());
        } else {
            refererConfig.setProxy("jboot");
        }


        refererConfig.setCheck(String.valueOf(getRpcConfig().isConsumerCheck()));

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
            if (StringUtils.isBlank(getRpcConfig().getDirectUrl())) {
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
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, String group, String version, int port) {

        if (StringUtils.isBlank(group)) {
            group = getRpcConfig().getDefaultGroup();
        }

        synchronized (this) {

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);

            ServiceConfig<T> motanServiceConfig = new ServiceConfig<T>();
            motanServiceConfig.setRegistry(registryConfig);

            motanServiceConfig.setProtocol(protocolConfig);

            // 设置接口及实现类
            motanServiceConfig.setInterface(interfaceClass);
            motanServiceConfig.setRef((T) object);

            // 配置服务的group以及版本号
            if (StringUtils.isNotBlank(getRpcConfig().getHost())) {
                motanServiceConfig.setHost(getRpcConfig().getHost());
            }
            motanServiceConfig.setGroup(group);
            motanServiceConfig.setVersion(version);

            motanServiceConfig.setShareChannel(true);
            motanServiceConfig.setExport(String.format("motan:%s", port));
            motanServiceConfig.setCheck(String.valueOf(getRpcConfig().isProviderCheck()));


            motanServiceConfig.export();


            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
        }

        return true;
    }


}
