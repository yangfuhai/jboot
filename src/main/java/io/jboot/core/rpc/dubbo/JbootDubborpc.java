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
package io.jboot.core.rpc.dubbo;

import com.alibaba.dubbo.config.*;
import io.jboot.Jboot;
import io.jboot.core.rpc.JbootrpcBase;
import io.jboot.core.rpc.JbootrpcConfig;
import io.jboot.exception.JbootException;
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

        applicationConfig = new ApplicationConfig();
        applicationConfig.setName("jboot");


        /**
         * 注册中心的调用模式
         */
        if (jbootrpcConfig.isRegistryCallMode()) {
            registryConfig = new RegistryConfig();
            registryConfig.setAddress(jbootrpcConfig.getRegistryAddress());
            registryConfig.setUsername(jbootrpcConfig.getRegistryUserName());
            registryConfig.setPassword(jbootrpcConfig.getRegistryPassword());
        }

        /**
         * 直连调用模式
         */
        if (jbootrpcConfig.isRedirectCallMode()) {
            if (StringUtils.isBlank(jbootrpcConfig.getDirectUrl())) {
                throw new JbootException("directUrl must not be null if you use redirect call mode，please config jboot.rpc.directUrl value");
            }
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
        reference.setProxy(jbootrpcConfig.getProxy());
        reference.setFilter("jbootConsumerOpentracing");


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
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(port <= 0 ? jbootrpcConfig.getDefaultPort() : port);
        protocolConfig.setThreads(dubboConfig.getProtocolThreads());

        if (StringUtils.isNotBlank(dubboConfig.getProtocolTransporter())) {
            protocolConfig.setTransporter(dubboConfig.getProtocolTransporter());
        }

        //此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        ServiceConfig<T> service = new ServiceConfig<T>();
        service.setApplication(applicationConfig);

        /**
         * 注册中心的调用模式
         */
        if (jbootrpcConfig.isRegistryCallMode()) {
            service.setRegistry(registryConfig); // 多个注册中心可以用setRegistries()
        }

        service.setProtocol(protocolConfig); // 多个协议可以用setProtocols()
        service.setInterface(interfaceClass);
        service.setRef((T) object);
        service.setVersion(version);
        service.setProxy("jbootProviderOpentracing");
        service.setFilter(jbootrpcConfig.getFilter());

        // 暴露及注册服务
        service.export();

        return true;
    }
}
