/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core;

import com.jfinal.aop.Aop;
import com.jfinal.aop.AopManager;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.json.JsonManager;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.template.Engine;
import io.jboot.Jboot;
import io.jboot.aop.JbootAopFactory;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.components.gateway.JbootGatewayHandler;
import io.jboot.components.gateway.JbootGatewayManager;
import io.jboot.components.limiter.LimiterManager;
import io.jboot.components.rpc.JbootrpcManager;
import io.jboot.components.schedule.JbootScheduleManager;
import io.jboot.core.listener.JbootAppListenerManager;
import io.jboot.core.log.Slf4jLogFactory;
import io.jboot.db.ArpManager;
import io.jboot.support.metric.JbootMetricConfig;
import io.jboot.support.seata.JbootSeataManager;
import io.jboot.support.shiro.JbootShiroManager;
import io.jboot.support.swagger.JbootSwaggerConfig;
import io.jboot.support.swagger.JbootSwaggerController;
import io.jboot.support.swagger.JbootSwaggerManager;
import io.jboot.utils.*;
import io.jboot.web.json.JbootJson;
import io.jboot.web.attachment.AttachmentHandler;
import io.jboot.web.attachment.LocalAttachmentContainerConfig;
import io.jboot.web.controller.JbootControllerManager;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.annotation.JFinalSharedMethod;
import io.jboot.web.directive.annotation.JFinalSharedObject;
import io.jboot.web.directive.annotation.JFinalSharedStaticMethod;
import io.jboot.web.handler.JbootActionHandler;
import io.jboot.web.handler.JbootHandler;
import io.jboot.web.handler.JbootMetricsHandler;
import io.jboot.web.render.JbootRenderFactory;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;


public class JbootCoreConfig extends JFinalConfig {

    private List<Routes.Route> routeList = new ArrayList<>();


    public JbootCoreConfig() {

        initSystemProperties();

        // 自动为 Interceptor 和 Controller 等添加依赖注入
        AopManager.me().setInjectDependency(true);

        AopManager.me().setAopFactory(JbootAopFactory.me());

        Aop.inject(this);

        JbootAppListenerManager.me().onInit();
    }


    /**
     * 设置必要的系统参数：
     * 有些组件，比如 apollo、sentinel 等配置需要通过 System Properites来进行配置的
     */
    private void initSystemProperties() {

        //加载 jboot-system.properties 代替启动参数的 -D 配置
        File spf = new File(PathKit.getRootClassPath(), "jboot-system.properties");
        if (spf.exists() && spf.isFile()) {
            Properties properties = PropKit.use(spf).getProperties();
            if (properties != null && !properties.isEmpty()) {
                for (Object key : properties.keySet()) {
                    if (StrUtil.isNotBlank(key)) {
                        String newKey = key.toString().trim();
                        String systemValue = System.getProperty(newKey);
                        if (StrUtil.isNotBlank(systemValue)) {
                            continue;
                        }
                        String newValue = properties.getProperty(newKey);
                        if (StrUtil.isNotBlank(newValue)) {
                            System.setProperty(newKey, newValue.trim());
                        }
                    }
                }
            }
        }
    }


    @Override
    public void configConstant(Constants constants) {

        constants.setRenderFactory(JbootRenderFactory.me());
        constants.setDevMode(Jboot.isDevMode());
//        ApiConfigKit.setDevMode(Jboot.isDevMode());
//
//        JbootWechatConfig config = Jboot.config(JbootWechatConfig.class);
//        ApiConfig apiConfig = config.getApiConfig();
//        if (apiConfig != null) {
//            ApiConfigKit.putApiConfig(apiConfig);
//        }

        constants.setLogFactory(new Slf4jLogFactory());
        constants.setMaxPostSize(1024 * 1024 * 2000);
        constants.setReportAfterInvocation(false);

        constants.setControllerFactory(JbootControllerManager.me());
        constants.setJsonFactory(JbootJson::new);
        constants.setInjectDependency(true);

        constants.setBaseUploadPath(LocalAttachmentContainerConfig.getInstance().buildUploadAbsolutePath());

        JbootAppListenerManager.me().onConstantConfig(constants);

    }


    @Override
    public void configRoute(Routes routes) {

        routes.setMappingSuperClass(true);

        List<Class<Controller>> controllerClassList = ClassScanner.scanSubClass(Controller.class);
        if (ArrayUtil.isNotEmpty(controllerClassList)) {
            for (Class<Controller> clazz : controllerClassList) {
                RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
                if (mapping == null) {
                    continue;
                }

                String value = AnnotationUtil.get(mapping.value());
                if (value == null) {
                    continue;
                }

                String viewPath = AnnotationUtil.get(mapping.viewPath());

                if (StrUtil.isNotBlank(viewPath)) {
                    routes.add(value, clazz, viewPath);
                } else {
                    routes.add(value, clazz);
                }
            }
        }

        JbootSwaggerConfig swaggerConfig = Jboot.config(JbootSwaggerConfig.class);
        if (swaggerConfig.isConfigOk()) {
            routes.add(swaggerConfig.getPath(), JbootSwaggerController.class, swaggerConfig.getPath());
        }

        JbootAppListenerManager.me().onRouteConfig(routes);

        for (Routes.Route route : routes.getRouteItemList()) {
            JbootControllerManager.me().setMapping(route.getControllerPath(), route.getControllerClass());
        }

        routeList.addAll(routes.getRouteItemList());
    }

    @Override
    public void configEngine(Engine engine) {

        //通过 java -jar xxx.jar 在单独的jar里运行
        if (runInFatjar()) {
            engine.setToClassPathSourceFactory();
            engine.setBaseTemplatePath("webapp");
        }

        List<Class> directiveClasses = ClassScanner.scanClass();
        for (Class clazz : directiveClasses) {
            JFinalDirective directive = (JFinalDirective) clazz.getAnnotation(JFinalDirective.class);
            if (directive != null) {
                if (directive.override()) {
                    //remove old directive
                    engine.removeDirective(directive.value());
                }
                engine.addDirective(AnnotationUtil.get(directive.value()), clazz);
            }

            JFinalSharedMethod sharedMethod = (JFinalSharedMethod) clazz.getAnnotation(JFinalSharedMethod.class);
            if (sharedMethod != null) {
                engine.addSharedMethod(ClassUtil.newInstance(clazz));
            }

            JFinalSharedStaticMethod sharedStaticMethod = (JFinalSharedStaticMethod) clazz.getAnnotation(JFinalSharedStaticMethod.class);
            if (sharedStaticMethod != null) {
                engine.addSharedStaticMethod(clazz);
            }

            JFinalSharedObject sharedObject = (JFinalSharedObject) clazz.getAnnotation(JFinalSharedObject.class);
            if (sharedObject != null) {
                engine.addSharedObject(AnnotationUtil.get(sharedObject.value()), ClassUtil.newInstance(clazz));
            }
        }

        JbootAppListenerManager.me().onEngineConfig(engine);
    }

    private boolean runInFatjar() {
        return Thread.currentThread().getContextClassLoader().getResource("") == null;
    }

    @Override
    public void configPlugin(Plugins plugins) {

        List<ActiveRecordPlugin> arps = ArpManager.me().getActiveRecordPlugins();
        for (ActiveRecordPlugin arp : arps) {
            plugins.add(arp);
        }

        JbootAppListenerManager.me().onPluginConfig(new JfinalPlugins(plugins));

    }


    @Override
    public void configInterceptor(Interceptors interceptors) {
        // 拦截器的 inject 通过  AopManager.me().setInjectDependency(true); 去配置
        JbootAppListenerManager.me().onInterceptorConfig(interceptors);
    }

    @Override
    public void configHandler(Handlers handlers) {

        //先添加用户的handler，再添加jboot自己的handler
        //用户的 handler 优先于 jboot 的 handler 执行
        JbootAppListenerManager.me().onHandlerConfig(new JfinalHandlers(handlers));

        handlers.add(new JbootGatewayHandler());
        handlers.add(new AttachmentHandler());

        //metrics 处理
        JbootMetricConfig metricsConfig = Jboot.config(JbootMetricConfig.class);
        if (metricsConfig.isConfigOk()) {
            handlers.add(new JbootMetricsHandler(metricsConfig.getUrl()));
        }


        handlers.add(new JbootHandler());

        //若用户自己没配置 ActionHandler，默认使用 JbootActionHandler
        if (handlers.getActionHandler() == null) {
            handlers.setActionHandler(new JbootActionHandler());
        }

    }

    @Override
    public void onStart() {

        JbootAppListenerManager.me().onStartBefore();

        JsonManager.me().setDefaultDatePattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 初始化
         */
        JbootrpcManager.me().init();
        JbootShiroManager.me().init(routeList);
        JbootScheduleManager.me().init();
        JbootSwaggerManager.me().init();
        LimiterManager.me().init();
        JbootSeataManager.me().init();
        JbootGatewayManager.me().init();

        JbootAppListenerManager.me().onStart();

        //使用场景：需要等所有组件 onStart() 完成之后，再去执行某些工作的时候
        JbootAppListenerManager.me().onStartFinish();
    }

    @Override
    public void onStop() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        if (drivers != null) {
            while (drivers.hasMoreElements()) {
                try {
                    Driver driver = drivers.nextElement();
                    DriverManager.deregisterDriver(driver);
                } catch (Exception e) {
                    LogKit.error(e.toString(), e);
                }
            }
        }
        JbootAppListenerManager.me().onStop();
        JbootScheduleManager.me().stop();
        JbootSeataManager.me().stop();
        JbootrpcManager.me().stop();
    }


}
