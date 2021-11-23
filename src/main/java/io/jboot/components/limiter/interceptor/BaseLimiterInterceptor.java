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
package io.jboot.components.limiter.interceptor;


import com.google.common.util.concurrent.RateLimiter;
import com.jfinal.aop.Invocation;
import io.jboot.components.limiter.LimiterManager;
import io.jboot.components.limiter.redis.RedisRateLimitUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Semaphore;

public abstract class BaseLimiterInterceptor {


    protected void doInterceptForConcurrency(int rate, String resource, String fallback, Invocation inv) {
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


    protected void doInterceptForTokenBucket(int rate, String resource, String fallback, Invocation inv) {
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

    protected void doInterceptForTokenBucketWithCluster(int rate, String resource, String fallback, Invocation inv) {
        //允许通行
        if (RedisRateLimitUtil.tryAcquire(resource, rate)) {
            inv.invoke();
        }
        //不允许通行
        else {
            doExecFallback(resource, fallback, inv);
        }
    }

    protected void doExecFallback(String resource, String fallback, Invocation inv) {
        LimiterManager.me().processFallback(resource, fallback, inv);
    }


    protected String getPackageOrTarget(Invocation inv) {
        return inv.isActionInvocation() ? buildUrl(inv) : ClassUtil.buildMethodString(inv.getMethod());
    }

    protected String buildUrl(Invocation inv) {
        HttpServletRequest request = inv.getController().getRequest();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        return StrUtil.isBlank(query) ? uri : uri + "?" + query;
    }

}
