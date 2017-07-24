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
package io.jboot.web;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.List;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.json.JsonManager;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Directive;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import io.jboot.Jboot;
import io.jboot.component.log.Slf4jLogFactory;
import io.jboot.component.metrics.JbootMetricsManager;
import io.jboot.component.shiro.JbootShiroInterceptor;
import io.jboot.core.cache.JbootCacheConfig;
import io.jboot.core.rpc.JbootrpcManager;
import io.jboot.db.JbootDbManager;
import io.jboot.schedule.JbootTaskManager;
import io.jboot.server.listener.JbootAppListenerManager;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.interceptor.GuiceInterceptor;
import io.jboot.web.controller.interceptor.ParaValidateInterceptor;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.annotation.JFinalSharedMethod;
import io.jboot.web.directive.annotation.JFinalSharedObject;
import io.jboot.web.directive.annotation.JFinalSharedStaticMethod;
import io.jboot.web.handler.JbootHandler;
import io.jboot.web.render.JbootRenderFactory;
import io.jboot.wechat.JbootAccessTokenCache;
import io.jboot.wechat.JbootWechatConfig;


public class JbootAppConfig extends JFinalConfig {
	
	// 提供配置的路由对象供其他地方调用
	public static Routes routes;

    static final Log log = Log.getLog(JbootAppConfig.class);


    @Override
    public void configConstant(Constants constants) {

        PropKit.use("jboot.properties");
        constants.setRenderFactory(new JbootRenderFactory());
        constants.setDevMode(Jboot.me().isDevMode());
        ApiConfigKit.setDevMode(Jboot.me().isDevMode());

        JbootWechatConfig config = Jboot.config(JbootWechatConfig.class);
        ApiConfig apiConfig = config.getApiConfig();
        if (apiConfig != null) {
            ApiConfigKit.putApiConfig(apiConfig);
        }

        constants.setLogFactory(Slf4jLogFactory.me());
        constants.setMaxPostSize(1024 * 1024 * 2000);
        constants.setReportAfterInvocation(false);

        JbootAppListenerManager.me().onJfinalConstantConfig(constants);
    }


    @Override
    public void configRoute(Routes routes) {

        List<Class<Controller>> controllerClassList = ClassScanner.scanSubClass(Controller.class);
        if (controllerClassList == null) {
            return;
        }

        for (Class<Controller> clazz : controllerClassList) {
            RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
            if (mapping == null || mapping.value() == null) {
                continue;
            }

            if (StrKit.notBlank(mapping.viewPath())) {
                routes.add(mapping.value(), clazz, mapping.viewPath());
            } else {
                routes.add(mapping.value(), clazz);
            }

        }
        JbootAppListenerManager.me().onJfinalRouteConfig(routes);
        JbootAppConfig.routes = routes;
    }

    @Override
    public void configEngine(Engine engine) {

        /**
         * now 并没有被添加到默认的指令当中
         * 先添加，后移除，防止在热加载的时候重复添加而产生异常。
         * 查看：EngineConfig
         */
        engine.addDirective("now", new com.jfinal.template.ext.directive.NowDirective());

        List<Class> directiveClasses = ClassScanner.scanClass();
        for (Class clazz : directiveClasses) {
            JFinalDirective jDirective = (JFinalDirective) clazz.getAnnotation(JFinalDirective.class);
            if (jDirective != null) {
                Directive directive = ClassNewer.newInstance((Class<Directive>) clazz);
                if (directive != null) {
                    engine.removeDirective(jDirective.value());
                    engine.addDirective(jDirective.value(), directive);
                }
            }

            JFinalSharedMethod sharedMethod = (JFinalSharedMethod) clazz.getAnnotation(JFinalSharedMethod.class);
            if (sharedMethod != null) {
                engine.addSharedMethod(ClassNewer.newInstance(clazz));
            }

            JFinalSharedStaticMethod sharedStaticMethod = (JFinalSharedStaticMethod) clazz.getAnnotation(JFinalSharedStaticMethod.class);
            if (sharedStaticMethod != null) {
                engine.addSharedStaticMethod(clazz);
            }

            JFinalSharedObject sharedObject = (JFinalSharedObject) clazz.getAnnotation(JFinalSharedObject.class);
            if (sharedObject != null) {
                engine.addSharedObject(sharedObject.value(), ClassNewer.newInstance(clazz));
            }
        }

        JbootAppListenerManager.me().onJfinalEngineConfig(engine);
    }


    @Override
    public void configPlugin(Plugins plugins) {

        List<ActiveRecordPlugin> arps = JbootDbManager.me().getActiveRecordPlugins();
        for (ActiveRecordPlugin arp : arps) {
            plugins.add(arp);
        }

        if (JbootTaskManager.me().isCron4jEnable()) {
            plugins.add(JbootTaskManager.me().getCron4jPlugin());
        }

        JbootCacheConfig cacheConfig = Jboot.config(JbootCacheConfig.class);
        if (JbootCacheConfig.TYPE_EHCACHE.equals(cacheConfig.getType())
                || JbootCacheConfig.TYPE_EHREDIS.equals(cacheConfig.getType())) {

            plugins.add(new EhCachePlugin());
        }

        JbootAppListenerManager.me().onJfinalPluginConfig(plugins);

    }


    @Override
    public void configInterceptor(Interceptors interceptors) {


        interceptors.add(new GuiceInterceptor());
        interceptors.add(new JbootShiroInterceptor());
        interceptors.add(new ParaValidateInterceptor());

        JbootAppListenerManager.me().onInterceptorConfig(interceptors);
    }

    @Override
    public void configHandler(Handlers handlers) {
        handlers.add(new JbootHandler());

        JbootAppListenerManager.me().onHandlerConfig(handlers);
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        ApiConfigKit.setAccessTokenCache(new JbootAccessTokenCache());
        JsonManager.me().setDefaultDatePattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 初始化
         */
        JbootMetricsManager.me().init();
        JbootrpcManager.me().init();

        /**
         * 发送启动完成通知
         */
        Jboot.me().sendEvent(Jboot.EVENT_STARTED, null);

        JbootAppListenerManager.me().onJbootStarted();

    }

    @Override
    public void beforeJFinalStop() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        if (drivers != null) {
            while (drivers.hasMoreElements()) {
                try {
                    Driver driver = drivers.nextElement();
                    DriverManager.deregisterDriver(driver);
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
            }
        }

        JbootAppListenerManager.me().onJFinalStop();
    }


}
