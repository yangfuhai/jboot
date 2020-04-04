/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import java.util.Map;


@ConfigModel(prefix = "jboot.rpc")
public class JbootrpcConfig {

    public static final String TYPE_DUBBO = "dubbo";
    public static final String TYPE_MOTAN = "motan";
    public static final String TYPE_LOCAL = "local";

    private String type;

    //用于直连时的配置，直连一般只用于测试环境
    //com.service.AAAService:127.0.0.1:8080,com.service.XXXService:127.0.0.1:8080
    private Map<String, String> urls;

    //服务的provider指定，可以通过注解 @RPCBean 指定，也可以通过此处指定，此处的配置优先于注解
    //com.service.AAAService:providerName,com.service.XXXService:providerName
    private Map<String, String> providers;

    //服务的consumer指定，可以通过注解 @RPCInject 指定，也可以通过此处指定，此处的配置优先于注解
    //com.service.AAAService:providerName,com.service.XXXService:providerName
    private Map<String, String> consumers;

    //当不配置的时候，默认版本号
    private String defaultVersion = "1.0.0";

    //指定的服务的版本号
    private Map<String, String> versions;

    //当不指定的时候，默认分组
    private String defaultGroup;

    //指定的服务的分组
    private Map<String, String> groups;

    //本地自动暴露 @RPCBean 的 service
    private boolean autoExportEnable = true;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }

    public String getUrl(String serviceClass) {
        return urls == null ? null : urls.get(serviceClass);
    }

    public Map<String, String> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, String> providers) {
        this.providers = providers;
    }

    public String getProvider(String serviceClass) {
        return providers == null ? null : providers.get(serviceClass);
    }

    public Map<String, String> getConsumers() {
        return consumers;
    }

    public void setConsumers(Map<String, String> consumers) {
        this.consumers = consumers;
    }

    public String getConsumer(String serviceClass) {
        return consumers == null ? null : consumers.get(serviceClass);
    }

    public String getDefaultVersion() {
        return defaultVersion;
    }

    public void setDefaultVersion(String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    public Map<String, String> getVersions() {
        return versions;
    }

    public void setVersions(Map<String, String> versions) {
        this.versions = versions;
    }

    public String getVersion(String className) {
        String version = versions == null || versions.isEmpty() ? null : versions.get(className);
        return version == null ? defaultVersion : version;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public Map<String, String> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, String> groups) {
        this.groups = groups;
    }

    public String getGroup(String className) {
        String group = groups == null || groups.isEmpty() ? null : groups.get(className);
        return group == null ? defaultGroup : group;
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
