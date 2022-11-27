/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.core.Path;
import com.jfinal.core.converter.TypeConverter;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.template.Directive;
import com.jfinal.template.Engine;
import io.jboot.Jboot;
import io.jboot.aop.JbootAopFactory;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.app.ApplicationUtil;
import io.jboot.components.cache.support.JbootCaptchaCache;
import io.jboot.components.cache.support.JbootTokenCache;
import io.jboot.components.gateway.JbootGatewayHandler;
import io.jboot.components.gateway.JbootGatewayManager;
import io.jboot.components.limiter.LimiterManager;
import io.jboot.components.mq.JbootmqManager;
import io.jboot.components.rpc.JbootrpcManager;
import io.jboot.components.schedule.JbootScheduleManager;
import io.jboot.core.listener.JbootAppListenerManager;
import io.jboot.core.log.JbootLogFactory;
import io.jboot.db.ArpManager;
import io.jboot.support.metric.JbootMetricConfig;
import io.jboot.support.metric.MetricServletHandler;
import io.jboot.support.metric.request.JbootRequestMetricHandler;
import io.jboot.support.seata.JbootSeataManager;
import io.jboot.support.sentinel.JbootSentinelManager;
import io.jboot.support.sentinel.SentinelConfig;
import io.jboot.support.sentinel.SentinelHandler;
import io.jboot.support.shiro.JbootShiroManager;
import io.jboot.support.swagger.JbootSwaggerConfig;
import io.jboot.support.swagger.JbootSwaggerController;
import io.jboot.support.swagger.JbootSwaggerManager;
import io.jboot.utils.*;
import io.jboot.web.JbootActionMapping;
import io.jboot.web.JbootWebConfig;
import io.jboot.web.PathVariableActionMapping;
import io.jboot.web.attachment.AttachmentHandler;
import io.jboot.web.attachment.LocalAttachmentContainerConfig;
import io.jboot.web.controller.JbootControllerManager;
import io.jboot.web.controller.annotation.GetMapping;
import io.jboot.web.controller.annotation.PostMapping;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.converter.ArrayConverters;
import io.jboot.web.converter.TypeConverterFunc;
import io.jboot.web.directive.JbootOutputDirectiveFactory;
import io.jboot.web.directive.SharedEnumObject;
import io.jboot.web.directive.annotation.*;
import io.jboot.web.handler.JbootActionHandler;
import io.jboot.web.handler.JbootHandler;
import io.jboot.web.handler.PathVariableActionHandler;
import io.jboot.web.json.JbootJson;
import io.jboot.web.render.JbootRenderFactory;
import io.jboot.web.xss.XSSHandler;
import io.jboot.wechat.WechatSupport;

import java.io.File;
import java.util.ArrayList;
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

        initWebRootPath();

        JbootAppListenerManager.me().onInit();
    }


    /**
     * apollo、sentinel 等配置需要通过 System Properites 来进行配置的
     * 而 System Properties 的配置需要在启动的时候同 java -D 添加配置，极为不方便
     * 此时，可以添加在 jboot-system.properties 里添加，来代替 java -D 的情况
     */
    private void initSystemProperties() {
        //加载 jboot-system.properties 代替启动参数的 -D 配置
        File systemPropFile = new File(PathKit.getRootClassPath(), "jboot-system.properties");
        if (systemPropFile.exists() && systemPropFile.isFile()) {
            Properties properties = PropKit.use(systemPropFile).getProperties();
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


    /**
     * 在 JFinal.initPathKit() 这个方法中，如果 webRootPath 会为 null
     * 其会去通过 PathKit.detectWebRootPath() 去初始化一个错误的路径
     * 此方法的目的是为了防止 webRootPath 为 null
     */
    private void initWebRootPath() {
        String webRootPath = ReflectUtil.getStaticFieldValue(PathKit.class, "webRootPath");
        if (webRootPath == null) {
            PathKit.setWebRootPath(PathKit.getRootClassPath());
        }
    }


    @Override
    public void configConstant(Constants constants) {

        JbootAppListenerManager.me().onConstantConfigBefore(constants);

        constants.setRenderFactory(JbootRenderFactory.me());
        constants.setDevMode(Jboot.isDevMode());

        constants.setLogFactory(new JbootLogFactory());
        constants.setMaxPostSize(1024 * 1024 * 2000);
        constants.setReportAfterInvocation(false);

        constants.setControllerFactory(JbootControllerManager.me());
        constants.setJsonFactory(JbootJson::new);
        constants.setInjectDependency(true);

        constants.setTokenCache(new JbootTokenCache());
        constants.setCaptchaCache(new JbootCaptchaCache());

        constants.setBaseUploadPath(LocalAttachmentContainerConfig.getInstance().buildUploadAbsolutePath());
        constants.setJsonDatePattern(DateUtil.datetimePattern);

        if (JbootWebConfig.getInstance().isPathVariableEnable()) {
            constants.setActionMapping(PathVariableActionMapping::new);
        } else {
            constants.setActionMapping(JbootActionMapping::new);
        }

        JbootAppListenerManager.me().onConstantConfig(constants);

    }


    @Override
    public void configRoute(Routes routes) {

        routes.setMappingSuperClass(true);

        List<Class<Controller>> controllerClassList = ClassScanner.scanSubClass(Controller.class);
        if (ArrayUtil.isNotEmpty(controllerClassList)) {
            for (Class<Controller> clazz : controllerClassList) {
                String[] valueAndViewPath = getMappingAndViewPath(clazz);
                if (valueAndViewPath != null) {
                    initRoutes(routes, clazz, valueAndViewPath[0], valueAndViewPath[1]);
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

    private String removeLastSlash(String path) {
        while (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String[] getMappingAndViewPath(Class<? extends Controller> clazz) {
        RequestMapping rm = clazz.getAnnotation(RequestMapping.class);
        if (rm != null) {
            return new String[]{AnnotationUtil.get(rm.value()), AnnotationUtil.get(rm.viewPath())};
        }

        Path path = clazz.getAnnotation(Path.class);
        if (path != null) {
            return new String[]{AnnotationUtil.get(path.value()), AnnotationUtil.get(path.viewPath())};
        }

        GetMapping gp = clazz.getAnnotation(GetMapping.class);
        if (gp != null) {
            return new String[]{AnnotationUtil.get(gp.value()), AnnotationUtil.get(gp.viewPath())};
        }

        PostMapping pp = clazz.getAnnotation(PostMapping.class);
        if (pp != null) {
            return new String[]{AnnotationUtil.get(pp.value()), AnnotationUtil.get(pp.viewPath())};
        }

        return null;
    }


    private void initRoutes(Routes routes, Class<Controller> controllerClass, String path, String viewPath) {
        if (StrUtil.isBlank(path)) {
            return;
        } else {
            path = AnnotationUtil.get(path);
        }

        path = removeLastSlash(path);
        viewPath = AnnotationUtil.get(viewPath);

        if (Path.NULL_VIEW_PATH.equals(viewPath)) {
            routes.add(path, controllerClass);
        } else {
            routes.add(path, controllerClass, viewPath);
        }
    }


    @Override
    public void configEngine(Engine engine) {

        engine.setOutputDirectiveFactory(JbootOutputDirectiveFactory.me);

        //通过 java -jar xxx.jar 在单独的jar里运行
        if (ApplicationUtil.runInFatjar()) {
            engine.setToClassPathSourceFactory();
            engine.setBaseTemplatePath("webapp");
        }

        List<Class> directiveClasses = ClassScanner.scanClass();
        for (Class<?> clazz : directiveClasses) {

            if (Directive.class.isAssignableFrom(clazz)) {
                JFinalDirective directive = clazz.getAnnotation(JFinalDirective.class);
                if (directive != null) {
                    String name = AnnotationUtil.get(directive.value());
                    if (directive.override()) {
                        //remove old directive
                        engine.removeDirective(name);
                    }
                    engine.addDirective(name, (Class<? extends Directive>) clazz);
                }
            } else if (clazz.isEnum()) {
                JFinalSharedEnum sharedEnum = clazz.getAnnotation(JFinalSharedEnum.class);
                if (sharedEnum != null) {
                    String name = AnnotationUtil.get(sharedEnum.value(), clazz.getSimpleName());
                    if (sharedEnum.override()) {
                        engine.removeSharedObject(name);
                    }
                    engine.addSharedObject(name, SharedEnumObject.create((Class<? extends Enum<?>>) clazz));
                }
            }

            JFinalSharedMethod sharedMethod = clazz.getAnnotation(JFinalSharedMethod.class);
            if (sharedMethod != null) {
                engine.addSharedMethod(ClassUtil.newInstance(clazz));
            }

            JFinalSharedStaticMethod sharedStaticMethod = clazz.getAnnotation(JFinalSharedStaticMethod.class);
            if (sharedStaticMethod != null) {
                engine.addSharedStaticMethod(clazz);
            }

            JFinalSharedObject sharedObject = clazz.getAnnotation(JFinalSharedObject.class);
            if (sharedObject != null) {
                engine.addSharedObject(AnnotationUtil.get(sharedObject.value()), ClassUtil.newInstance(clazz));
            }


        }

        JbootAppListenerManager.me().onEngineConfig(engine);
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

        //一般的项目没必要添加门户网关的 Gateway
        //在某些情况下，必须要添加的，可以自行添加
        if (JbootGatewayManager.me().isConfigOk()) {
            handlers.add(new JbootGatewayHandler());
        }

        handlers.add(new AttachmentHandler());

        SentinelConfig sentinelConfig = SentinelConfig.get();
        if (sentinelConfig.isEnable() && sentinelConfig.isReqeustEnable()) {
            handlers.add(new SentinelHandler());
        }

        //metrics 处理
        JbootMetricConfig metricsConfig = Jboot.config(JbootMetricConfig.class);
        if (metricsConfig.isEnable() && metricsConfig.isConfigOk()) {

            if (StrUtil.isNotBlank(metricsConfig.getAdminServletMapping())) {
                handlers.add(new MetricServletHandler(metricsConfig.getAdminServletMapping()));
            }

            if (metricsConfig.isRequestMetricEnable()) {
                handlers.add(new JbootRequestMetricHandler());
            }
        }

        if (JbootWebConfig.getInstance().isEscapeParasEnable()) {
            handlers.add(new XSSHandler());
        }

        handlers.add(new JbootHandler());

        //若用户自己没配置 ActionHandler，默认使用 JbootActionHandler
        if (handlers.getActionHandler() == null) {
            if (JbootWebConfig.getInstance().isPathVariableEnable()) {
                handlers.setActionHandler(new PathVariableActionHandler());
            } else {
                handlers.setActionHandler(new JbootActionHandler());
            }
        }

    }

    @Override
    public void onStart() {

        JbootAppListenerManager.me().onStartBefore();

        // 初始化 Jboot 内置组件
        JbootrpcManager.me().init();
        JbootShiroManager.me().init(routeList);
        JbootScheduleManager.me().init();
        JbootSwaggerManager.me().init();
        LimiterManager.me().init();
        JbootSeataManager.me().init();
        JbootSentinelManager.me().init();

        if (ClassUtil.hasClass("com.jfinal.weixin.sdk.api.ApiConfigKit")) {
            new WechatSupport().autoSupport();
        }

        JbootAppListenerManager.me().onStart();

        //自定义参数转换方法
        TypeConverter.me().setConvertFunc(new TypeConverterFunc());
        ArrayConverters.init();

        //一般情况下，各个模块会在 onStart 进行添加监听器
        //此时可以主动去启动下 mq
        JbootmqManager.me().init();

        //使用场景：需要等所有组件 onStart() 完成之后，再去执行某些工作的时候
        JbootAppListenerManager.me().onStartFinish();


    }

    @Override
    public void onStop() {
        JbootAppListenerManager.me().onStop();

        JbootScheduleManager.me().stop();
        JbootSeataManager.me().stop();
        JbootrpcManager.me().stop();

        JbootmqManager.me().stop();
    }


}
