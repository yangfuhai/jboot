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
package io.jboot.web.session;

import io.jboot.app.config.annotation.ConfigModel;

@ConfigModel(prefix = "jboot.web.session")
public class JbootSessionConfig {

    public final static int DEFAULT_MAX_INACTIVE_INTERVAL = 60 * 60;
    public final static String DEFAULT_COOKIE_CONTEXT_PATH = "/";
    public final static int DEFAULT_COOKIE_MAX_AGE = -1;
    public final static String DEFAULT_SESSION_COOKIE_NAME = "_JSID";
    public final static String DEFAULT_SESSION_CACHE_NAME = "JBOOTSESSION";


    private String cookieName = DEFAULT_SESSION_COOKIE_NAME;
    private String cookieDomain;
    private String cookieContextPath = DEFAULT_COOKIE_CONTEXT_PATH;
    private int maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL;
    private int cookieMaxAge = DEFAULT_COOKIE_MAX_AGE;

    private String cacheName = DEFAULT_SESSION_CACHE_NAME;
    private String useCacheName = "default";


    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieDomain() {
        return cookieDomain;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public String getCookieContextPath() {
        return cookieContextPath;
    }

    public void setCookieContextPath(String cookieContextPath) {
        this.cookieContextPath = cookieContextPath;
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public int getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getUseCacheName() {
        return useCacheName;
    }

    public void setUseCacheName(String useCacheName) {
        this.useCacheName = useCacheName;
    }

}
