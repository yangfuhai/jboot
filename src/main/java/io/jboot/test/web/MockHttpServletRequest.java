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
package io.jboot.test.web;

import io.jboot.test.MockProxy;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.*;

public class MockHttpServletRequest extends HttpServletRequestWrapper {

    protected String contextPath;
    protected String method = "GET";
    protected String pathInfo;
    protected String pathTranslated;
    protected String queryString;
    protected String remoteUser;
    protected String requestURI;
    protected String servletPath;
    protected String characterEncoding;
    protected String protocol;

    protected StringBuffer requestURL;
    protected HttpSession session;
    protected ServletInputStream inputStream;
    protected Principal userPrincipal;
    protected ServletContext servletContext = MockServletContext.DEFAULT;
    protected HttpServletResponse response;

    protected Map<String, String> headers = new HashMap<>();
    protected Map<String, Object> attributeMap = new HashMap<>();
    protected Map<String, String[]> params = new HashMap<>();
    protected Set<Cookie> cookies = new HashSet<>();


    public MockHttpServletRequest() {
        super(MockProxy.create(HttpServletRequest.class));
    }

    @Override
    public String getContextPath() {
        if (contextPath == null) {
            contextPath = servletContext.getContextPath();
        }
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public void addHeader(String name, Object value) {
        headers.put(name, value.toString());
    }

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }


    @Override
    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }


    @Override
    public String getPathTranslated() {
        return pathTranslated;
    }

    public void setPathTranslated(String pathTranslated) {
        this.pathTranslated = pathTranslated;
    }


    @Override
    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }


    @Override
    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }


    @Override
    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }


    @Override
    public StringBuffer getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(StringBuffer requestURL) {
        this.requestURL = requestURL;
    }

    @Override
    public String getRequestedSessionId() {
        if (session != null) {
            return session.getId();
        }
        return null;
    }


    @Override
    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
        if (requestURI == null) {
            this.requestURI = getContextPath() + servletPath;
        }
    }


    @Override
    public HttpSession getSession() {
        return getSession(false);
    }


    @Override
    public HttpSession getSession(boolean create) {
        if (session != null) {
            return session;
        }

        String sessionId = getCookieValue("jsessionId");
        if (sessionId != null) {
            session = new MockHttpSession(sessionId, getServletContext());
            session.setMaxInactiveInterval(60 * 60);
        } else if (create) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
            session = new MockHttpSession(sessionId, getServletContext());
            session.setMaxInactiveInterval(60 * 60);
            setCookie("jsessionId", sessionId, -1);
        }
        return session;
    }

    /**
     * Get cookie value by cookie name.
     */
    private String getCookieValue(String name) {
        Cookie cookie = getCookieObject(name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * Get cookie object by cookie name.
     */
    private Cookie getCookieObject(String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
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
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInSeconds);
        response.addCookie(cookie);
    }


    @Override
    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }


    @Override
    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Vector<>(attributeMap.keySet()).elements();
    }


    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public int getContentLength() {
        String cl = this.getHeader("content-length");
        try {
            return Integer.parseInt(cl);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String getContentType() {
        return this.getHeader("content-type");
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (inputStream == null) {
            inputStream = new MockServletInputStream("");
        }
        return inputStream;
    }

    public void setInputStream(ServletInputStream ins) {
        this.inputStream = ins;
    }


    @Override
    public String getParameter(String key) {
        if (params.containsKey(key)) {
            return params.get(key)[0];
        }
        return null;
    }

    public void addParameter(String key, Number num) {
        addParameter(key, num.toString());
    }

    public void addParameter(String key, String[] values) {
        params.put(key, values);
    }

    public void addParameter(String key, String value) {
        params.put(key, new String[]{value});
    }

    public void addParameter(String key, Object value) {
        params.put(key, new String[]{String.valueOf(value)});
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector<>(params.keySet()).elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }


    @Override
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }


    @Override
    public Cookie[] getCookies() {
        return cookies.toArray(new Cookie[cookies.size()]);
    }

    public void setCookies(Set<Cookie> cookies) {
        this.cookies = cookies;
    }


    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getServerName() {
        return "localhost";
    }

    @Override
    public int getServerPort() {
        return 80;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getRemoteAddr() {
        return "127.0.0.1";
    }

    @Override
    public String getRemoteHost() {
        return "localhost";
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
