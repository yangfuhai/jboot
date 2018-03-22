/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.core.cache.JbootCache;
import io.jboot.core.cache.JbootCacheManager;
import io.jboot.web.JbootRequestContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;


public class JbootServletRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletRequest originHttpServletRequest;
    private JbootHttpSession httpSession;
    private JbootCache jbootCache;

    private int maxInactiveInterval = JbootSessionConfig.get().getMaxInactiveInterval();
    private String cookieName = JbootSessionConfig.get().getCookieName();
    private String cookiePath = JbootSessionConfig.get().getCookieContextPath();
    private String cookieDomain = JbootSessionConfig.get().getCookieDomain();
    private int cookieMaxAge = JbootSessionConfig.get().getCookieMaxAge();
    private String cacheName = JbootSessionConfig.get().getCacheName();
    private String cacheType = JbootSessionConfig.get().getCacheType();


    public JbootServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.originHttpServletRequest = request;
        this.jbootCache = JbootCacheManager.me().getCache(cacheType);
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }


    @Override
    public HttpSession getSession(boolean create) {
        if (httpSession != null) {
            return httpSession;
        }

        String sessionId = getCookie(cookieName);
        if (sessionId != null) {
            httpSession = new JbootHttpSession(sessionId, originHttpServletRequest.getServletContext(), createHttpSessionStore(sessionId));
        } else if (create) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
            httpSession = new JbootHttpSession(sessionId, originHttpServletRequest.getServletContext(), createHttpSessionStore(sessionId));
            setCookie(cookieName, sessionId, maxInactiveInterval);
        }
        return httpSession;
    }

    private Map<String, Object> createHttpSessionStore(String sessionId) {
        Map<String, Object> store = jbootCache.get(cacheName, sessionId);
        if (store == null) {
            store = Collections.emptyMap();
        }
        return store;
    }


    public void finish() {
        if (httpSession == null) {
            return;
        }

        //session已经被删除
        if (!httpSession.isValid()) {
            jbootCache.remove(cacheName, httpSession.getId());
            setCookie(cookieName, null, 0);
        }
        //session 已经被修改
        else if (httpSession.isDirty()) {
            Map<String, Object> snapshot = httpSession.snapshot();
            jbootCache.put(cacheName, httpSession.getId(), snapshot, maxInactiveInterval);
        }
        //更新session存储时间
        else {
            jbootCache.setTtl(cacheName, httpSession.getId(), maxInactiveInterval);
        }
    }


    /**
     * Get cookie value by cookie name.
     */
    private String getCookie(String name) {
        Cookie cookie = getCookieObject(name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * Get cookie object by cookie name.
     */
    private Cookie getCookieObject(String name) {
        Cookie[] cookies = JbootRequestContext.getRequest().getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies)
                if (cookie.getName().equals(name))
                    return cookie;
        return null;
    }

    /**
     * @param name
     * @param value
     * @param maxAgeInSeconds
     */
    private void setCookie(String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(cookiePath);
        if (cookieDomain != null) {
            cookie.setDomain(cookieDomain);
        }
        cookie.setMaxAge(cookieMaxAge);
        cookie.setHttpOnly(true);
        JbootRequestContext.getResponse().addCookie(cookie);
    }

}
