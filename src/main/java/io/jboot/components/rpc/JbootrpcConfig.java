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
package io.jboot.components.rpc;

import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.utils.StrUtil;

import java.io.IOException;
import java.net.ServerSocket;


@ConfigModel(prefix = "jboot.rpc")
public class JbootrpcConfig {

    public static final String TYPE_DUBBO = "dubbo";
    public static final String TYPE_GRPC = "grpc";
    public static final String TYPE_MOTAN = "motan";
    public static final String TYPE_THRIFT = "thrift";
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_ZBUS = "zbus";

    public static final String REGISTRY_TYPE_CONSUL = "consul";
    public static final String REGISTRY_TYPE_ZOOKEEPER = "zookeeper";


    /**
     * RPC的调用模式：registry 注册中心，direct直连模式
     */
    public static final String CALL_MODE_REGISTRY = "registry";
    public static final String CALL_MODE_DIRECT = "direct";


    private String type;
    private String callMode = CALL_MODE_REGISTRY;

    private int requestTimeOut = 5000;


    /**
     * 注册中心的相关调用
     */
    private String registryType = REGISTRY_TYPE_CONSUL;
    private String registryAddress = "127.0.0.1:8500";
    private String registryName = "jboot";
    private String registryUserName;
    private String registryPassword;
    private String registryFile;

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
    private String host; //当有多个IP的时候可以指定某个IP
    private Integer defaultPort = 0; //0 为随机可用端口
    private String defaultGroup = "jboot";
    private String defaultVersion = "1.0";

    private String proxy;

    //多个过滤器请用英文逗号（,）隔开，默认添加opentracing过滤器，用于对rpc分布式调用的追踪
    private String filter;

    private String serialization;

    //重试次数，不配置默认使用框架默认配置 motan和dubbo可能不一样
    private Integer retries;

    //本地自动暴露 @RPCBean 的service
    private boolean autoExportEnable = true;


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

    public Integer getDefaultPort() {
        if (defaultPort == 0) {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(0); //.getLocalPort();
                return serverSocket.getLocalPort();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

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

    public String getRegistryFile() {
        return registryFile;
    }

    public void setRegistryFile(String registryFile) {
        this.registryFile = registryFile;
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
        if (directUrl != null && directUrl.contains(":")) {
            this.defaultPort = Integer.valueOf(directUrl.split(":")[1]);
        }
        this.directUrl = directUrl;
    }

    public boolean isDirectCallMode() {
        return CALL_MODE_DIRECT.equals(getCallMode());
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

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public boolean isAutoExportEnable() {
        return autoExportEnable;
    }

    public void setAutoExportEnable(boolean autoExportEnable) {
        this.autoExportEnable = autoExportEnable;
    }

    public boolean isConfigOk() {
        return StrUtil.isNotBlank(getType());
    }
}
