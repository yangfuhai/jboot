/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.server.jetty;


import com.codahale.metrics.servlets.AdminServlet;
import com.jfinal.core.JFinalFilter;
import com.jfinal.log.Log;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import io.jboot.Jboot;
import io.jboot.component.hystrix.JbootHystrixConfig;
import io.jboot.component.metrics.JbootHealthCheckServletContextListener;
import io.jboot.component.metrics.JbootMetricsConfig;
import io.jboot.component.metrics.JbootMetricsServletContextListener;
import io.jboot.component.shiro.JbootShiroConfig;
import io.jboot.server.ContextListeners;
import io.jboot.server.JbootServer;
import io.jboot.server.JbootServerConfig;
import io.jboot.server.JbootServerClassloader;
import io.jboot.server.listener.JbootAppListenerManager;
import io.jboot.utils.ClassKits;
import io.jboot.utils.StringUtils;
import io.jboot.web.JbootWebConfig;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Map;

public class JettyServer extends JbootServer {

    private static Log log = Log.getLog(JettyServer.class);

    private JbootServerConfig config;
    private JbootWebConfig webConfig;

    private Server jettyServer;
    private ServletContextHandler handler;

    public JettyServer() {
        config = Jboot.config(JbootServerConfig.class);
        webConfig = Jboot.config(JbootWebConfig.class);
    }

    @Override
    public boolean start() {
        try {
            initJettyServer();
            JbootAppListenerManager.me().onAppStartBefore(this);
            jettyServer.start();
        } catch (Throwable ex) {
            log.error(ex.toString(), ex);
            stop();
            return false;
        }
        return true;
    }

    private void initJettyServer() {
        InetSocketAddress address = new InetSocketAddress(config.getHost(), config.getPort());
        jettyServer = new Server(address);

        handler = new ServletContextHandler();
        handler.setContextPath(config.getContextPath());
        handler.setClassLoader(new JbootServerClassloader(JettyServer.class.getClassLoader()));
        handler.setResourceBase(getRootClassPath());

        JbootShiroConfig shiroConfig = Jboot.config(JbootShiroConfig.class);
        if (shiroConfig.isConfigOK()) {
            handler.addEventListener(new EnvironmentLoaderListener());
            handler.addFilter(ShiroFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        }

        //JFinal
        FilterHolder jfinalFilter = handler.addFilter(JFinalFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        jfinalFilter.setInitParameter("configClass", Jboot.me().getJbootConfig().getJfinalConfig());

        JbootHystrixConfig hystrixConfig = Jboot.config(JbootHystrixConfig.class);
        if (StringUtils.isNotBlank(hystrixConfig.getUrl())) {
            handler.addServlet(HystrixMetricsStreamServlet.class, hystrixConfig.getUrl());
        }


        JbootMetricsConfig metricsConfig = Jboot.config(JbootMetricsConfig.class);
        if (StringUtils.isNotBlank(metricsConfig.getUrl())) {
            handler.addEventListener(new JbootMetricsServletContextListener());
            handler.addEventListener(new JbootHealthCheckServletContextListener());
            handler.addServlet(AdminServlet.class, metricsConfig.getUrl());
        }

        io.jboot.server.Servlets jbootServlets = new io.jboot.server.Servlets();
        ContextListeners listeners = new ContextListeners();

        JbootAppListenerManager.me().onJbootDeploy(jbootServlets, listeners);


        for (Map.Entry<String, io.jboot.server.Servlets.ServletInfo> entry : jbootServlets.getServlets().entrySet()) {
            for (String path : entry.getValue().getUrlMapping()) {
                handler.addServlet(entry.getValue().getServletClass(), path);
            }
        }


        for (Class<? extends ServletContextListener> listenerClass : listeners.getListeners()) {
            handler.addEventListener(ClassKits.newInstance(listenerClass));
        }

        jettyServer.setHandler(handler);
    }

    private static String getRootClassPath() {
        String path = null;
        try {
            path = JettyServer.class.getClassLoader().getResource("").toURI().getPath();
            return new File(path).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return path;
    }

    @Override
    public boolean restart() {
        stop();
        start();
        return true;
    }

    @Override
    public boolean stop() {
        try {
            jettyServer.stop();
            return true;
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return false;
    }


    public Server getJettyServer() {
        return jettyServer;
    }


    public ServletContextHandler getHandler() {
        return handler;
    }


}
