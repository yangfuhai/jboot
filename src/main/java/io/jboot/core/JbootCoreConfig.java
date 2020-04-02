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
import io.jboot.aop.JbootAopInterceptor;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.app.config.support.apollo.ApolloConfigManager;
import io.jboot.app.config.support.apollo.ApolloServerConfig;
import io.jboot.app.config.support.nacos.NacosConfigManager;
import io.jboot.components.gateway.JbootGatewayHandler;
import io.jboot.components.gateway.JbootGatewayManager;
import io.jboot.components.limiter.LimiterManager;
import io.jboot.components.restful.JbootRestfulManager;
import io.jboot.components.restful.RestfulHandler;
import io.jboot.components.restful.annotation.RestController;
import io.jboot.components.rpc.JbootrpcManager;
import io.jboot.components.schedule.JbootScheduleManager;
import io.jboot.core.listener.JbootAppListenerManager;
import io.jboot.core.log.Slf4jLogFactory;
import io.jboot.db.ArpManager;
import io.jboot.support.seata.JbootSeataManager;
import io.jboot.support.sentinel.SentinelManager;
import io.jboot.support.shiro.JbootShiroManager;
import io.jboot.support.swagger.JbootSwaggerConfig;
import io.jboot.support.swagger.JbootSwaggerController;
import io.jboot.support.swagger.JbootSwaggerManager;
import io.jboot.utils.*;
import io.jboot.web.JbootJson;
import io.jboot.web.controller.JbootControllerManager;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.annotation.JFinalSharedMethod;
import io.jboot.web.directive.annotation.JFinalSharedObject;
import io.jboot.web.directive.annotation.JFinalSharedStaticMethod;
import io.jboot.web.fixedinterceptor.FixedInterceptors;
import io.jboot.web.handler.JbootActionHandler;
import io.jboot.web.handler.JbootFilterHandler;
import io.jboot.web.handler.JbootHandler;
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

    private JbootRestfulManager.Config restfulConfig = new JbootRestfulManager.Config();


    public JbootCoreConfig() {

        initSystemProperties();

        ApolloConfigManager.me().init();
        NacosConfigManager.me().init();

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

        //apollo 配置
        ApolloServerConfig apolloConfig = Jboot.config(ApolloServerConfig.class);
        if (apolloConfig.isEnable() && apolloConfig.isConfigOk()) {
            System.setProperty("app.id", apolloConfig.getAppId());
            System.setProperty("apollo.meta", apolloConfig.getMeta());
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
        constants.setJsonFactory(() -> new JbootJson());
        constants.setInjectDependency(true);


        JbootAppListenerManager.me().onConstantConfig(constants);

    }


    @Override
    public void configRoute(Routes routes) {

        routes.setMappingSuperClass(true);

        List<Routes.Route> restfulRoutes = new ArrayList<>();

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

                //检查是否是restful类型的controller，如果是则加入restful专门指定的routes
                RestController restController = clazz.getAnnotation(RestController.class);
                if(restController != null){
                    restfulRoutes.add(new Routes.Route(value, clazz, value));
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
            JbootControllerManager.me().setMapping(route.getControllerKey(), route.getControllerClass());
        }

        if( !restfulRoutes.isEmpty() ){
            //处理restful专属的routes
            restfulConfig.setRoutes(restfulRoutes)
                    .setBaseViewPath(routes.getBaseViewPath())
                    .setMappingSupperClass(routes.getMappingSuperClass())
                    .setRouteInterceptors(routes.getInterceptors());
            for (Routes.Route route : restfulRoutes) {
                JbootControllerManager.me().setMapping(route.getControllerKey(), route.getControllerClass());
            }
            routeList.addAll(restfulRoutes);
        }

        routeList.addAll(routes.getRouteItemList());
    }

    @Override
    public void configEngine(Engine engine) {

        //通过 java -jar xxx.jar 在单独的jar里运行
        if (runInFatjar()) {
            engine.setToClassPathSourceFactory();
            engine.setBaseTemplatePath(null);
        }

        List<Class> directiveClasses = ClassScanner.scanClass();
        for (Class clazz : directiveClasses) {
            JFinalDirective directive = (JFinalDirective) clazz.getAnnotation(JFinalDirective.class);
            if (directive != null) {
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

        interceptors.addGlobalServiceInterceptor(new JbootAopInterceptor());

        JbootAppListenerManager.me().onInterceptorConfig(interceptors);
        JbootAppListenerManager.me().onFixedInterceptorConfig(FixedInterceptors.me());
    }

    @Override
    public void configHandler(Handlers handlers) {

        //先添加用户的handler，再添加jboot自己的handler
        //用户的handler优先于jboot的handler执行
        JbootAppListenerManager.me().onHandlerConfig(new JfinalHandlers(handlers));

        handlers.add(new JbootGatewayHandler());
        handlers.add(new JbootFilterHandler());
        handlers.add(new JbootHandler());
        handlers.setActionHandler(new RestfulHandler());

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
        SentinelManager.me().init();
        JbootGatewayManager.me().init();
        JbootRestfulManager.me().init(restfulConfig);


        JbootAppListenerManager.me().onStart();
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
