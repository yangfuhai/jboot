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
import io.jboot.utils.StrUtil;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
    protected String queryString = "";
    protected String requestURI;
    protected String servletPath;
    protected String characterEncoding = "UTF-8";
    protected String protocol = "HTTP/1.1";

    protected String remoteUser;
    protected String authType;
    protected Principal userPrincipal;

    protected StringBuffer requestURL;
    protected HttpSession session;
    protected ServletInputStream inputStream;

    private byte[] content;


    protected ServletContext servletContext = MockServletContext.DEFAULT;
    protected HttpServletResponse response;

    protected Map<String, String> headers = new HashMap<>();
    protected Map<String, Object> attributeMap = new HashMap<>();
    protected Map<String, String[]> parameters = new HashMap<>();
    protected Set<Cookie> cookies = new HashSet<>();
    protected LinkedList<Locale> locales = new LinkedList<>();


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
        return (this.pathInfo != null ? getRealPath(this.pathInfo) : null);
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
    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
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
        return getSession(true);
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


    @Override
    public String changeSessionId() {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        session = new MockHttpSession(sessionId, getServletContext());
        session.setMaxInactiveInterval(60 * 60);
        setCookie("jsessionId", sessionId, -1);
        return sessionId;
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
        return Collections.enumeration(attributeMap.keySet());
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


    public void setContent(byte[] content) {
        this.content = content;
    }


    @Override
    public long getContentLengthLong() {
        return (this.content != null ? this.content.length : -1);
    }


    @Override
    public String getParameter(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key)[0];
        }
        return null;
    }

    public void addParameter(String key, Number num) {
        addParameter(key, num.toString());
    }

    public void addParameter(String key, String value) {
        addParameter(key, new String[]{value});
    }

    public void addParameter(String key, Object value) {
        addParameter(key, new String[]{String.valueOf(value)});
    }


    public void addParameter(String key, String[] values) {
        parameters.put(key, values);

        if ("GET".equalsIgnoreCase(getMethod())) {
            updateQueryString();
        }
    }

    public void addQueryParameter(String key,Object value){

        if ("GET".equalsIgnoreCase(getMethod())) {
            parameters.put(key, new String[]{String.valueOf(value)});
        }

        Map queryStringMap = StrUtil.isNotBlank(queryString) ? StrUtil.queryStringToMap(this.queryString) : new HashMap();
        queryStringMap.put(key,value);
        setQueryString(StrUtil.mapToQueryString(queryStringMap));
    }

    private void updateQueryString() {
        StringBuilder sb = new StringBuilder();
        for (String key : parameters.keySet()) {
            if (key == null || key.length() == 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }

            sb.append(key.trim()).append("=");
            String[] values = parameters.get(key);
            if (values == null || values.length == 0) {
                continue;
            }

            if (values.length == 1) {
                sb.append(StrUtil.urlEncode(values[0]));
            } else {
                for (int i = 0; i < values.length; i++) {
                    if (i == 0) {
                        sb.append(StrUtil.urlEncode(values[i]));
                    } else {
                        if (sb.length() > 0) {
                            sb.append("&");
                        }
                        sb.append(key.trim()).append("=").append(StrUtil.urlEncode(values[i]));
                        ;
                    }
                }
            }
        }

        setQueryString(sb.toString());
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
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
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(locales);
    }

    public void setLocales(LinkedList<Locale> locales) {
        this.locales = locales;
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

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return true;
    }

    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public Locale getLocale() {
        return this.locales.getFirst();
    }

    @Override
    public int getLocalPort() {
        return 80;
    }

    @Override
    public String getLocalAddr() {
        return "127.0.0.1";
    }

    @Override
    public String getLocalName() {
        return "localhost";
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public long getDateHeader(String name) {
        return Long.valueOf(getHeader(name));
    }

    @Override
    public DispatcherType getDispatcherType() {
        return DispatcherType.REQUEST;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String header = getHeader(name);
        String[] headers = header.split(";");
        return Collections.enumeration(Arrays.asList(headers));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        return Integer.valueOf(getHeader(name));
    }

    @Override
    public void logout() throws ServletException {
        this.userPrincipal = null;
        this.remoteUser = null;
        this.authType = null;
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
