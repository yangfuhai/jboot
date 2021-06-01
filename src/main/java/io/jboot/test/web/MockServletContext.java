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

import com.jfinal.kit.PathKit;
import io.jboot.test.MockExceptions;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MockServletContext implements ServletContext {
    public static MockServletContext DEFAULT = new MockServletContext();

    protected Map<String, String> initParameters = new HashMap<>();
    protected Map<String, Object> attributes = new HashMap<>();

    protected String contextPath = "";
    protected String servletContextName = "";

    @Override
    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public ServletContext getContext(String uripath) {
        throw MockExceptions.unsupported;
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public String getMimeType(String file) {
        throw MockExceptions.unsupported;
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        try {
            HashSet<String> hashSet = new HashSet<>();
            Enumeration<URL> enumeration = getClass().getClassLoader().getResources(path);
            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();
                hashSet.add(url.toString());
            }
            return hashSet;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return getClass().getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return getClass().getResourceAsStream(path);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw MockExceptions.unsupported;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        throw MockExceptions.unsupported;
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        throw MockExceptions.unsupported;
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        throw MockExceptions.unsupported;
    }

    @Override
    public Enumeration<String> getServletNames() {
        throw MockExceptions.unsupported;
    }

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
        System.out.println(msg);
        exception.printStackTrace();
    }

    @Override
    public void log(String message, Throwable throwable) {
        System.out.println(message);
        throwable.printStackTrace();
    }


    @Override
    public String getRealPath(String path) {
        return new File(PathKit.getWebRootPath(), path).getAbsolutePath();
    }

    @Override
    public String getServerInfo() {
        return "Jboot Mock Server 1.0";
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return new Vector<>(initParameters.keySet()).elements();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return null != initParameters.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Vector<>(attributes.keySet()).elements();
    }

    @Override
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public String getServletContextName() {
        return servletContextName;
    }

    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        throw MockExceptions.unsupported;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        throw MockExceptions.unsupported;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        throw MockExceptions.unsupported;
    }

    @Override
    public ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
        throw MockExceptions.unsupported;
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        throw MockExceptions.unsupported;
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        throw MockExceptions.unsupported;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        throw MockExceptions.unsupported;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        throw MockExceptions.unsupported;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        throw MockExceptions.unsupported;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        throw MockExceptions.unsupported;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        throw MockExceptions.unsupported;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        throw MockExceptions.unsupported;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        throw MockExceptions.unsupported;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    @Override
    public void addListener(String className) {

    }

    @Override
    public <T extends EventListener> void addListener(T t) {

    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {

    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void declareRoles(String... roleNames) {

    }

    @Override
    public String getVirtualServerName() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int sessionTimeout) {

    }

    @Override
    public String getRequestCharacterEncoding() {
        return null;
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) {

    }

    @Override
    public String getResponseCharacterEncoding() {
        return null;
    }

    @Override
    public void setResponseCharacterEncoding(String encoding) {

    }
}
