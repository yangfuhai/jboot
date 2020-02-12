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
package io.jboot.components.cache.interceptor;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.components.cache.annotation.CacheEvict;

import java.lang.reflect.Method;

/**
 * 清除缓存操作的拦截器
 */
public class JbootCacheEvictInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

        Method method = inv.getMethod();

        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
        if (cacheEvict == null) {
            inv.invoke();
            return;
        }

        Class targetClass = inv.getTarget().getClass();

        if (cacheEvict.beforeInvocation()) {
            Utils.doCacheEvict(inv.getArgs(), targetClass, method, cacheEvict);
        }

        inv.invoke();

        if (!cacheEvict.beforeInvocation()) {
            Utils.doCacheEvict(inv.getArgs(), targetClass, method, cacheEvict);
        }
    }
}
