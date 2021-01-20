/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.web.controller.JbootController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于对Jwt的设置
 */
public class JwtInterceptor implements Interceptor {

    public static final String ISUUED_AT = "isuuedAt";

    @Override
    public void intercept(Invocation inv) {
        try {
            inv.invoke();
        } finally {

            JbootController jbootController = (JbootController) inv.getController();
            Map<String, Object> jwtAttrs = jbootController.getJwtAttrs();

            if (jwtAttrs == null) {
                refreshIfNecessary(jbootController, jbootController.getJwtParas());
            } else {
                responseJwt(jbootController, jwtAttrs);
            }
        }
    }


    /**
     * 对 jwt 内容进行刷新
     * @param controller
     * @param oldData
     */
    private void refreshIfNecessary(Controller controller, Map oldData) {
        if (oldData == null || oldData.isEmpty()) {
            return;
        }

        // Jwt token 的发布时间
        Long isuuedAtMillis = (Long) oldData.get(ISUUED_AT);

        // 有效期
        long validityPeriod = JwtManager.me().getConfig().getValidityPeriod();

        // 永久有效，没必要刷新 Jwt
        if (isuuedAtMillis == null || validityPeriod <= 0) {
            return;
        }

        // 已经发布的时间
        long savedMillis = System.currentTimeMillis() - isuuedAtMillis;

        // 已经发布的时间 大于有效期的一半，重新刷新
        if (savedMillis > validityPeriod / 2) {
            responseJwt(controller, oldData);
        }
    }


    /**
     * 输出 jwt 内容到客户端
     *
     * @param controller
     * @param map
     */
    private void responseJwt(Controller controller, Map map) {
        String token = JwtManager.me().createJwtToken(map);
        HttpServletResponse response = controller.getResponse();
        response.addHeader(JwtManager.me().getHttpHeaderName(), token);
    }
}
