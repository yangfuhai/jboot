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
package io.jboot.aop.interceptor.cache;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCacheConfig;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.utils.AnnotationUtil;

import java.lang.reflect.Method;

/**
 * 缓存操作的拦截器
 */
public class JbootCacheInterceptor implements Interceptor {

    private static final String NULL_VALUE = "NULL_VALUE";

    private static final JbootCacheConfig CONFIG = Jboot.config(JbootCacheConfig.class);

    @Override
    public void intercept(Invocation inv) {

        Method method = inv.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if (cacheable == null) {
            inv.invoke();
            return;
        }

        String unlessString = AnnotationUtil.get(cacheable.unless());
        if (Utils.isUnless(unlessString, method, inv.getArgs())) {
            inv.invoke();
            return;
        }

        Class targetClass = inv.getTarget().getClass();
        String cacheName = AnnotationUtil.get(cacheable.name());
        Utils.ensureCachenameAvailable(method, targetClass, cacheName);
        String cacheKey = Utils.buildCacheKey(AnnotationUtil.get(cacheable.key()), targetClass, method, inv.getArgs());

        Object data = Jboot.getCache().get(cacheName, cacheKey);
        if (data != null) {
            if (NULL_VALUE.equals(data)) {
                inv.setReturnValue(null);
            } else {
                inv.setReturnValue(data);
            }
            return;
        }

        inv.invoke();

        data = inv.getReturnValue();
        putDataToCache(cacheable,cacheName,cacheKey,data == null ? NULL_VALUE : data);
    }

    protected void putDataToCache(Cacheable cacheable, String cacheName, String cacheKey, Object data) {
        int liveSeconds = cacheable.liveSeconds() > 0
                ? cacheable.liveSeconds()
                : CONFIG.getAopCacheLiveSeconds();
        if (liveSeconds > 0) {
            Jboot.getCache().put(cacheName, cacheKey, data, liveSeconds);
        } else {
            Jboot.getCache().put(cacheName, cacheKey, data);
        }
    }


}
