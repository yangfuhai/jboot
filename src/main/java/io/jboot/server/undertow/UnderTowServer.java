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
package io.jboot.server.undertow;

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
import io.jboot.utils.StringUtils;
import io.jboot.web.JbootWebConfig;
import io.jboot.web.websocket.JbootWebsocketManager;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContextListener;
import java.util.Map;
import java.util.Set;


public class UnderTowServer extends JbootServer {

    static Log log = Log.getLog(UnderTowServer.class);

    private DeploymentManager deploymentManager;
    private DeploymentInfo deploymentInfo;
    private PathHandler pathHandler;
    private Undertow undertow;
    private ServletContainer servletContainer;
    private JbootServerConfig config;
    private JbootWebConfig webConfig;


    public UnderTowServer() {
        config = Jboot.config(JbootServerConfig.class);
        webConfig = Jboot.config(JbootWebConfig.class);

    }

    public void initUndertowServer() {


        JbootServerClassloader classloader = new JbootServerClassloader(UnderTowServer.class.getClassLoader());
        classloader.setDefaultAssertionStatus(false);


        deploymentInfo = buildDeploymentInfo(classloader);

        if (webConfig.isWebsocketEnable()) {
            Set<Class> endPointClasses = JbootWebsocketManager.me().getWebsocketEndPoints();
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(true, webConfig.getWebsocketBufferPoolSize()));
            for (Class endPointClass : endPointClasses) {
                webSocketDeploymentInfo.addEndpoint(endPointClass);
            }
            deploymentInfo.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, webSocketDeploymentInfo);
        }

        servletContainer = Servlets.newContainer();
        deploymentManager = servletContainer.addDeployment(deploymentInfo);
        deploymentManager.deploy();

        HttpHandler httpHandler = null;
        try {
            /**
             * 启动并初始化servlet和filter
             */
            httpHandler = deploymentManager.start();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }


        pathHandler = Handlers.path(
                Handlers.resource(new ClassPathResourceManager(classloader, "webRoot")));

        pathHandler.addPrefixPath(config.getContextPath(), httpHandler);

        undertow = Undertow.builder()
                .addHttpListener(config.getPort(), config.getHost())
                .setHandler(pathHandler)
                .build();

    }

    private DeploymentInfo buildDeploymentInfo(JbootServerClassloader classloader) {
        DeploymentInfo deploymentInfo = Servlets.deployment()
                .setClassLoader(classloader)
                .setResourceManager(new ClassPathResourceManager(classloader))
                .setContextPath(config.getContextPath())
                .setDeploymentName("jboot" + StringUtils.uuid())
                .setEagerFilterInit(true); //设置启动的时候，初始化servlet或filter


        JbootShiroConfig shiroConfig = Jboot.config(JbootShiroConfig.class);
        if (shiroConfig.isConfigOK()) {
            deploymentInfo.addListeners(Servlets.listener(EnvironmentLoaderListener.class));
            deploymentInfo.addFilter(
                    Servlets.filter("shiro", ShiroFilter.class))
                    .addFilterUrlMapping("shiro", "/*", DispatcherType.REQUEST);
        }


        deploymentInfo.addFilter(
                Servlets.filter("jfinal", JFinalFilter.class)
                        .addInitParam("configClass", Jboot.me().getJbootConfig().getJfinalConfig()))
                .addFilterUrlMapping("jfinal", "/*", DispatcherType.REQUEST);


        JbootHystrixConfig hystrixConfig = Jboot.config(JbootHystrixConfig.class);
        if (StringUtils.isNotBlank(hystrixConfig.getUrl())) {
            deploymentInfo.addServlets(
                    Servlets.servlet("HystrixMetricsStreamServlet", HystrixMetricsStreamServlet.class)
                            .addMapping(hystrixConfig.getUrl()));
        }


        JbootMetricsConfig metricsConfig = Jboot.config(JbootMetricsConfig.class);
        if (StringUtils.isNotBlank(metricsConfig.getUrl())) {
            deploymentInfo.addServlets(
                    Servlets.servlet("MetricsAdminServlet", AdminServlet.class)
                            .addMapping(metricsConfig.getUrl()));

            deploymentInfo.addListeners(Servlets.listener(JbootMetricsServletContextListener.class));
            deploymentInfo.addListeners(Servlets.listener(JbootHealthCheckServletContextListener.class));
        }


        io.jboot.server.Servlets jbootServlets = new io.jboot.server.Servlets();
        ContextListeners listeners = new ContextListeners();

        JbootAppListenerManager.me().onJbootDeploy(jbootServlets, listeners);


        for (Map.Entry<String, io.jboot.server.Servlets.ServletInfo> entry : jbootServlets.getServlets().entrySet()) {
            ServletInfo servletInfo = Servlets.servlet(entry.getKey(), entry.getValue().getServletClass()).addMappings(entry.getValue().getUrlMapping());
            deploymentInfo.addServlet(servletInfo);
        }

        for (Class<? extends ServletContextListener> listenerClass : listeners.getListeners()) {
            deploymentInfo.addListeners(Servlets.listener(listenerClass));
        }


        deploymentInfo.addServlets(
                Servlets.servlet("JbootResourceServlet", JbootResourceServlet.class)
                        .addMapping("/*"));

        return deploymentInfo;
    }


    @Override
    public boolean start() {
        try {
            initUndertowServer();
            JbootAppListenerManager.me().onAppStartBefore(this);
            undertow.start();
        } catch (Throwable ex) {
            log.error(ex.toString(), ex);
            stop();
            return false;
        }
        return true;
    }

    @Override
    public boolean restart() {
        try {
            stop();
            start();
            System.err.println("undertow restarted!!!");
        } catch (Throwable ex) {
            return false;
        }

        return true;
    }


    @Override
    public boolean stop() {

        deploymentManager.undeploy();
        servletContainer.removeDeployment(deploymentInfo);

        if (pathHandler != null) {
            pathHandler.clearPaths();
        }
        if (undertow != null) {
            undertow.stop();
        }

        return true;
    }

    public DeploymentManager getDeploymentManager() {
        return deploymentManager;
    }

    public DeploymentInfo getDeploymentInfo() {
        return deploymentInfo;
    }

    public PathHandler getPathHandler() {
        return pathHandler;
    }

    public Undertow getUndertow() {
        return undertow;
    }

    public ServletContainer getServletContainer() {
        return servletContainer;
    }

    public JbootServerConfig getConfig() {
        return config;
    }
}
