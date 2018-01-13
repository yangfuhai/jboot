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
package io.jboot.core.rpc;

import io.jboot.config.annotation.PropertyConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@PropertyConfig(prefix = "jboot.rpc")
public class JbootrpcConfig {

    public static final String TYPE_DUBBO = "dubbo";
    public static final String TYPE_GRPC = "grpc";
    public static final String TYPE_MOTAN = "motan";
    public static final String TYPE_THRIFT = "thrift";
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_ZBUS = "zbus";

    public static final String REGISTRY_TYPE_CONSUL = "consul";
    public static final String REGISTRY_TYPE_ZOOKEEPER = "zookeeper";

    private String type = TYPE_LOCAL;
    private int requestTimeOut = 5000;

    /**
     * RPC的调用模式：registry 注册中心，redirect直连模式
     */
    public static final String CALL_MODE_REGISTRY = "registry";
    public static final String CALL_MODE_REDIRECT = "redirect";
    private String callMode = CALL_MODE_REGISTRY;


    /**
     * 注册中心的相关调用
     */
    private String registryType = REGISTRY_TYPE_CONSUL;
    private String registryAddress = "127.0.0.1:8500";
    private String registryName = "jboot";
    private String registryUserName;
    private String registryPassword;

    /**
     * 启动检查
     */
    private boolean registryCheck = false;
    private boolean consumerCheck = false;
    private boolean providerCheck = false;


    /**
     * 直连模式的时候，配置的url
     */
    private String directUrl;


    /**
     * 对外暴露服务的相关配置
     */
    private String host;
    private int defaultPort = 8088;
    private String defaultGroup = "jboot";
    private String defaultVersion = "1.0";

    private String proxy;
    private String filter;  //多个过滤器请用英文逗号（,）隔开，默认添加opentracing过滤器，用于对rpc分布式调用的追踪
    private String serialization;


    /**
     * RPC Hystrix 相关的配置
     */
    // keys 的值为  key1:method1,method2;key2:method3,method4
    private boolean hystrixEnable = true;
    private int hystrixTimeout = 1000 * 3; //单位：毫秒
    private String hystrixKeys;
    private boolean hystrixAutoConfig = true;
    private String hystrixFallbackListener = JbootrpcHystrixFallbackListenerDefault.class.getName();

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRequestTimeOut() {
        return requestTimeOut;
    }

    public void setRequestTimeOut(int requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public String getDefaultVersion() {
        return defaultVersion;
    }

    public void setDefaultVersion(String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    public String getRegistryUserName() {
        return registryUserName;
    }

    public void setRegistryUserName(String registryUserName) {
        this.registryUserName = registryUserName;
    }

    public String getRegistryPassword() {
        return registryPassword;
    }

    public void setRegistryPassword(String registryPassword) {
        this.registryPassword = registryPassword;
    }

    public String getCallMode() {
        return callMode;
    }

    public void setCallMode(String callMode) {
        this.callMode = callMode;
    }

    public String getDirectUrl() {
        return directUrl;
    }

    public void setDirectUrl(String directUrl) {
        this.directUrl = directUrl;
    }

    public boolean isRedirectCallMode() {
        return CALL_MODE_REDIRECT.equals(getCallMode());
    }

    public boolean isRegistryCallMode() {
        return CALL_MODE_REGISTRY.equals(getCallMode());
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getHystrixKeys() {
        return hystrixKeys;
    }

    public void setHystrixKeys(String hystrixKeys) {
        this.hystrixKeys = hystrixKeys;
    }

    public boolean isHystrixEnable() {
        return hystrixEnable;
    }

    public void setHystrixEnable(boolean hystrixEnable) {
        this.hystrixEnable = hystrixEnable;
    }

    public int getHystrixTimeout() {
        return hystrixTimeout;
    }

    public void setHystrixTimeout(int hystrixTimeout) {
        this.hystrixTimeout = hystrixTimeout;
    }

    public boolean isHystrixAutoConfig() {
        return hystrixAutoConfig;
    }

    public void setHystrixAutoConfig(boolean hystrixAutoConfig) {
        this.hystrixAutoConfig = hystrixAutoConfig;
    }

    public String getHystrixFallbackListener() {
        return hystrixFallbackListener;
    }

    public void setHystrixFallbackListener(String hystrixFallbackListener) {
        this.hystrixFallbackListener = hystrixFallbackListener;
    }

    public boolean isRegistryCheck() {
        return registryCheck;
    }

    public void setRegistryCheck(boolean registryCheck) {
        this.registryCheck = registryCheck;
    }

    public boolean isConsumerCheck() {
        return consumerCheck;
    }

    public void setConsumerCheck(boolean consumerCheck) {
        this.consumerCheck = consumerCheck;
    }

    public boolean isProviderCheck() {
        return providerCheck;
    }

    public void setProviderCheck(boolean providerCheck) {
        this.providerCheck = providerCheck;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    private Map<String, String> methodKeyMapping = new ConcurrentHashMap<>();

    public String getHystrixKeyByMethod(String method) {
        if (hystrixKeys != null && methodKeyMapping.isEmpty()) {
            initMapping();
        }

        return methodKeyMapping.get(method);
    }

    private void initMapping() {
        String keyMethodStrings[] = hystrixKeys.split(";");
        for (String keyMethodString : keyMethodStrings) {
            String[] keyMethod = keyMethodString.split(":");
            if (keyMethod.length != 2) continue;

            String key = keyMethod[0];
            String[] methods = keyMethod[1].split(",");
            for (String method : methods) {
                methodKeyMapping.put(method, key);
            }
        }
    }
}
