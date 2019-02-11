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
package io.jboot.components.limiter;


import com.google.common.util.concurrent.RateLimiter;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.components.limiter.annotation.EnableLimit;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

import java.lang.reflect.Method;
import java.util.concurrent.Semaphore;

public class LimiterInterceptor implements FixedInterceptor, Interceptor {
    @Override
    public void intercept(FixedInvocation inv) {
        doProcess(inv);
    }

    @Override
    public void intercept(Invocation inv) {
        doProcess(inv);
    }

    private void doProcess(Invocation inv) {
        String packageOrTarget = getPackageOrTarget(inv);
        LimiterManager.TypeAndRate typeAndRate = LimiterManager.me().matchConfig(packageOrTarget);

        if (typeAndRate != null) {
            doInterceptByTypeAndRate(typeAndRate, packageOrTarget, inv);
            return;
        }

        EnableLimit enableLimit = inv.getMethod().getAnnotation(EnableLimit.class);
        if (enableLimit != null) {
            String resource = StrUtil.obtainDefaultIfBlank(enableLimit.resource(), packageOrTarget);
            doInterceptByLimitInfo(enableLimit, resource, inv);
            return;
        }

        inv.invoke();
    }


    private void doInterceptByTypeAndRate(LimiterManager.TypeAndRate typeAndRate, String resource, Invocation inv) {
        switch (typeAndRate.getType()) {
            case LimitType.CONCURRENCY:
                doInterceptForConcurrency(typeAndRate.getRate(), resource, null, inv);
                break;
            case LimitType.TOKEN_BUCKET:
                doInterceptForTokenBucket(typeAndRate.getRate(), resource, null, inv);
                break;
        }
    }

    private void doInterceptByLimitInfo(EnableLimit enableLimit, String resource, Invocation inv) {
        String type = AnnotationUtil.get(enableLimit.type());
        switch (type) {
            case LimitType.CONCURRENCY:
                doInterceptForConcurrency(enableLimit.rate(), resource, enableLimit.fallback(), inv);
                break;
            case LimitType.TOKEN_BUCKET:
                doInterceptForTokenBucket(enableLimit.rate(), resource, enableLimit.fallback(), inv);
                break;
        }
    }


    private void doInterceptForConcurrency(int rate, String resource, String fallback, Invocation inv) {
        Semaphore semaphore = LimiterManager.me().getOrCreateSemaphore(resource, rate);
        boolean acquire = false;
        try {
            acquire = semaphore.tryAcquire();
            if (acquire) {
                inv.invoke();
            }
            //不允许通行
            else {
                doExecFallback(resource, fallback, inv);
            }
        } finally {
            if (acquire) {
                semaphore.release();
            }
        }
    }


    private void doInterceptForTokenBucket(int rate, String resource, String fallback, Invocation inv) {
        RateLimiter limiter = LimiterManager.me().getOrCreateRateLimiter(resource, rate);
        //允许通行
        if (limiter.tryAcquire()) {
            inv.invoke();
        }
        //不允许通行
        else {
            doExecFallback(resource, fallback, inv);
        }
    }

    private void doExecFallback(String resource, String fallback, Invocation inv) {
        LimiterManager.me().processFallback(resource,fallback,inv);
    }


    private String getPackageOrTarget(Invocation inv) {
        return inv.isActionInvocation() ? inv.getActionKey() : buildMethodKey(inv.getMethod());
    }


    private String buildMethodKey(Method method) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(method.getDeclaringClass().getName());//packageAndClass
        keyBuilder.append(".");
        keyBuilder.append(method.getName());//methodName

        //method paras
        if (method.getParameterCount() > 0) {
            Class[] paraClasses = method.getParameterTypes();
            keyBuilder.append("(");
            for (Class c : paraClasses) {
                keyBuilder.append(c.getSimpleName()).append(",");
            }
            keyBuilder.deleteCharAt(keyBuilder.length() - 1);//del last char ,
            keyBuilder.append(")");
        } else {
            keyBuilder.append("()");
        }

        return keyBuilder.toString();
    }


}
