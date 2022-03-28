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
package io.jboot.web.cors;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.ext.cors.EnableCORS;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: CORS 处理相关 拦截器
 */
@AutoLoad
public class CORSInterceptor implements Interceptor, InterceptorBuilder {

    private static final String METHOD_OPTIONS = "OPTIONS";


    @Override
    public void intercept(Invocation inv) {

        EnableCORS enableCORS = getAnnotation(inv);

        if (enableCORS == null) {
            inv.invoke();
            return;
        }

        process(inv, CORSConfig.fromAnnotation(enableCORS));
    }


    private EnableCORS getAnnotation(Invocation inv) {
        EnableCORS enableCORS = inv.getController().getClass().getAnnotation(EnableCORS.class);
        return enableCORS != null ? enableCORS : inv.getMethod().getAnnotation(EnableCORS.class);
    }


    public static void process(Invocation inv, CORSConfig config) {
        //配置 http 头信息
        configHeaders(inv.getController().getResponse(), config);

        String method = inv.getController().getRequest().getMethod();
        if (METHOD_OPTIONS.equalsIgnoreCase(method)) {
            inv.getController().renderText("");
        } else {
            inv.invoke();
        }
    }


    private static void configHeaders(HttpServletResponse response, CORSConfig config) {
        response.setHeader("Access-Control-Allow-Origin", config.getAllowOrigin());
        response.setHeader("Access-Control-Allow-Methods", config.getAllowMethods());
        response.setHeader("Access-Control-Allow-Headers", config.getAllowHeaders());
        response.setHeader("Access-Control-Max-Age", config.getMaxAge());
        response.setHeader("Access-Control-Allow-Credentials", config.getAllowCredentials());

        if (StrUtil.isNotBlank(config.getExposeHeaders())) {
            response.setHeader("Access-Control-Expose-Headers", config.getExposeHeaders());
        }

        if (StrUtil.isNotBlank(config.getRequestHeaders())) {
            response.setHeader("Access-Control-Request-Headers", config.getRequestHeaders());
        }

        if (StrUtil.isNotBlank(config.getRequestMethod())) {
            response.setHeader("Access-Control-Request-Method", config.getRequestMethod());
        }

        if (StrUtil.isNotBlank(config.getOrigin())) {
            response.setHeader("Origin", config.getOrigin());
        }
    }


    @Override
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {
        if (Util.isController(targetClass) && Util.hasAnnotation(targetClass, method, EnableCORS.class)) {
            interceptors.addToFirstIfNotExist(CORSInterceptor.class);
        }
    }

}
