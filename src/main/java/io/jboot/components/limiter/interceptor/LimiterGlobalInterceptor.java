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
package io.jboot.components.limiter.interceptor;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.components.limiter.LimitType;
import io.jboot.components.limiter.LimiterManager;
import io.jboot.utils.RequestUtil;

public class LimiterGlobalInterceptor extends BaseLimiterInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {
        LimiterManager manager = LimiterManager.me();
        if (inv.isActionInvocation() && manager.isInIpWhitelist(RequestUtil.getIpAddress(inv.getController().getRequest()))) {
            inv.invoke();
        } else {
            String packageOrTarget = getPackageOrTarget(inv);
            LimiterManager.LimitConfigBean configBean = manager.matchConfig(packageOrTarget);
            if (configBean != null) {
                doInterceptByTypeAndRate(configBean, packageOrTarget, inv);
            } else {
                inv.invoke();
            }
        }
    }


    private void doInterceptByTypeAndRate(LimiterManager.LimitConfigBean limitConfigBean, String resource, Invocation inv) {
        switch (limitConfigBean.getType()) {
            case LimitType.CONCURRENCY:
                doInterceptForConcurrency(limitConfigBean.getRate(), resource, null, inv);
                break;
            case LimitType.IP_CONCURRENCY:
                String resKey1 = RequestUtil.getIpAddress(inv.getController().getRequest()) + ":" + resource;
                doInterceptForConcurrency(limitConfigBean.getRate(), resKey1, null, inv);
                break;
            case LimitType.TOKEN_BUCKET:
                doInterceptForTokenBucket(limitConfigBean.getRate(), resource, null, inv);
                break;
            case LimitType.IP_TOKEN_BUCKET:
                String resKey2 = RequestUtil.getIpAddress(inv.getController().getRequest()) + ":" + resource;
                doInterceptForTokenBucket(limitConfigBean.getRate(), resKey2, null, inv);
                break;
        }
    }


}
