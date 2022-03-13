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
package io.jboot.components.cache.interceptor;

import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.CachePut;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.cache.annotation.CachesEvict;

import java.lang.reflect.Method;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
@AutoLoad
public class CacheInterceptorBuilder implements InterceptorBuilder {

    // 缓存拦截器的权重
    // 其他拦截器器的默认权重是 1+
    public static final int INTERCEPTOR_WEIGHT = 100;

    @Override
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {
        if (Util.hasAnnotation(method, CacheEvict.class)) {
            interceptors.add(CacheEvictInterceptor.class, INTERCEPTOR_WEIGHT);
        }

        if (Util.hasAnnotation(method, Cacheable.class)) {
            interceptors.add(CacheableInterceptor.class, INTERCEPTOR_WEIGHT);
        }

        if (Util.hasAnnotation(method, CachePut.class)) {
            interceptors.add(CachePutInterceptor.class, INTERCEPTOR_WEIGHT);
        }

        if (Util.hasAnnotation(method, CachesEvict.class)) {
            interceptors.add(CachesEvictInterceptor.class, INTERCEPTOR_WEIGHT);
        }
    }


}
