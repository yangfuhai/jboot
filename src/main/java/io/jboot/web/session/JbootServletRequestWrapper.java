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

import io.jboot.Jboot;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheConfig;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.utils.StrUtil;

import javax.servlet.http.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class JbootServletRequestWrapper extends HttpServletRequestWrapper {

    private static JbootSessionConfig config = Jboot.config(JbootSessionConfig.class);

    private static int maxInactiveInterval = config.getMaxInactiveInterval();
    private static String cookieName = config.getCookieName();
    private static String cookiePath = config.getCookieContextPath();
    private static String cookieDomain = config.getCookieDomain();
    private static int cookieMaxAge = config.getCookieMaxAge();
    private static String cacheName = config.getCacheName();
    private static String cacheType = config.getCacheType();

    private static JbootCache jbootCache = JbootCacheManager.me()
            .getCache(StrUtil.isBlank(cacheType) || JbootCacheConfig.TYPE_NONE.equals(cacheType)
                    ? JbootCacheConfig.TYPE_EHCACHE
                    : cacheType);


    private HttpServletResponse response;
    private HttpServletRequest originRequest;
    private JbootHttpSession jbootSession;


    public JbootServletRequestWrapper(HttpServletRequest request, HttpServletResponse response) {
        super(request);
        this.originRequest = request;
        this.response = response;
    }

    @Override
    public HttpSession getSession() {
        return getSession(false);
    }


    @Override
    public HttpSession getSession(boolean create) {
        if (jbootSession != null) {
            return jbootSession;
        }

        String sessionId = getCookie(cookieName);
        if (sessionId != null) {
            jbootSession = new JbootHttpSession(sessionId, originRequest.getServletContext(), createSessionStore(sessionId));
            jbootSession.setMaxInactiveInterval(maxInactiveInterval);
        } else if (create) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
            jbootSession = new JbootHttpSession(sessionId, originRequest.getServletContext(), createSessionStore(sessionId));
            jbootSession.setMaxInactiveInterval(maxInactiveInterval);
            setCookie(cookieName, sessionId, cookieMaxAge);
        }

        return jbootSession;
    }

    private Map<String, Object> createSessionStore(String sessionId) {
        Map<String, Object> store = jbootCache.get(cacheName, sessionId);
        if (store == null) {
            store = new HashMap<>();
            HttpSession originSession = originRequest.getSession();
            if (originSession != null) {
                Enumeration<String> names = originSession.getAttributeNames();
                if (names != null) {
                    while (names.hasMoreElements()) {
                        String name = names.nextElement();
                        store.put(name, originSession.getAttribute(name));
                    }
                }
            }
        }
        return store;
    }


    /**
     * http请求结束时，更新session信息，包括：刷新session的存储时间，更新session数据，清空session数据等
     */
    public void refreshSession() {
        if (jbootSession == null) {
            return;
        }

        // 空的 jbootSession 数据
        // 或者 session 已经被整体删除，调用了session.invalidate()
        if (jbootSession.isEmpty() || !jbootSession.isValid()) {
            jbootCache.remove(cacheName, jbootSession.getId());
            setCookie(cookieName, null, 0);
        }
        //session 已经被修改(session数据的增删改查)
        else if (jbootSession.isDataChanged()) {
            Map<String, Object> snapshot = jbootSession.snapshot();
            // 数据已经全部被删除了
            if (snapshot.isEmpty()) {
                jbootCache.remove(cacheName, jbootSession.getId());
                setCookie(cookieName, null, 0);
            } else {
                jbootCache.put(cacheName, jbootSession.getId(), snapshot, maxInactiveInterval);
            }
        }
        //更新session存储时间
        else {
            jbootCache.setTtl(cacheName, jbootSession.getId(), maxInactiveInterval);
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
        Cookie[] cookies = originRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * @param name
     * @param value
     * @param maxAgeInSeconds
     */
    private void setCookie(String name, String value, int maxAgeInSeconds) {
        if (!response.isCommitted()) {
            Cookie cookie = new Cookie(name, value);
            cookie.setMaxAge(maxAgeInSeconds);
            cookie.setPath(cookiePath);
            if (cookieDomain != null) {
                cookie.setDomain(cookieDomain);
            }
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }

    public HttpServletRequest getOriginRequest() {
        return originRequest;
    }
}
