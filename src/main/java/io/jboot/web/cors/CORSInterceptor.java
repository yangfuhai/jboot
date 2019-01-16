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
package io.jboot.web.cors;

import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: CORS 处理相关 拦截器
 * @Package io.jboot.web.cors
 */
public class CORSInterceptor implements FixedInterceptor {

    private static final String ALLOW_ORIGIN = "*";
    private static final String ALLOW_METHODS = "GET, POST, PUT, DELETE, OPTIONS";
    private static final int MAX_AGE = 3600;

    private static final String METHOD_OPTIONS = "OPTIONS";

    @Override
    public void intercept(FixedInvocation inv) {

        EnableCORS enableCORS = inv.getMethod().getAnnotation(EnableCORS.class);

        if (enableCORS == null) {
            enableCORS = inv.getController().getClass().getAnnotation(EnableCORS.class);
        }

        if (enableCORS != null) {
            String method = inv.getController().getRequest().getMethod();
            if (METHOD_OPTIONS.equals(method)) {
                inv.getController().renderText("");
                return;
            }
            doProcessCORS(inv, enableCORS);
        }

        inv.invoke();
    }

    private void doProcessCORS(FixedInvocation inv, EnableCORS enableCORS) {

        HttpServletResponse response = inv.getController().getResponse();

        String allowOrigin = AnnotationUtil.get(enableCORS.allowOrigin());
        String allowCredentials = AnnotationUtil.get(enableCORS.allowCredentials());
        String allowHeaders = AnnotationUtil.get(enableCORS.allowHeaders());
        String allowMethods = AnnotationUtil.get(enableCORS.allowMethods());
        String exposeHeaders = AnnotationUtil.get(enableCORS.exposeHeaders());
        String requestHeaders = AnnotationUtil.get(enableCORS.requestHeaders());
        String requestMethod = AnnotationUtil.get(enableCORS.requestMethod());
        String origin = AnnotationUtil.get(enableCORS.origin());

        int maxAge = enableCORS.maxAge();

        allowOrigin = StrUtil.isNotBlank(allowOrigin) ? allowOrigin : ALLOW_ORIGIN;
        allowMethods = StrUtil.isNotBlank(allowMethods) ? allowMethods : ALLOW_METHODS;
        maxAge = maxAge > 0 ? maxAge : MAX_AGE;

        response.setHeader("Access-Control-Allow-Origin", allowOrigin);
        response.setHeader("Access-Control-Allow-Methods", allowMethods);
        response.setHeader("Access-Control-Max-Age", String.valueOf(maxAge));

        if (StrUtil.isNotBlank(allowHeaders)) {
            response.setHeader("Access-Control-Allow-Headers", allowHeaders);
        }

        if (StrUtil.isNotBlank(allowCredentials)) {
            response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
        }

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
