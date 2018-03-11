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
package io.jboot.component.jwt;

import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于对Jwt的设置
 * @Package io.jboot.web.jwt
 */
public class JwtInterceptor implements FixedInterceptor {

    @Override
    public void intercept(FixedInvocation inv) {
        if (!JwtManager.me().isEnable()) {
            inv.invoke();
            return;
        }

        HttpServletRequest request = inv.getController().getRequest();
        String token = request.getHeader(JwtManager.me().getHttpHeaderName());

        if (StringUtils.isBlank(token)) {
            inv.invoke();
            processInvokeAfter(inv);
            return;
        }

        Map map = JwtManager.me().parseJwtToken(token);
        if (map == null) {
            inv.invoke();
            processInvokeAfter(inv);
            return;
        }

        JwtShiroBridge jwtShiroBridge = JwtManager.me().getJwtShiroBridge();
        if (jwtShiroBridge != null) {
            Subject subject = jwtShiroBridge.buildSubject(map, inv.getController());
            if (subject != null) {
                ThreadContext.bind(subject);
            }
        }

        try {
            JwtManager.me().holdJwts(map);
            inv.invoke();
            processInvokeAfter(inv);
        } finally {
            JwtManager.me().releaseJwts();
        }
    }


    private void processInvokeAfter(FixedInvocation inv) {
        if (!(inv.getController() instanceof JbootController)) {
            return;
        }

        JbootController jbootController = (JbootController) inv.getController();
        Map<String, Object> jwtMap = jbootController.getJwtAttrs();

        if (jwtMap == null || jwtMap.isEmpty()) {
            return;
        }

        String token = JwtManager.me().createJwtToken(jwtMap);
        HttpServletResponse response = inv.getController().getResponse();
        response.addHeader(JwtManager.me().getHttpHeaderName(), token);
    }
}
