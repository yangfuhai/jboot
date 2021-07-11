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

import com.jfinal.kit.Ret;
import io.jboot.Jboot;
import io.jboot.utils.StrUtil;
import io.jboot.web.render.JbootJsonRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;


public class GatewayInvocation {

    private JbootGatewayConfig config;
    private GatewayInterceptor[] inters;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private GatewayHttpProxy proxy;
    private String proxyUrl;

    //是否跳过错误渲染，如果跳过，那么则由拦截器通过 getResponse() 自行渲染
    private boolean skipExceptionRender = false;

    private static boolean devMode = Jboot.isDevMode();

    private int index = 0;


    public GatewayInvocation(JbootGatewayConfig config, HttpServletRequest request, HttpServletResponse response) {
        this.config = config;
        this.request = request;
        this.response = response;
        this.inters = config.getGatewayInterceptors();
        this.proxy = new GatewayHttpProxy(config);
        this.proxyUrl = buildProxyUrl(config, request);
    }


    public void invoke() {
        if (inters.length == 0) {
            doInvoke();
            return;
        }
        if (index < inters.length) {
            inters[index++].intercept(this);
        } else if (index++ >= inters.length) {
            doInvoke();
        }
    }


    protected void doInvoke() {
        if (StrUtil.isBlank(proxyUrl)) {
            renderError(null, GatewayErrorRender.noneHealthUrl, config, request, response);
            return;
        }

        if (devMode) {
            System.out.println("Jboot Gateway >>> " + proxyUrl);
        }

        //启用 Sentinel 限流
        if (config.isSentinelEnable()) {
            new GatewaySentinelProcesser().process(proxy, proxyUrl, config, request, response, skipExceptionRender);
            return;
        }


        //未启用 Sentinel 的情况
        proxy.sendRequest(proxyUrl, request, response);


        Exception exception = proxy.getException();
        if (exception != null && !skipExceptionRender) {
            if (exception instanceof ConnectException) {
                Ret connectionError = Ret.fail().set("errorCode", 2).set("message", "Can not connect to target server: " + proxyUrl);
                renderError(exception, connectionError, config, request, response);
            } else {
                Ret ret = Ret.fail().set("errorCode", 9).set("message", exception.getMessage());
                renderError(exception, ret, config, request, response);
            }
        }

    }


    private static void renderError(Exception error, Ret errorMessage, JbootGatewayConfig config, HttpServletRequest request, HttpServletResponse response) {
        GatewayErrorRender errorRender = JbootGatewayManager.me().getGatewayErrorRender();
        if (errorRender != null) {
            errorRender.renderError(error, errorMessage, config, request, response);
        } else {
            new JbootJsonRender(errorMessage).setContext(request, response).render();
        }
    }


    private static String buildProxyUrl(JbootGatewayConfig config, HttpServletRequest request) {
        //配置负载均衡策略
        GatewayLoadBalanceStrategy lbs = config.buildLoadBalanceStrategy();

        //通过负载均衡策略获取 URL 地址
        String url = lbs.getUrl(config, request);
        if (StrUtil.isBlank(url)) {
            return null;
        }

        StringBuilder sb = new StringBuilder(url);
        if (StrUtil.isNotBlank(request.getRequestURI())) {
            sb.append(request.getRequestURI());
        }

        if (StrUtil.isNotBlank(request.getQueryString())) {
            sb.append("?").append(request.getQueryString());
        }

        return sb.toString();
    }


    public JbootGatewayConfig getConfig() {
        return config;
    }

    public void setConfig(JbootGatewayConfig config) {
        this.config = config;
    }

    public GatewayInterceptor[] getInters() {
        return inters;
    }

    public void setInters(GatewayInterceptor[] inters) {
        this.inters = inters;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public GatewayHttpProxy getProxy() {
        return proxy;
    }


    public void setProxy(GatewayHttpProxy proxy) {
        this.proxy = proxy;
    }

    public boolean hasException() {
        return proxy.getException() != null;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public boolean isSkipExceptionRender() {
        return skipExceptionRender;
    }

    public void setSkipExceptionRender(boolean skipExceptionRender) {
        this.skipExceptionRender = skipExceptionRender;
    }
}
