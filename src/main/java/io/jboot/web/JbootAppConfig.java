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

import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.json.JsonManager;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Directive;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import io.jboot.Jboot;
import io.jboot.cache.JbootCacheConfig;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;
import io.jboot.db.JbootDbManager;
import io.jboot.schedule.JbootTaskManager;
import io.jboot.web.controller.annotation.UrlMapping;
import io.jboot.web.directive.annotation.JbootDirective;
import io.jboot.web.handler.JbootHandler;
import io.jboot.web.controller.interceptor.ParaValidateInterceptor;
import io.jboot.web.render.JbootRenderFactory;
import io.jboot.wechat.JbootAccessTokenCache;
import io.jboot.wechat.JbootWechatConfig;

import java.util.List;


public class JbootAppConfig extends JFinalConfig {

    @Override
    public void configConstant(Constants constants) {

        PropKit.use("jboot.properties");
        constants.setRenderFactory(new JbootRenderFactory());
        constants.setDevMode(Jboot.isDevMode());
        ApiConfigKit.setDevMode(Jboot.isDevMode());

        JbootWechatConfig config = Jboot.config(JbootWechatConfig.class);
        ApiConfig apiConfig = config.getApiConfig();
        if (apiConfig != null) {
            ApiConfigKit.putApiConfig(apiConfig);
        }

    }

    @Override
    public void configRoute(Routes routes) {
        List<Class<Controller>> controllerClassList = ClassScanner.scanSubClass(Controller.class);
        if (controllerClassList == null) {
            return;
        }

        for (Class<Controller> clazz : controllerClassList) {
            UrlMapping urlMapping = clazz.getAnnotation(UrlMapping.class);
            if (urlMapping == null || urlMapping.url() == null) {
                continue;
            }

            if (StrKit.notBlank(urlMapping.viewPath())) {
                routes.add(urlMapping.url(), clazz, urlMapping.viewPath());
            } else {
                routes.add(urlMapping.url(), clazz);
            }
        }
    }

    @Override
    public void configEngine(Engine engine) {
        engine.addDirective("dateFormat", new com.jfinal.template.ext.directive.DateDirective());
        List<Class<Directive>> directiveClasses = ClassScanner.scanSubClass(Directive.class);
        for (Class<Directive> clazz : directiveClasses) {
            JbootDirective jDirective = clazz.getAnnotation(JbootDirective.class);
            if (jDirective == null) continue;

            Directive directive = ClassNewer.newInstance(clazz);
            if (directive != null) {
                engine.addDirective(jDirective.value(), directive);
            }
        }
    }

    @Override
    public void configPlugin(Plugins plugins) {

        if (JbootDbManager.me().isConfigOk()) {
            plugins.add(JbootDbManager.me().getActiveRecordPlugin());
        }

        if (JbootDbManager.me().isMasterConfigOk()) {
            plugins.add(JbootDbManager.me().getMasterActiveRecordPlugin());
        }

        if (JbootTaskManager.me().isCron4jEnable()) {
            plugins.add(JbootTaskManager.me().getCron4jPlugin());
        }

        JbootCacheConfig cacheConfig = Jboot.config(JbootCacheConfig.class);
        if (JbootCacheConfig.TYPE_EHCACHE.equals(cacheConfig.getType())
                || JbootCacheConfig.TYPE_EHREDIS.equals(cacheConfig.getType())) {
            plugins.add(new EhCachePlugin());
        }

    }


    @Override
    public void configInterceptor(Interceptors interceptors) {
        interceptors.add(new ParaValidateInterceptor());
    }

    @Override
    public void configHandler(Handlers handlers) {
        handlers.add(new JbootHandler());
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        ApiConfigKit.setAccessTokenCache(new JbootAccessTokenCache());
        JsonManager.me().setDefaultDatePattern("yyyy-MM-dd HH:mm:ss");
    }
}
