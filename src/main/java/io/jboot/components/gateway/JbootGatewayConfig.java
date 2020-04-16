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
package io.jboot.components.gateway;

import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/22
 */
public class JbootGatewayConfig implements Serializable {

    public static final String DEFAULT_PROXY_CONTENT_TYPE = "text/html;charset=utf-8";

    private String name;
    private String[] uri;
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

//    暂时不支持 cookie
//    private Map<String, String> cookieEquals;
//    private String[] cookieContains;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String[] getUri() {
        return uri;
    }

    public void setUri(String[] uri) {
        this.uri = uri;
    }

    public String getRandomUri() {
        if (uri == null || uri.length == 0) {
            return null;
        } else if (uri.length == 1) {
            return uri[0];
        } else {
            return uri[ThreadLocalRandom.current().nextInt(uri.length)];
        }
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


    private GatewayInterceptor[] inters;

    public GatewayInterceptor[] buildInterceptors() {
        if (interceptors == null || interceptors.length == 0) {
            return null;
        }
        if (inters == null) {
            synchronized (this) {
                if (inters == null) {
                    inters = new GatewayInterceptor[interceptors.length];
                    for (int i = 0; i < interceptors.length; i++) {
                        GatewayInterceptor interceptor = ClassUtil.newInstance(interceptors[i]);
                        if (interceptor == null) {
                            throw new NullPointerException("can not new instance by class:" + interceptors[i]);
                        }
                        inters[i] = interceptor;
                    }
                }
            }
        }
        return inters;
    }


    private Boolean configOk = null;

    public boolean isConfigOk() {
        if (configOk != null) {
            return configOk;
        }
        synchronized (this) {
            if (configOk == null) {
                configOk = uri != null && uri.length > 0;
                if (configOk) {
                    ensureUriConfigCorrect();
                }
            }
        }
        return configOk;
    }

    private void ensureUriConfigCorrect() {
        for (String u : uri) {
            if (!u.toLowerCase().startsWith("http://")
                    && !u.toLowerCase().startsWith("https://")) {
                throw new JbootIllegalConfigException("gateway uri must start with http:// or https://");
            }
        }
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
            Map<String, String> queryMap = queryStringToMap(request.getQueryString());
            if (queryMap != null && !queryMap.isEmpty()) {

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


    private static Map<String, String> queryStringToMap(String queryString) {
        if (StrUtil.isBlank(queryString)) {
            return null;
        }
        String[] params = queryString.split("&");
        Map<String, String> resMap = new HashMap<>();
        for (int i = 0; i < params.length; i++) {
            String[] param = params[i].split("=");
            if (param.length >= 2) {
                String key = param[0];
                String value = param[1];
                for (int j = 2; j < param.length; j++) {
                    value += "=" + param[j];
                }
                resMap.put(key, value);
            }
        }
        return resMap;
    }
}
