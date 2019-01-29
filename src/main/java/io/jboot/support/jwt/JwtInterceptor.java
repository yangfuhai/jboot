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
package io.jboot.support.jwt;

import io.jboot.Jboot;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootController;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

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

    private static JwtConfig jwtConfig = Jboot.config(JwtConfig.class);

    @Override
    public void intercept(FixedInvocation inv) {
        if (!jwtConfig.isEnable()) {
            inv.invoke();
            return;
        }

        HttpServletRequest request = inv.getController().getRequest();
        String token = request.getHeader(JwtManager.me().getHttpHeaderName());

        if (StrUtil.isBlank(token)) {
            processInvoke(inv, null);
            return;
        }

        Map map = JwtManager.me().parseJwtToken(token);
        if (map == null) {
            processInvoke(inv, null);
            return;
        }

        try {
            JwtManager.me().holdJwts(map);
            processInvoke(inv, map);
        } finally {
            JwtManager.me().releaseJwts();
        }
    }



    private void processInvoke(FixedInvocation inv, Map oldData) {

        inv.invoke();


        if (!(inv.getController() instanceof JbootController)) {
            return;
        }

        JbootController jbootController = (JbootController) inv.getController();
        Map<String, Object> jwtMap = jbootController.getJwtAttrs();


        if (jwtMap == null || jwtMap.isEmpty()) {
            refreshIfNecessary(inv, oldData);
            return;
        }

        String token = JwtManager.me().createJwtToken(jwtMap);
        HttpServletResponse response = inv.getController().getResponse();
        response.addHeader(JwtManager.me().getHttpHeaderName(), token);
    }

    
    private void refreshIfNecessary(FixedInvocation inv, Map oldData) {
        if (oldData == null) {
            return;
        }

        Long isuuedAtMillis = (Long) oldData.get("isuuedAt");
        if (isuuedAtMillis == null || jwtConfig.getValidityPeriod() <= 0) {
            return;
        }

        Long nowMillis = System.currentTimeMillis();
        long savedMillis = nowMillis - isuuedAtMillis;

        if (savedMillis > jwtConfig.getValidityPeriod() / 2) {
            String token = JwtManager.me().createJwtToken(oldData);
            HttpServletResponse response = inv.getController().getResponse();
            response.addHeader(JwtManager.me().getHttpHeaderName(), token);
        }

    }
}
