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
package io.jboot.aop.interceptor.cache;


import io.jboot.Jboot;
import io.jboot.core.cache.annotation.CacheEvict;
import io.jboot.exception.JbootAssert;
import io.jboot.utils.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 清除缓存操作的拦截器
 */
public class JbootCacheEvictInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        Class targetClass = methodInvocation.getThis().getClass();
        Method method = methodInvocation.getMethod();

        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
        if (cacheEvict == null) {
            return methodInvocation.proceed();
        }

        String unlessString = cacheEvict.unless();
        if (StringUtils.isNotBlank(unlessString)) {
            unlessString = String.format("#(%s)", unlessString);
            String unlessBoolString = Kits.engineRender(unlessString, method, methodInvocation.getArguments());
            if ("true".equals(unlessBoolString)) {
                return methodInvocation.proceed();
            }
        }


        String cacheName = cacheEvict.name();
        JbootAssert.assertTrue(StringUtils.isNotBlank(cacheName),
                String.format("CacheEvict.name()  must not empty in method [%s]!!!", targetClass.getName() + "#" + method.getName()));

        String cacheKey = Kits.buildCacheKey(cacheEvict.key(), targetClass, method, methodInvocation.getArguments());

        Jboot.me().getCache().remove(cacheName, cacheKey);
        return methodInvocation.proceed();
    }


}
