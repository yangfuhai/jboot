/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.kits.StringKits;
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

    @Override
    public void intercept(FixedInvocation inv) {
        EnableCORS enableCORS = inv.getMethod().getAnnotation(EnableCORS.class);
        if (enableCORS == null) {
            enableCORS = inv.getController().getClass().getAnnotation(EnableCORS.class);
        }
        if (enableCORS != null) {
            doProcessCORS(inv, enableCORS);
        }

        inv.invoke();
    }

    private void doProcessCORS(FixedInvocation inv, EnableCORS enableCORS) {
        HttpServletResponse response = inv.getController().getResponse();

        String allowOrigin = enableCORS.allowOrigin();
        String allowCredentials = enableCORS.allowCredentials();
        String allowHeaders = enableCORS.allowHeaders();
        String allowMethods = enableCORS.allowMethods();
        String exposeHeaders = enableCORS.exposeHeaders();
        String requestHeaders = enableCORS.requestHeaders();
        String requestMethod = enableCORS.requestMethod();
        String origin = enableCORS.origin();
        int maxAge = enableCORS.maxAge();

        allowOrigin = StringKits.isNotBlank(allowOrigin) ? allowOrigin : ALLOW_ORIGIN;
        allowMethods = StringKits.isNotBlank(allowMethods) ? allowMethods : ALLOW_METHODS;
        maxAge = maxAge > 0 ? maxAge : MAX_AGE;

        response.setHeader("Access-Control-Allow-Origin", allowOrigin);
        response.setHeader("Access-Control-Allow-Methods", allowMethods);
        response.setHeader("Access-Control-Max-Age", String.valueOf(maxAge));

        if (StringKits.isNotBlank(allowHeaders)) {
            response.setHeader("Access-Control-Allow-Headers", allowHeaders);
        }

        if (StringKits.isNotBlank(allowCredentials)) {
            response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
        }

        if (StringKits.isNotBlank(exposeHeaders)) {
            response.setHeader("Access-Control-Expose-Headers", exposeHeaders);
        }

        if (StringKits.isNotBlank(requestHeaders)) {
            response.setHeader("Access-Control-Request-Headers", requestHeaders);
        }

        if (StringKits.isNotBlank(requestMethod)) {
            response.setHeader("Access-Control-Request-Method", requestMethod);
        }

        if (StringKits.isNotBlank(origin)) {
            response.setHeader("Origin", origin);
        }

    }
}
