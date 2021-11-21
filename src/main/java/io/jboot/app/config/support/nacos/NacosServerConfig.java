/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.app.config.support.nacos;


import io.jboot.app.config.JbootConfigKit;
import io.jboot.app.config.annotation.ConfigModel;

import java.util.Properties;

/**
 * @see com.alibaba.nacos.api.PropertyKeyConst
 */
@ConfigModel(prefix = "jboot.config.nacos")
public class NacosServerConfig {

    private boolean enable = false;

    private String isUseCloudNamespaceParsing;
    private String isUseEndpointParsingRule;
    private String endpoint;
    private String endpointPort;
    private String namespace;
    private String username;
    private String password;
    private String accessKey;
    private String secretKey;
    private String ramRoleName;
    private String serverAddr;
    private String contextPath;
    private String clusterName;
    private String encode;
    private String configLongPollTimeout;
    private String configRetryTime;
    private String maxRetry;
    private String enableRemoteSyncConfig;

    private String dataId;
    private String group;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getIsUseCloudNamespaceParsing() {
        return isUseCloudNamespaceParsing;
    }

    public void setIsUseCloudNamespaceParsing(String isUseCloudNamespaceParsing) {
        this.isUseCloudNamespaceParsing = isUseCloudNamespaceParsing;
    }

    public String getIsUseEndpointParsingRule() {
        return isUseEndpointParsingRule;
    }

    public void setIsUseEndpointParsingRule(String isUseEndpointParsingRule) {
        this.isUseEndpointParsingRule = isUseEndpointParsingRule;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpointPort() {
        return endpointPort;
    }

    public void setEndpointPort(String endpointPort) {
        this.endpointPort = endpointPort;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRamRoleName() {
        return ramRoleName;
    }

    public void setRamRoleName(String ramRoleName) {
        this.ramRoleName = ramRoleName;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getConfigLongPollTimeout() {
        return configLongPollTimeout;
    }

    public void setConfigLongPollTimeout(String configLongPollTimeout) {
        this.configLongPollTimeout = configLongPollTimeout;
    }

    public String getConfigRetryTime() {
        return configRetryTime;
    }

    public void setConfigRetryTime(String configRetryTime) {
        this.configRetryTime = configRetryTime;
    }

    public String getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(String maxRetry) {
        this.maxRetry = maxRetry;
    }

    public String getEnableRemoteSyncConfig() {
        return enableRemoteSyncConfig;
    }

    public void setEnableRemoteSyncConfig(String enableRemoteSyncConfig) {
        this.enableRemoteSyncConfig = enableRemoteSyncConfig;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        putProperties(properties, "isUseCloudNamespaceParsing", isUseCloudNamespaceParsing);
        putProperties(properties, "isUseEndpointParsingRule", isUseEndpointParsingRule);
        putProperties(properties, "endpoint", endpoint);
        putProperties(properties, "endpointPort", endpointPort);
        putProperties(properties, "namespace", namespace);
        putProperties(properties, "username", username);
        putProperties(properties, "password", password);
        putProperties(properties, "accessKey", accessKey);
        putProperties(properties, "secretKey", secretKey);
        putProperties(properties, "ramRoleName", ramRoleName);
        putProperties(properties, "serverAddr", serverAddr);
        putProperties(properties, "contextPath", contextPath);
        putProperties(properties, "clusterName", clusterName);
        putProperties(properties, "encode", encode);
        putProperties(properties, "configLongPollTimeout", configLongPollTimeout);
        putProperties(properties, "configRetryTime", configRetryTime);
        putProperties(properties, "maxRetry", maxRetry);
        putProperties(properties, "enableRemoteSyncConfig", enableRemoteSyncConfig);
        return properties;
    }

    private void putProperties(Properties p, String key, String value) {
        if (value != null && value.trim().length() > 0) {
            p.put(key, value);
        }
    }


    public boolean isConfigOk() {
        return JbootConfigKit.areNotBlank(serverAddr, dataId, group);
    }
}
