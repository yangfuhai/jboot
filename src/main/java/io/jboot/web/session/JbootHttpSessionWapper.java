/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.session;

import io.jboot.Jboot;
import io.jboot.utils.StringUtils;
import io.jboot.web.RequestManager;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class JbootHttpSessionWapper implements HttpSession {

    private static final long SESSION_TIME = TimeUnit.DAYS.toSeconds(2);
    private static final String SESSION_CACHE_NAME = "SESSION";

    HttpSession baseSession;

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return getOrCreatSessionId();
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new RuntimeException("getSessionContext method not finished.");
    }

    @Override
    public Object getAttribute(String name) {
        return Jboot.getCache().get(SESSION_CACHE_NAME, buildKey(name));
    }

    @Override
    public Object getValue(String name) {
        return Jboot.getCache().get(SESSION_CACHE_NAME, buildKey(name));
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new RuntimeException("getAttributeNames method not finished.");
    }

    @Override
    public String[] getValueNames() {
        throw new RuntimeException("getValueNames method not finished.");
    }

    @Override
    public void setAttribute(String name, Object value) {
        Jboot.getCache().put(SESSION_CACHE_NAME, buildKey(name), value);
    }

    @Override
    public void putValue(String name, Object value) {
        Jboot.getCache().put(SESSION_CACHE_NAME, buildKey(name), value);
    }


    @Override
    public void removeAttribute(String name) {
        Jboot.getCache().remove(SESSION_CACHE_NAME, buildKey(name));
    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }


    private Object buildKey(String name) {
        return String.format("%s:%s", getOrCreatSessionId(), name);
    }

    private String getOrCreatSessionId() {
        String sessionid = ("JSESSIONID");
        if (StringUtils.isNotBlank(sessionid)) {
            return sessionid;
        }

        sessionid = UUID.randomUUID().toString().replace("-", "");
        setCookie("JSESSIONID", sessionid, (int) SESSION_TIME);
        return sessionid;
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
        Cookie[] cookies = RequestManager.me().getRequest().getCookies();
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
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        RequestManager.me().getResponse().addCookie(cookie);
    }

}
