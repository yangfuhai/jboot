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
package io.jboot.components.gateway;

import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/22
 */
public class JbootGatewayConfig implements Serializable {

    public static final String DEFAULT_PROXY_CONTENT_TYPE = "text/html;charset=utf-8";
    public static final GatewayInterceptor[] EMPTY_GATEWAY_INTERCEPTOR_ARRAY = new GatewayInterceptor[0];

    private String name;
    private Set<String> uri;


    // 是否启用健康检查
    private boolean uriHealthCheckEnable;

    // URI 健康检查路径，要求服务 statusCode = 200
    // 当配置 uriHealthCheckPath 后，健康检查的 url 地址为 uri + uriHealthCheckPath
    private String uriHealthCheckPath;

    // 是否启用
    private boolean enable = false;

    // 是否启用 sentinel 限流
    private boolean sentinelEnable = false;
    // sentinel 被限流后跳转地址
    private String sentinelBlockPage;
    // sentinel 被渲染的json内容，如果配置 sentinelBlockPage，则 sentinelBlockJsonMap 配置无效
    private Map<String, String> sentinelBlockJsonMap;

    // 设置 http 代理的参数
    private int proxyReadTimeout = 10000; //10s
    private int proxyConnectTimeout = 5000; //5s
    private int proxyRetries = 2; //2 times
    private String proxyContentType = DEFAULT_PROXY_CONTENT_TYPE;


    private String[] pathEquals;
    private String[] pathContains;
    private String[] pathStartsWith;
    private String[] pathEndsWith;


    private String[] hostEquals;
    private String[] hostContains;
    private String[] hostStartsWith;
    private String[] hostEndsWith;


    private Map<String, String> queryEquals;
    private String[] queryContains;

    //拦截器配置，一般可以用于对请求进行 鉴权 等处理
    private String[] interceptors;
    private String loadBalanceStrategy;

//    暂时不支持 cookie
//    private Map<String, String> cookieEquals;
//    private String[] cookieContains;

    //不健康的 URI 地址
    private Set<String> unHealthUris = Collections.synchronizedSet(new HashSet<>());


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Set<String> getUri() {
        return uri;
    }

    public void setUri(Set<String> uri) {
        this.uri = uri;
    }

    //服务发现的 URI 列表
    private Set<String> discoveryUris;

    //健康的 URI 缓存
    private String[] healthUris;

    //健康 URI 是否有变化的标识
    private boolean healthUriChanged = true;

    public String[] getHealthUris() {
        if (healthUriChanged) {
            synchronized (this) {
                if (healthUriChanged) {
                    if ((uri == null || uri.isEmpty()) && (discoveryUris == null || discoveryUris.isEmpty())) {
                        healthUris = null;
                    } else {
                        HashSet<String> healthUriSet = new HashSet<>();
                        if (uri != null && !uri.isEmpty()) {
                            healthUriSet.addAll(uri);
                        }

                        if (discoveryUris != null && !discoveryUris.isEmpty()) {
                            healthUriSet.addAll(discoveryUris);
                        }

                        if (!unHealthUris.isEmpty()) {
                            healthUriSet.removeAll(unHealthUris);
                        }
                        healthUris = healthUriSet.isEmpty() ? null : healthUriSet.toArray(new String[healthUriSet.size()]);

                    }
                    healthUriChanged = false;
                }
            }
        }
        return healthUris;
    }

    public boolean isUriHealthCheckEnable() {
        return uriHealthCheckEnable;
    }

    public void setUriHealthCheckEnable(boolean uriHealthCheckEnable) {
        this.uriHealthCheckEnable = uriHealthCheckEnable;
    }

    public String getUriHealthCheckPath() {
        return uriHealthCheckPath;
    }

    public void setUriHealthCheckPath(String uriHealthCheckPath) {
        this.uriHealthCheckPath = uriHealthCheckPath;
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isSentinelEnable() {
        return sentinelEnable;
    }

    public void setSentinelEnable(boolean sentinelEnable) {
        this.sentinelEnable = sentinelEnable;
    }

    public String getSentinelBlockPage() {
        return sentinelBlockPage;
    }

    public void setSentinelBlockPage(String sentinelBlockPage) {
        this.sentinelBlockPage = sentinelBlockPage;
    }

    public Map<String, String> getSentinelBlockJsonMap() {
        return sentinelBlockJsonMap;
    }

    public void setSentinelBlockJsonMap(Map<String, String> sentinelBlockJsonMap) {
        this.sentinelBlockJsonMap = sentinelBlockJsonMap;
    }

    public int getProxyReadTimeout() {
        return proxyReadTimeout;
    }

    public void setProxyReadTimeout(int proxyReadTimeout) {
        this.proxyReadTimeout = proxyReadTimeout;
    }

    public int getProxyConnectTimeout() {
        return proxyConnectTimeout;
    }

    public void setProxyConnectTimeout(int proxyConnectTimeout) {
        this.proxyConnectTimeout = proxyConnectTimeout;
    }

    public int getProxyRetries() {
        return proxyRetries;
    }

    public void setProxyRetries(int proxyRetries) {
        this.proxyRetries = proxyRetries;
    }

    public String getProxyContentType() {
        return proxyContentType;
    }

    public void setProxyContentType(String proxyContentType) {
        this.proxyContentType = proxyContentType;
    }

    public String[] getPathEquals() {
        return pathEquals;
    }

    public void setPathEquals(String[] pathEquals) {
        this.pathEquals = pathEquals;
    }

    public String[] getPathContains() {
        return pathContains;
    }

    public void setPathContains(String[] pathContains) {
        this.pathContains = pathContains;
    }

    public String[] getPathStartsWith() {
        return pathStartsWith;
    }

    public void setPathStartsWith(String[] pathStartsWith) {
        this.pathStartsWith = pathStartsWith;
    }

    public String[] getPathEndsWith() {
        return pathEndsWith;
    }

    public void setPathEndsWith(String[] pathEndsWith) {
        this.pathEndsWith = pathEndsWith;
    }

    public String[] getHostEquals() {
        return hostEquals;
    }

    public void setHostEquals(String[] hostEquals) {
        this.hostEquals = hostEquals;
    }

    public String[] getHostContains() {
        return hostContains;
    }

    public void setHostContains(String[] hostContains) {
        this.hostContains = hostContains;
    }

    public String[] getHostStartsWith() {
        return hostStartsWith;
    }

    public void setHostStartsWith(String[] hostStartsWith) {
        this.hostStartsWith = hostStartsWith;
    }

    public String[] getHostEndsWith() {
        return hostEndsWith;
    }

    public void setHostEndsWith(String[] hostEndsWith) {
        this.hostEndsWith = hostEndsWith;
    }

    public Map<String, String> getQueryEquals() {
        return queryEquals;
    }

    public void setQueryEquals(Map<String, String> queryEquals) {
        this.queryEquals = queryEquals;
    }

    public String[] getQueryContains() {
        return queryContains;
    }

    public void setQueryContains(String[] queryContains) {
        this.queryContains = queryContains;
    }

    public String[] getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(String[] interceptors) {
        this.interceptors = interceptors;
    }


    private GatewayInterceptor[] gatewayInterceptors;

    public GatewayInterceptor[] getGatewayInterceptors() {
        if (gatewayInterceptors == null) {
            synchronized (this) {
                if (gatewayInterceptors == null) {
                    if (interceptors == null || interceptors.length == 0) {
                        gatewayInterceptors = EMPTY_GATEWAY_INTERCEPTOR_ARRAY;
                    } else {
                        gatewayInterceptors = new GatewayInterceptor[interceptors.length];
                        for (int i = 0; i < interceptors.length; i++) {
                            GatewayInterceptor interceptor = ClassUtil.newInstance(interceptors[i]);
                            if (interceptor == null) {
                                throw new NullPointerException("can not new instance by class:" + interceptors[i]);
                            }
                            gatewayInterceptors[i] = interceptor;
                        }
                    }
                }
            }
        }
        return gatewayInterceptors;
    }


    public void setGatewayInterceptors(GatewayInterceptor[] gatewayInterceptors) {
        this.gatewayInterceptors = gatewayInterceptors;
    }


    public String getLoadBalanceStrategy() {
        return loadBalanceStrategy;
    }

    public void setLoadBalanceStrategy(String loadBalanceStrategy) {
        this.loadBalanceStrategy = loadBalanceStrategy;
    }

    private GatewayLoadBalanceStrategy gatewayLoadBalanceStrategy;

    public GatewayLoadBalanceStrategy buildLoadBalanceStrategy() {
        if (gatewayLoadBalanceStrategy != null) {
            return gatewayLoadBalanceStrategy;
        }

        if (gatewayLoadBalanceStrategy == null) {
            synchronized (this) {
                if (gatewayLoadBalanceStrategy == null) {
                    if (StrUtil.isBlank(loadBalanceStrategy)) {
                        gatewayLoadBalanceStrategy = GatewayLoadBalanceStrategy.DEFAULT_STRATEGY;
                    } else {
                        GatewayLoadBalanceStrategy glbs = ClassUtil.newInstance(loadBalanceStrategy);
                        if (glbs == null) {
                            throw new NullPointerException("Can not new instance by class: " + loadBalanceStrategy);
                        }
                        gatewayLoadBalanceStrategy = glbs;
                    }
                }
            }
        }
        return gatewayLoadBalanceStrategy;
    }

    public void setGatewayLoadBalanceStrategy(GatewayLoadBalanceStrategy strategy) {
        this.gatewayLoadBalanceStrategy = strategy;
    }


    public boolean matches(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        String path = request.getServletPath();
        if (pathEquals != null) {
            for (String p : pathEquals) {
                if (path.equals(p)) {
                    return true;
                }
            }
        }

        if (pathContains != null) {
            for (String p : pathContains) {
                if (path.contains(p)) {
                    return true;
                }
            }
        }

        if (pathStartsWith != null) {
            for (String p : pathStartsWith) {
                if (path.startsWith(p)) {
                    return true;
                }
            }
        }

        if (pathEndsWith != null) {
            for (String p : pathEndsWith) {
                if (path.endsWith(p)) {
                    return true;
                }
            }
        }

        String host = request.getServerName();
        if (hostEquals != null) {
            for (String h : hostEquals) {
                if (host.equals(h)) {
                    return true;
                }
            }
        }

        if (hostContains != null) {
            for (String h : hostContains) {
                if (host.contains(h)) {
                    return true;
                }
            }
        }

        if (hostStartsWith != null) {
            for (String h : hostStartsWith) {
                if (host.startsWith(h)) {
                    return true;
                }
            }
        }

        if (hostEndsWith != null) {
            for (String h : hostEndsWith) {
                if (host.endsWith(h)) {
                    return true;
                }
            }
        }

        if (queryContains != null || queryEquals != null) {
            Map<String, String> queryMap = StrUtil.queryStringToMap(request.getQueryString());
            if (!queryMap.isEmpty()) {

                if (queryContains != null) {
                    for (String q : queryContains) {
                        if (queryMap.containsKey(q)) {
                            return true;
                        }
                    }
                }

                if (queryEquals != null) {
                    for (Map.Entry<String, String> e : queryEquals.entrySet()) {
                        String queryValue = queryMap.get(e.getKey());
                        if (Objects.equals(queryValue, e.getValue())) {
                            return true;
                        }
                    }
                }
            }
        }


        return false;
    }

    public void syncDiscoveryUris(Collection<String> syncUris) {
        if (syncUris == null || syncUris.isEmpty()) {
            discoveryUris = null;
            healthUriChanged = true;
        }

        if (discoveryUris == null) {
            discoveryUris = new HashSet<>(syncUris);
            healthUriChanged = true;
            return;
        }

        if (discoveryUris.size() == syncUris.size() && discoveryUris.containsAll(syncUris)) {
            return;
        } else {
            discoveryUris.clear();
            discoveryUris.addAll(syncUris);
            healthUriChanged = true;
        }
    }


    public void addUnHealthUri(String uri) {
        if (unHealthUris.add(uri)) {
            healthUriChanged = true;
        }
    }


    public void removeUnHealthUri(String uri) {
        if (unHealthUris.size() > 0) {
            if (unHealthUris.remove(uri)) {
                healthUriChanged = true;
            }
        }
    }


}
