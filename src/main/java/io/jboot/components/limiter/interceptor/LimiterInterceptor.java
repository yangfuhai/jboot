/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.components.limiter.annotation.EnableLimit;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;

public class LimiterInterceptor extends BaseLimiterInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

        String packageOrTarget = getPackageOrTarget(inv);
        EnableLimit enableLimit = inv.getMethod().getAnnotation(EnableLimit.class);
        String resource = StrUtil.obtainDefaultIfBlank(enableLimit.resource(), packageOrTarget);

        doInterceptByLimitInfo(enableLimit, resource, inv);
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


}
