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
package io.jboot.web.session;

import io.jboot.Jboot;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheConfig;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.utils.StrUtil;

import javax.servlet.http.*;
import java.util.Collections;
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
    private JbootHttpSession httpSession;


    public JbootServletRequestWrapper(HttpServletRequest request, HttpServletResponse response) {
        super(request);
        this.response = response;
    }

    @Override
    public HttpSession getSession() {
        return getSession(false);
    }


    @Override
    public HttpSession getSession(boolean create) {
        if (httpSession != null) {
            return httpSession;
        }

        String sessionId = getCookie(cookieName);
        if (sessionId != null) {
            httpSession = new JbootHttpSession(sessionId, getRequest().getServletContext(), createSessionStore(sessionId));
            httpSession.setMaxInactiveInterval(maxInactiveInterval);
        } else if (create) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
            httpSession = new JbootHttpSession(sessionId, getRequest().getServletContext(), createSessionStore(sessionId));
            httpSession.setMaxInactiveInterval(maxInactiveInterval);
            setCookie(cookieName, sessionId, cookieMaxAge);
        }

        return httpSession;
    }

    private Map<String, Object> createSessionStore(String sessionId) {
        Map<String, Object> store = jbootCache.get(cacheName, sessionId);
        if (store == null) {
            store = Collections.emptyMap();
        }
        return store;
    }


    /**
     * http请求结束时，更新session信息，包括：刷新session的存储时间，更新session数据，清空session数据等
     */
    public void refreshSession() {
        if (httpSession == null) {
            return;
        }

        //session已经被整体删除，调用了session.invalidate()
        if (!httpSession.isValid()) {
            jbootCache.remove(cacheName, httpSession.getId());
            setCookie(cookieName, null, 0);
        }

        // 空的httpSession数据
        else if (httpSession.isEmpty()) {
            jbootCache.remove(cacheName, httpSession.getId());
            setCookie(cookieName, null, 0);
        }

        //session 已经被修改(session数据的增删改查)
        else if (httpSession.isDataChanged()) {
            Map<String, Object> snapshot = httpSession.snapshot();

            // 数据已经全部被删除了
            if (snapshot.isEmpty()) {
                jbootCache.remove(cacheName, httpSession.getId());
                setCookie(cookieName, null, 0);
            } else {
                jbootCache.put(cacheName, httpSession.getId(), snapshot, maxInactiveInterval);
            }
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
        Cookie[] cookies = ((HttpServletRequest) getRequest()).getCookies();
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
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

}
