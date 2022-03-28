/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.cors;

import com.jfinal.ext.cors.EnableCORS;
import io.jboot.utils.AnnotationUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CORSConfig {

    private static final CORSConfig DEFAULT_CONFIG = new CORSConfig();
    private static final Map<Integer, CORSConfig> cache = new ConcurrentHashMap<>();

    private String allowOrigin = "*";
    private String allowCredentials = "true";
    private String allowHeaders = "Origin,X-Requested-With,Content-Type,Accept,Authorization,Jwt";
    private String allowMethods = "GET,PUT,POST,DELETE,PATCH,OPTIONS";
    private String exposeHeaders = "";
    private String requestHeaders = "";
    private String requestMethod = "";
    private String origin = "";
    private String maxAge = "3600";

    public static CORSConfig getDefaultConfig() {
        return DEFAULT_CONFIG;
    }

    public static CORSConfig fromAnnotation(EnableCORS enableCORS) {
        int identityHashCode = System.identityHashCode(enableCORS);
        CORSConfig corsConfig = cache.get(identityHashCode);
        if (corsConfig == null) {
            corsConfig = new CORSConfig(enableCORS);
            if (corsConfig.equals(DEFAULT_CONFIG)) {
                corsConfig = DEFAULT_CONFIG;
            }
            cache.put(identityHashCode, corsConfig);
        }
        return corsConfig;
    }


    public CORSConfig() {
    }

    public CORSConfig(String allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    public CORSConfig(String allowOrigin, String allowHeaders) {
        this.allowOrigin = allowOrigin;
        this.allowHeaders = allowHeaders;
    }

    public CORSConfig(String allowOrigin
            , String allowCredentials
            , String allowHeaders
            , String allowMethods
            , String exposeHeaders
            , String requestHeaders
            , String requestMethod
            , String origin
            , String maxAge) {
        this.allowOrigin = allowOrigin;
        this.allowCredentials = allowCredentials;
        this.allowHeaders = allowHeaders;
        this.allowMethods = allowMethods;
        this.exposeHeaders = exposeHeaders;
        this.requestHeaders = requestHeaders;
        this.requestMethod = requestMethod;
        this.origin = origin;
        this.maxAge = maxAge;
    }

    public CORSConfig(EnableCORS enableCORS) {
        this.allowOrigin = AnnotationUtil.get(enableCORS.allowOrigin());
        this.allowCredentials = AnnotationUtil.get(enableCORS.allowCredentials());
        this.allowHeaders = AnnotationUtil.get(enableCORS.allowHeaders());
        this.allowMethods = AnnotationUtil.get(enableCORS.allowMethods());
        this.exposeHeaders = AnnotationUtil.get(enableCORS.exposeHeaders());
        this.requestHeaders = AnnotationUtil.get(enableCORS.requestHeaders());
        this.requestMethod = AnnotationUtil.get(enableCORS.requestMethod());
        this.origin = AnnotationUtil.get(enableCORS.origin());
        this.maxAge = AnnotationUtil.get(enableCORS.maxAge());
    }


    public String getAllowOrigin() {
        return allowOrigin;
    }

    public void setAllowOrigin(String allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    public String getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public String getAllowHeaders() {
        return allowHeaders;
    }

    public void setAllowHeaders(String allowHeaders) {
        this.allowHeaders = allowHeaders;
    }

    public String getAllowMethods() {
        return allowMethods;
    }

    public void setAllowMethods(String allowMethods) {
        this.allowMethods = allowMethods;
    }

    public String getExposeHeaders() {
        return exposeHeaders;
    }

    public void setExposeHeaders(String exposeHeaders) {
        this.exposeHeaders = exposeHeaders;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CORSConfig that = (CORSConfig) o;
        return Objects.equals(allowOrigin, that.allowOrigin)
                && Objects.equals(allowCredentials, that.allowCredentials)
                && Objects.equals(allowHeaders, that.allowHeaders)
                && Objects.equals(allowMethods, that.allowMethods)
                && Objects.equals(exposeHeaders, that.exposeHeaders)
                && Objects.equals(requestHeaders, that.requestHeaders)
                && Objects.equals(requestMethod, that.requestMethod)
                && Objects.equals(origin, that.origin)
                && Objects.equals(maxAge, that.maxAge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowOrigin, allowCredentials, allowHeaders, allowMethods, exposeHeaders, requestHeaders, requestMethod, origin, maxAge);
    }
}
