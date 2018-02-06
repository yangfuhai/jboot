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
import com.jfinal.core.Controller;
import io.jboot.utils.RequestUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

import java.util.concurrent.Semaphore;

/**
 * 限流拦截器
 */
public class LimitationInterceptor implements FixedInterceptor {


    @Override
    public void intercept(FixedInvocation inv) {

        LimitationManager manager = LimitationManager.me();

        LimitationInfo info = manager.getLimitationInfo(inv.getActionKey());
        if (info == null) {
            inv.invoke();
            return;
        }

        if (!doIntercept(inv,info)) {
            try {
                renderLimitation(inv.getController(), info);
            }finally {
                if (info.getType() == LimitationInfo.TYPE_CONCURRENCY){
                    manager.getSemaphore(inv.getActionKey()).release();
                }
            }
            return;
        }

        inv.invoke();
    }


    private boolean doIntercept(FixedInvocation inv,LimitationInfo limitationInfo) {

        switch (limitationInfo.getType()) {
            case LimitationInfo.TYPE_CONCURRENCY:
                return concurrencyIntercept(inv,limitationInfo);
            case LimitationInfo.TYPE_REQUEST:
                return requestIntercept(inv,limitationInfo);
            case LimitationInfo.TYPE_IP:
                return ipIntercept(inv,limitationInfo);
            case LimitationInfo.TYPE_USER:
               return userIntercept(inv,limitationInfo);
        }

        return true;
    }


    private boolean concurrencyIntercept(FixedInvocation inv, LimitationInfo info) {
        LimitationManager manager = LimitationManager.me();
        Semaphore semaphore = manager.getSemaphore(inv.getActionKey());
        if (semaphore == null) {
            semaphore = manager.initSemaphore(inv.getActionKey(), info.getRate());
        }
        return semaphore.tryAcquire();
    }



    private boolean requestIntercept(FixedInvocation inv, LimitationInfo info) {
        LimitationManager manager = LimitationManager.me();
        RateLimiter limiter = manager.getLimiter(inv.getActionKey());
        if (limiter == null) {
            limiter = manager.initRateLimiter(inv.getActionKey(), info.getRate());
        }
        return limiter.tryAcquire();
    }

    private boolean ipIntercept(FixedInvocation inv, LimitationInfo info) {
        LimitationManager manager = LimitationManager.me();
        String ipaddress = RequestUtils.getIpAddress(inv.getController().getRequest());
        long currentTime = System.currentTimeMillis();
        long userFlagTime = manager.getIpflag(ipaddress);
        manager.flagIpRequest(ipaddress);

        //第一次访问，可能manager里还未对此IP进行标识
        if (userFlagTime >= currentTime) {
            return true;
        }

        double rate = info.getRate();
        if (rate <= 0 || rate >= 1000) {
            throw new IllegalArgumentException("@EnableIpRateLimit.rate must > 0 and < 1000");
        }

        double interval = 1000 / rate;
        if ((currentTime - userFlagTime) >= interval) {
            return true;
        }

        return false;
    }

    private boolean userIntercept(FixedInvocation inv, LimitationInfo info) {
        LimitationManager manager = LimitationManager.me();
        String sesssionId = inv.getController().getSession(true).getId();

        long currentTime = System.currentTimeMillis();
        long userFlagTime = manager.getUserflag(sesssionId);

        manager.flagUserRequest(sesssionId);

        //第一次访问，可能manager里还未对此用户进行标识
        if (userFlagTime >= currentTime) {
            return true;
        }

        double rate = info.getRate();
        if (rate <= 0 || rate >= 1000) {
            throw new IllegalArgumentException("@EnableIpRateLimit.rate must > 0 and < 1000");
        }

        double interval = 1000 / rate;
        if ((currentTime - userFlagTime) >= interval) {
            return true;
        }

        return false;
    }


    private void renderLimitation(Controller controller, LimitationInfo limitationInfo) {

        LimitationManager manager = LimitationManager.me();

        /**
         * 注解上没有设置 Action , 使用jboot.properties配置文件的
         */
        if (StringUtils.isBlank(limitationInfo.getRenderType())) {
            //ajax 请求
            if (RequestUtils.isAjaxRequest(controller.getRequest())) {
                controller.renderJson(manager.getAjaxJsonMap());
            }
            //非ajax的正常请求
            else {
                String limitView = manager.getLimitView();
                if (limitView != null) {
                    controller.render(limitView);
                } else {
                    controller.renderText("reqeust limit.");
                }
            }
        }

        /**
         * 设置了 Action , 用用户自己配置的
         */
        else {
            switch (limitationInfo.getRenderType()) {
                case LimitRenderType.JSON:
                    controller.renderJson(limitationInfo.getRenderContent());
                    break;
                case LimitRenderType.TEXT:
                    controller.renderText(limitationInfo.getRenderContent());
                    break;
                case LimitRenderType.RENDER:
                    controller.render(limitationInfo.getRenderContent());
                    break;
                case LimitRenderType.REDIRECT:
                    controller.redirect(limitationInfo.getRenderContent(), true);
                    break;
            }

        }
    }


}
