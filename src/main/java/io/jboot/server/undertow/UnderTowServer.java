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
import io.jboot.server.JbootServer;
import io.jboot.server.JbootServerConfig;
import io.jboot.utils.StringUtils;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import javax.servlet.DispatcherType;


public class UnderTowServer extends JbootServer {

    static Log log = Log.getLog(UnderTowServer.class);

    private DeploymentManager mDeploymentManager;
    private PathHandler mHandler;
    private Undertow mServer;


    public UnderTowServer(JbootServerConfig config) {
        super(config);
        initUndertowServer();
    }

    public void initUndertowServer() {
        DeploymentInfo deploymentInfo = Servlets.deployment()
                .setClassLoader(UnderTowServer.class.getClassLoader())
                .setResourceManager(new ClassPathResourceManager(UnderTowServer.class.getClassLoader()))
                .setContextPath(getConfig().getContextPath())
                .setDeploymentName("jboot")
                .setEagerFilterInit(true); //设置启动的时候，初始化servlet或filter（好吧，跟了很久的源代码...）


        deploymentInfo.addFilter(
                Servlets.filter("jboot", JFinalFilter.class)
                        .addInitParam("configClass", Jboot.getJbootConfig().getJfinalConfig()))
                .addFilterUrlMapping("jboot", "/*", DispatcherType.REQUEST);


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


        deploymentInfo.addServlets(
                Servlets.servlet("JbootResourceServlet", JbootResourceServlet.class)
                        .addMapping("/*"));

        mDeploymentManager = Servlets.defaultContainer().addDeployment(deploymentInfo);
        mDeploymentManager.deploy();


        HttpHandler httpHandler = null;
        try {
            /**
             * 启动并初始化servlet和filter
             */
            httpHandler = mDeploymentManager.start();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }


        mHandler = Handlers.path(
                Handlers.resource(new ClassPathResourceManager(UnderTowServer.class.getClassLoader(), "webRoot")))
                .addPrefixPath(getConfig().getContextPath(), httpHandler);

        mServer = Undertow.builder()
                .addHttpListener(getConfig().getPort(), getConfig().getHost())
                .setHandler(mHandler)
                .build();
    }

    @Override
    public boolean start() {
        try {
            mServer.start();
        } catch (Throwable ex) {
            log.error(ex.toString(), ex);
            stop();
            return false;
        }

        return true;
    }

    @Override
    public boolean stop() {
        mServer.stop();
        return true;
    }

}
