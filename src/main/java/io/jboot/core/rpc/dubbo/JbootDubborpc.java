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
package io.jboot.core.rpc.dubbo;

import com.alibaba.dubbo.config.*;
import io.jboot.Jboot;
import io.jboot.core.rpc.JbootrpcBase;
import io.jboot.core.rpc.JbootrpcConfig;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dubbo 官方宣布开始维护了
 * 开始添加dubbo的支持
 */
public class JbootDubborpc extends JbootrpcBase {

    private static final Map<String, Object> singletons = new ConcurrentHashMap<>();

    private JbootrpcConfig jbootrpcConfig;
    private ApplicationConfig applicationConfig;
    private RegistryConfig registryConfig;
    private JbootDubborpcConfig dubboConfig;

    public JbootDubborpc() {
        jbootrpcConfig = Jboot.config(JbootrpcConfig.class);
        dubboConfig = Jboot.config(JbootDubborpcConfig.class);

        if (StringUtils.isNotBlank(dubboConfig.getQosPort())) {
            System.setProperty("dubbo.qos.port", String.valueOf(dubboConfig.getQosPort()));
        }

        applicationConfig = new ApplicationConfig();
        applicationConfig.setName("jboot");

        registryConfig = new RegistryConfig();
        registryConfig.setCheck(jbootrpcConfig.isRegistryCheck());

        /**
         * 注册中心的调用模式
         */
        if (jbootrpcConfig.isRegistryCallMode()) {

            registryConfig.setProtocol(jbootrpcConfig.getRegistryType());
            registryConfig.setAddress(jbootrpcConfig.getRegistryAddress());
            registryConfig.setUsername(jbootrpcConfig.getRegistryUserName());
            registryConfig.setPassword(jbootrpcConfig.getRegistryPassword());
        }
        /**
         * 直连模式
         */
        else if (jbootrpcConfig.isRedirectCallMode()) {
            registryConfig.setAddress(RegistryConfig.NO_AVAILABLE);
        }


    }


    @Override
    public <T> T serviceObtain(Class<T> serviceClass, String group, String version) {

        String key = String.format("%s:%s:%s", serviceClass.getName(), group, version);

        T object = (T) singletons.get(key);
        if (object != null) {
            return object;
        }


        // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
        // 引用远程服务
        // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        ReferenceConfig<T> reference = new ReferenceConfig<T>();
        reference.setApplication(applicationConfig);
        reference.setInterface(serviceClass);
        reference.setVersion(version);

        if (StringUtils.isNotBlank(jbootrpcConfig.getProxy())) {
            reference.setProxy(jbootrpcConfig.getProxy());
        } else {
            //设置 jboot 代理，目的是为了方便 Hystrix 的降级控制和统计
            reference.setProxy("jboot");
        }

        if (StringUtils.isNotBlank(jbootrpcConfig.getFilter())) {
            reference.setFilter(jbootrpcConfig.getFilter());
        } else {
            //默认情况下用于 OpenTracing 的追踪
            reference.setFilter("jbootConsumerOpentracing");
        }

        reference.setCheck(jbootrpcConfig.isConsumerCheck());


        /**
         * 注册中心的调用模式
         */
        if (jbootrpcConfig.isRegistryCallMode()) {
            reference.setRegistry(registryConfig); // 多个注册中心可以用setRegistries()
        }

        /**
         * 直连调用模式
         */
        else if (jbootrpcConfig.isRedirectCallMode()) {
            if (StringUtils.isBlank(jbootrpcConfig.getDirectUrl())) {
                throw new JbootIllegalConfigException("directUrl must not be null if you use redirect call mode，please config jboot.rpc.directUrl value");
            }
            reference.setUrl(jbootrpcConfig.getDirectUrl());
        }

        // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
        object = reference.get();

        if (object != null) {
            singletons.put(key, object);
        }
        return object;
    }

    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, String group, String version, int port) {

        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setPort(port <= 0 ? jbootrpcConfig.getDefaultPort() : port);
        protocolConfig.setThreads(dubboConfig.getProtocolThreads());

        protocolConfig.setName(dubboConfig.getProtocolName());
        protocolConfig.setServer(dubboConfig.getProtocolServer());

        if (StringUtils.isNotBlank(dubboConfig.getProtocolContextPath())) {
            protocolConfig.setContextpath(dubboConfig.getProtocolContextPath());
        }

        if (StringUtils.isNotBlank(dubboConfig.getProtocolTransporter())) {
            protocolConfig.setTransporter(dubboConfig.getProtocolTransporter());
        }

        if (StringUtils.isNotBlank(jbootrpcConfig.getHost())) {
            protocolConfig.setHost(jbootrpcConfig.getHost());
        }

        if (StringUtils.isNotBlank(jbootrpcConfig.getSerialization())) {
            protocolConfig.setSerialization(jbootrpcConfig.getSerialization());
        }


        //此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        ServiceConfig<T> service = new ServiceConfig<T>();
        service.setApplication(applicationConfig);

        service.setRegistry(registryConfig); // 多个注册中心可以用setRegistries()

        service.setProtocol(protocolConfig); // 多个协议可以用setProtocols()
        service.setInterface(interfaceClass);
        service.setRef((T) object);
        service.setVersion(version);
        service.setProxy(jbootrpcConfig.getProxy());
        service.setFilter("jbootProviderOpentracing");


        // 暴露及注册服务
        service.export();

        return true;
    }
}
