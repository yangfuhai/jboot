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

import io.jboot.components.rpc.annotation.RPCBean;

import java.io.Serializable;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.components.rpc
 */
public class JbootrpcServiceConfig implements Serializable {

    /**
     * Service version, default value is empty string
     */
    private String version;

    /**
     * Service group, default value is empty string
     */
    private String group;

    /**
     * Service path, default value is empty string
     */
    private String path;

    /**
     * Whether to export service, default value is true
     */
    private boolean export = true;

    /**
     * Service token, default value is false
     */
    private String token;

    /**
     * Whether the service is deprecated, default value is false
     */
    private boolean deprecated = false;


    /**
     * Whether to register the service to register center, default value is true
     */
    private boolean register = true;

    /**
     * Service weight value, default value is 0
     */
    private int weight = 0;

    /**
     * Service doc, default value is ""
     */
    private String document = "";


    /**
     * Service invocation retry times
     */
    private int retries = 2;

    /**
     * Load balance strategy, legal values include: random, roundrobin, leastactive
     */
    private String loadbalance = "random";


    /**
     * Application bean name
     */
    private String application;

    /**
     * Module bean name
     */
    private String module;

    /**
     * Provider bean name
     */
    private String provider;

    /**
     * Protocol bean names
     */
    private String protocol;

    /**
     * Monitor bean name
     */
    private String monitor;

    /**
     * Registry bean name
     */
    private String registry;

    /**
     * Service tag name
     */
    private String tag;


    public JbootrpcServiceConfig() {
    }

    public JbootrpcServiceConfig(RPCBean bean) {
        RPCUtils.appendAnnotation(RPCBean.class, bean, this);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isExport() {
        return export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public String getLoadbalance() {
        return loadbalance;
    }

    public void setLoadbalance(String loadbalance) {
        this.loadbalance = loadbalance;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
