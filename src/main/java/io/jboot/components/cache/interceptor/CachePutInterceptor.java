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


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.components.cache.AopCache;
import io.jboot.components.cache.annotation.CachePut;
import io.jboot.utils.AnnotationUtil;

import java.lang.reflect.Method;

/**
 * 缓存设置拦截器
 */
public class CachePutInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

        Method method = inv.getMethod();
        CachePut cachePut = method.getAnnotation(CachePut.class);
        if (cachePut == null || (inv.isActionInvocation() && !CacheableInterceptor.isActionCacheEnable())) {
            inv.invoke();
            return;
        }

        if (inv.isActionInvocation()) {
            forController(inv, method, cachePut);
        } else {
            forService(inv, method, cachePut);
        }
    }


    private void forController(Invocation inv, Method method, CachePut cachePut) {
        String unless = AnnotationUtil.get(cachePut.unless());
        if (Utils.isUnless(unless, method, inv.getArgs())) {
            return;
        }

        Class<?> targetClass = inv.getTarget().getClass();
        String cacheName = AnnotationUtil.get(cachePut.name());
        Utils.ensureCacheNameNotBlank(method, cacheName);
        String cacheKey = Utils.buildCacheKey(AnnotationUtil.get(cachePut.key()), targetClass, method, inv.getArgs());

        Controller controller = inv.getController();

        inv.invoke();

        CacheableInterceptor.cacheActionContent(cacheName, cacheKey, cachePut.liveSeconds(), inv , method);
    }


    private void forService(Invocation inv, Method method, CachePut cachePut) {

        inv.invoke();

        Object data = inv.getReturnValue();

        String unless = AnnotationUtil.get(cachePut.unless());
        if (Utils.isUnless(unless, method, inv.getArgs())) {
            return;
        }

        Class<?> targetClass = inv.getTarget().getClass();
        String cacheName = AnnotationUtil.get(cachePut.name());
        Utils.ensureCacheNameNotBlank(method, cacheName);
        String cacheKey = Utils.buildCacheKey(AnnotationUtil.get(cachePut.key()), targetClass, method, inv.getArgs());

        AopCache.putDataToCache(cacheName, cacheKey, data, cachePut.liveSeconds());
    }


}
