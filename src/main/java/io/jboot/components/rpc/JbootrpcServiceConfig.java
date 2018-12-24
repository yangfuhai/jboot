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

import io.jboot.Jboot;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.utils.StrUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.core.rpc
 */
public class JbootrpcServiceConfig implements Serializable {


    private int port = 0;
    private String group;
    private String version;
    private Integer timeout;
    private Integer retries;
    private Integer actives;
    private String loadbalance;
    private Boolean async;
    private Boolean check;


    private String proxy;
    private String filter;

    // 用于扩展，用户通过SPI扩展获取自定义的 Service的时候，
    // 若以字段不满足，此时通过 params 自行扩展
    private Map<Object, Object> params;

    private static JbootrpcConfig defaultConfig = Jboot.config(JbootrpcConfig.class);


    public JbootrpcServiceConfig() {
        this.port = defaultConfig.getDefaultPort();
        this.group = defaultConfig.getDefaultGroup();
        this.version = defaultConfig.getDefaultVersion();
        this.timeout = defaultConfig.getRequestTimeOut();
        this.retries = defaultConfig.getRetries();
        this.proxy = defaultConfig.getProxy();
        this.filter = defaultConfig.getFilter();
    }

    public JbootrpcServiceConfig(RPCInject rpcInject) {
        this();

        if (rpcInject.port() > 0) {
            this.port = rpcInject.port();
        }

        if (StrUtil.isNotBlank(rpcInject.group())) {
            this.group = rpcInject.group();
        }

        if (StrUtil.isNotBlank(rpcInject.version())) {
            this.version = rpcInject.version();
        }

        if (rpcInject.retries() >= 0) {
            this.retries = rpcInject.retries();
        }

        if (rpcInject.actives() >= 0) {
            this.actives = rpcInject.actives();
        }

        if (StrUtil.isNotBlank(rpcInject.loadbalance())) {
            this.loadbalance = rpcInject.loadbalance();
        }

        if (StrUtil.isNotBlank(rpcInject.async())) {
            this.async = Boolean.getBoolean(rpcInject.async());
        }

        if (StrUtil.isNotBlank(rpcInject.check())) {
            this.check = Boolean.getBoolean(rpcInject.check());
        }

    }

    public JbootrpcServiceConfig(RPCBean annotation) {
        this();

        if (annotation.port() > 0) {
            this.port = annotation.port();
        }

        if (StrUtil.isNotBlank(annotation.group())) {
            this.group = annotation.group();
        }

        if (StrUtil.isNotBlank(annotation.version())) {
            this.version = annotation.version();
        }

        if (annotation.actives() >= 0) {
            this.actives = annotation.actives();
        }

        if (annotation.timeout() >= 0) {
            this.timeout = annotation.timeout();
        }

    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getActives() {
        return actives;
    }

    public void setActives(Integer actives) {
        this.actives = actives;
    }

    public String getLoadbalance() {
        return loadbalance;
    }

    public void setLoadbalance(String loadbalance) {
        this.loadbalance = loadbalance;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Map<Object, Object> getParams() {
        return params;
    }

    public void setParams(Map<Object, Object> params) {
        this.params = params;
    }

    public void addParam(Object key, Object value) {
        if (params == null) {
            params = new HashMap();
        }

        params.put(key, value);
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
}
