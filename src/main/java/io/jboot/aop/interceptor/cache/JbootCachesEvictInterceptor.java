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


import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.core.cache.annotation.CacheEvict;
import io.jboot.core.cache.annotation.CachesEvict;
import io.jboot.exception.JbootAssert;
import io.jboot.utils.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 清除缓存操作的拦截器
 */
public class JbootCachesEvictInterceptor implements MethodInterceptor {
    private static final Log LOG = Log.getLog(JbootCachesEvictInterceptor.class);

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        Class targetClass = methodInvocation.getThis().getClass();
        Method method = methodInvocation.getMethod();

        CachesEvict cachesEvict = method.getAnnotation(CachesEvict.class);
        if (cachesEvict == null) {
            return methodInvocation.proceed();
        }

        CacheEvict[] evicts = cachesEvict.value();

        for (CacheEvict evict : evicts) {
            Object[] arguments = methodInvocation.getArguments();
            try {
                doCacheEvict(arguments, targetClass, method, evict);
            } catch (Exception ex) {
                LOG.error(ex.toString(), ex);
            }
        }

        return methodInvocation.proceed();
    }

    private void doCacheEvict(Object[] arguments, Class targetClass, Method method, CacheEvict evict) {
        String unlessString = evict.unless();
        if (Kits.isUnless(unlessString, method, arguments)) {
            return;
        }

        String cacheName = evict.name();
        JbootAssert.assertTrue(StringUtils.isNotBlank(cacheName),
                String.format("CacheEvict.name()  must not empty in method [%s]!!!", targetClass.getName() + "#" + method.getName()));

        if ("*".equals(evict.key().trim())) {
            Jboot.me().getCache().removeAll(cacheName);
            return;
        }

        String cacheKey = Kits.buildCacheKey(evict.key(), targetClass, method, arguments);
        Jboot.me().getCache().remove(cacheName, cacheKey);
    }


}
