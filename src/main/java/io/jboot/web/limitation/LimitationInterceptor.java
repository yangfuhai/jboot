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
        if (userRateLimit != null && userIntercept(inv, userRateLimit)) {
            return;
        }

        EnableIpRateLimit ipRateLimit = inv.getMethod().getAnnotation(EnableIpRateLimit.class);
        if (ipRateLimit != null && ipIntercept(inv, ipRateLimit)) {
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
                case LimitActions.JSON:
                    inv.getController().renderJson(requestRateLimit.limitContent());
                    break;
                case LimitActions.TEXT:
                    inv.getController().renderText(requestRateLimit.limitContent());
                    break;
                case LimitActions.RENDER:
                    inv.getController().render(requestRateLimit.limitContent());
                    break;
                case LimitActions.REDIRECT:
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
        LimitationManager manager = LimitationManager.me();
        String sesssionId = inv.getController().getSession(true).getId();

        long currentTime = System.currentTimeMillis();
        long userFlagTime = manager.getUserflag(sesssionId);

        manager.flagUserRequest(sesssionId);

        //第一次访问，可能manager里还未对此用户进行标识
        if (userFlagTime >= currentTime) {
            return false;
        }

        double rate = userRateLimit.rate();
        if (rate <= 0 || rate >= 1000) {
            throw new IllegalArgumentException("@EnableIpRateLimit.rate must > 0 and < 1000");
        }

        double interval = 1000 / rate;
        if ((currentTime - userFlagTime) >= interval) {
            return false;
        }

        /**
         * 注解上没有设置 Action , 使用jboot.properties配置文件的
         */
        if (StringUtils.isBlank(userRateLimit.limitAction())) {
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
                    inv.getController().renderText("reqeust limit by @EnableUserRateLimit.");
                }
            }
        }

        /**
         * 设置了 Action , 用用户自己配置的
         */
        else {
            switch (userRateLimit.limitAction()) {
                case LimitActions.JSON:
                    inv.getController().renderJson(userRateLimit.limitContent());
                    break;
                case LimitActions.TEXT:
                    inv.getController().renderText(userRateLimit.limitContent());
                    break;
                case LimitActions.RENDER:
                    inv.getController().render(userRateLimit.limitContent());
                    break;
                case LimitActions.REDIRECT:
                    inv.getController().redirect(userRateLimit.limitContent(), true);
                    break;
                default:
                    throw new IllegalArgumentException("annotation @EnableUserRateLimit.limitAction error in "
                            + inv.getController().getClass().getName() + "." + inv.getMethodName()
                            + ",  limitAction support text,json,render,redirect only, not support " + userRateLimit.limitAction());
            }

        }


        return true;
    }


    /**
     * ip请求频率拦截
     *
     * @param inv
     * @param ipRateLimit
     * @return
     */
    private boolean ipIntercept(HandlerInvocation inv, EnableIpRateLimit ipRateLimit) {
        LimitationManager manager = LimitationManager.me();
        String ipaddress = RequestUtils.getIpAddress(inv.getController().getRequest());
        long currentTime = System.currentTimeMillis();
        long userFlagTime = manager.getIpflag(ipaddress);
        manager.flagIpRequest(ipaddress);

        //第一次访问，可能manager里还未对此IP进行标识
        if (userFlagTime >= currentTime) {
            return false;
        }

        double rate = ipRateLimit.rate();
        if (rate <= 0 || rate >= 1000) {
            throw new IllegalArgumentException("@EnableIpRateLimit.rate must > 0 and < 1000");
        }

        double interval = 1000 / rate;
        if ((currentTime - userFlagTime) >= interval) {
            return false;
        }


        /**
         * 注解上没有设置 Action , 使用jboot.properties配置文件的
         */
        if (StringUtils.isBlank(ipRateLimit.limitAction())) {
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
                    inv.getController().renderText("reqeust limit by @EnableIpRateLimit.");
                }
            }
        }

        /**
         * 设置了 Action , 用用户自己配置的
         */
        else {
            switch (ipRateLimit.limitAction()) {
                case LimitActions.JSON:
                    inv.getController().renderJson(ipRateLimit.limitContent());
                    break;
                case LimitActions.TEXT:
                    inv.getController().renderText(ipRateLimit.limitContent());
                    break;
                case LimitActions.RENDER:
                    inv.getController().render(ipRateLimit.limitContent());
                    break;
                case LimitActions.REDIRECT:
                    inv.getController().redirect(ipRateLimit.limitContent(), true);
                    break;
                default:
                    throw new IllegalArgumentException("annotation @EnableIpRateLimit.limitAction error in "
                            + inv.getController().getClass().getName() + "." + inv.getMethodName()
                            + ",  limitAction support text,json,render,redirect only, not support " + ipRateLimit.limitAction());
            }

        }


        return true;
    }


}
