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
package io.jboot.web.limitation;

import com.google.common.util.concurrent.RateLimiter;
import io.jboot.utils.RequestUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.HandlerInvocation;
import io.jboot.web.limitation.annotation.EnableIpRateLimit;
import io.jboot.web.limitation.annotation.EnableRequestRateLimit;
import io.jboot.web.limitation.annotation.EnableUserRateLimit;

/**
 * 限流拦截器
 */
public class LimitationInterceptor implements FixedInterceptor {


    @Override
    public void intercept(HandlerInvocation inv) {

        EnableRequestRateLimit requestRateLimit = inv.getMethod().getAnnotation(EnableRequestRateLimit.class);
        if (requestRateLimit != null && requestIntercept(inv, requestRateLimit)) {
            return;
        }

        EnableUserRateLimit userRateLimit = inv.getMethod().getAnnotation(EnableUserRateLimit.class);
        if (requestRateLimit != null && userIntercept(inv, userRateLimit)) {
            return;
        }

        EnableIpRateLimit ipRateLimit = inv.getMethod().getAnnotation(EnableIpRateLimit.class);
        if (requestRateLimit != null && ipIntercept(inv, userRateLimit)) {
            return;
        }

        inv.invoke();
    }


    /**
     * 请求频率拦截
     *
     * @param inv
     * @param requestRateLimit
     * @return
     */
    private boolean requestIntercept(HandlerInvocation inv, EnableRequestRateLimit requestRateLimit) {

        LimitationManager manager = LimitationManager.me();

        RateLimiter limiter = manager.getLimiter(inv.getActionKey());
        if (limiter == null) {
            limiter = manager.initRateLimiter(inv.getActionKey(), requestRateLimit.rate());
        }

        if (limiter.tryAcquire()) {
            return false;
        }

        /**
         * 注解上没有设置 Action , 使用jboot.properties配置文件的
         */
        if (StringUtils.isBlank(requestRateLimit.limitAction())) {
            //ajax 请求
            if (RequestUtils.isAjaxRequest(inv.getController().getRequest())) {
                inv.getController().renderJson(manager.getAjaxJsonMap());
            }
            //非ajax的正常请求
            else {
                String limitView = manager.getLimitView();
                if (limitView != null) {
                    inv.getController().render(limitView);
                } else {
                    inv.getController().renderText("reqeust limit.");
                }
            }
        }

        /**
         * 设置了 Action , 用用户自己配置的
         */
        else {
            switch (requestRateLimit.limitAction()) {
                case LimitationActions.JSON:
                    inv.getController().renderJson(requestRateLimit.limitContent());
                    break;
                case LimitationActions.TEXT:
                    inv.getController().renderText(requestRateLimit.limitContent());
                    break;
                case LimitationActions.RENDER:
                    inv.getController().render(requestRateLimit.limitContent());
                    break;
                case LimitationActions.REDIRECT:
                    inv.getController().redirect(requestRateLimit.limitContent(), true);
                    break;
                default:
                    throw new IllegalArgumentException("annotation @EnableRequestRateLimit.limitAction error in "
                            + inv.getController().getClass().getName() + "." + inv.getMethodName()
                            + ",  limitAction support text,json,render,redirect only, not support " + requestRateLimit.limitAction());
            }

        }


        return true;
    }


    /**
     * 用户操作频率拦截
     *
     * @param inv
     * @param userRateLimit
     * @return
     */
    private boolean userIntercept(HandlerInvocation inv, EnableUserRateLimit userRateLimit) {
        return false;
    }


    /**
     * ip请求频率拦截
     *
     * @param inv
     * @param userRateLimit
     * @return
     */
    private boolean ipIntercept(HandlerInvocation inv, EnableUserRateLimit userRateLimit) {
        return false;
    }


}
