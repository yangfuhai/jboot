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
package io.jboot.app;

import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.server.undertow.WebBuilder;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.undertow.JbootUndertowConfig;

import javax.servlet.DispatcherType;
import java.net.URISyntaxException;
import java.net.URL;

public class JbootApplication {

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        start(createServer(args));
    }

    public static void start(UndertowServer server) {
        server.start();
        if (isDevMode()) {
            new JbootResourceLoader().start();
        }
    }

    /**
     * 创建 Undertow 服务器，public 用于可以给第三方创建创建着急的 Server
     *
     * @param args
     * @return 返回 UndertowServer
     */
    public static UndertowServer createServer(String[] args) {

        JbootConfigManager.me().parseArgs(args);

        JbootApplicationConfig appConfig = getConfig(JbootApplicationConfig.class);

        printBannerInfo(appConfig);
        printApplicationInfo(appConfig);
        printClassPath();

        UndertowConfig undertowConfig = new JbootUndertowConfig(appConfig.getJfinalConfig());
        undertowConfig.addSystemClassPrefix("io.jboot.app");
        undertowConfig.addHotSwapClassPrefix("io.jboot");

        return UndertowServer.create(undertowConfig)
                .setDevMode(isDevMode())
                .configWeb(webBuilder -> {
                    tryAddMetricsSupport(webBuilder);
                    tryAddShiroSupport(webBuilder);
                });
    }


    private static void tryAddMetricsSupport(WebBuilder webBuilder) {
        String url = getConfigValue("jboot.metric.url");
        String reporter = getConfigValue("jboot.metric.reporter");
        if (url != null && reporter != null) {
            webBuilder.addServlet("MetricsAdminServlet", "com.codahale.metrics.servlets.AdminServlet")
                    .addServletMapping("MetricsAdminServlet", url.endsWith("/*") ? url : url + "/*");
            webBuilder.addListener("io.jboot.support.metric.JbootMetricServletContextListener");
            webBuilder.addListener("io.jboot.support.metric.JbootHealthCheckServletContextListener");
        }
    }


    private static void tryAddShiroSupport(WebBuilder webBuilder) {
        String iniConfig = getConfigValue("jboot.shiro.ini");
        if (iniConfig != null) {
            String urlMapping = getConfigValue("jboot.shiro.urlMapping");
            if (urlMapping == null) urlMapping = "/*";
            webBuilder.addListener("org.apache.shiro.web.env.EnvironmentLoaderListener");
            webBuilder.addFilter("shiro", "io.jboot.support.shiro.JbootShiroFilter")
                    .addFilterUrlMapping("shiro", urlMapping, DispatcherType.REQUEST);

        }
    }


    private static void printBannerInfo(JbootApplicationConfig appConfig) {
        if (appConfig.isBannerEnable()) {
            System.out.println();
            System.out.println(Banner.getText(appConfig.getBannerFile()));
            System.out.println();
        }
    }

    private static void printApplicationInfo(JbootApplicationConfig appConfig) {
        System.out.println(appConfig.toString());
    }

    private static void printClassPath() {
        try {
            URL resourceURL = JbootApplication.class.getResource("/");
            if (resourceURL != null) {
                System.out.println("Classpath : " + resourceURL.toURI().getPath());
            } else {
                System.out.println("Classpath : application in one jar.");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    private static <T> T getConfig(Class<T> clazz) {
        return JbootConfigManager.me().get(clazz);
    }

    private static String getConfigValue(String key) {
        return JbootConfigManager.me().getConfigValue(key);
    }


    public static void setBootArg(String key, Object value) {
        JbootConfigManager.me().setBootArg(key, value);
    }

    private static boolean isDevMode() {
        return JbootConfigManager.me().isDevMode();
    }


}
