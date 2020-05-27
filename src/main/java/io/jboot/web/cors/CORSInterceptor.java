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
package io.jboot.web.cors;

import com.jfinal.aop.Invocation;
import com.jfinal.ext.cors.EnableCORS;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.fixedinterceptor.FixedInterceptor;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: CORS 处理相关 拦截器
 * @Package io.jboot.web.cors
 */
public class CORSInterceptor implements FixedInterceptor {

    private static final String METHOD_OPTIONS = "OPTIONS";

    @Override
    public void intercept(Invocation inv) {

        EnableCORS enableCORS = getAnnotation(inv);

        if (enableCORS == null) {
            inv.invoke();
            return;
        }


        doConfigCORS(inv, enableCORS);


        String method = inv.getController().getRequest().getMethod();
        if (METHOD_OPTIONS.equals(method)) {
            inv.getController().renderText("");
        } else {
            inv.invoke();
        }
    }


    private EnableCORS getAnnotation(Invocation inv) {
        EnableCORS enableCORS = inv.getController().getClass().getAnnotation(EnableCORS.class);
        return enableCORS != null ? enableCORS : inv.getMethod().getAnnotation(EnableCORS.class);
    }

    private void doConfigCORS(Invocation inv, EnableCORS enableCORS) {

        HttpServletResponse response = inv.getController().getResponse();

        String allowOrigin = AnnotationUtil.get(enableCORS.allowOrigin());
        String allowCredentials = AnnotationUtil.get(enableCORS.allowCredentials());
        String allowHeaders = AnnotationUtil.get(enableCORS.allowHeaders());
        String allowMethods = AnnotationUtil.get(enableCORS.allowMethods());
        String exposeHeaders = AnnotationUtil.get(enableCORS.exposeHeaders());
        String requestHeaders = AnnotationUtil.get(enableCORS.requestHeaders());
        String requestMethod = AnnotationUtil.get(enableCORS.requestMethod());
        String origin = AnnotationUtil.get(enableCORS.origin());
        String maxAge = AnnotationUtil.get(enableCORS.maxAge());

        response.setHeader("Access-Control-Allow-Origin", allowOrigin);
        response.setHeader("Access-Control-Allow-Methods", allowMethods);
        response.setHeader("Access-Control-Allow-Headers", allowHeaders);
        response.setHeader("Access-Control-Max-Age", maxAge);
        response.setHeader("Access-Control-Allow-Credentials", allowCredentials);

        if (StrUtil.isNotBlank(exposeHeaders)) {
            response.setHeader("Access-Control-Expose-Headers", exposeHeaders);
        }

        if (StrUtil.isNotBlank(requestHeaders)) {
            response.setHeader("Access-Control-Request-Headers", requestHeaders);
        }

        if (StrUtil.isNotBlank(requestMethod)) {
            response.setHeader("Access-Control-Request-Method", requestMethod);
        }

        if (StrUtil.isNotBlank(origin)) {
            response.setHeader("Origin", origin);
        }

    }
}
