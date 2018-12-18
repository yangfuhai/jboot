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
import io.jboot.core.cache.annotation.CachePut;
import io.jboot.exception.JbootException;
import io.jboot.kits.ClassKits;
import io.jboot.kits.StringKits;

import java.lang.reflect.Method;

/**
 * 缓存设置拦截器
 */
public class JbootCachePutInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {

        //先执行，之后再保持数据
        inv.invoke();

        Method method = inv.getMethod();
        CachePut cachePut = method.getAnnotation(CachePut.class);
        if (cachePut == null) {
            return;
        }

        Object result = inv.getReturnValue();

        String unlessString = cachePut.unless();
        if (StringKits.isNotBlank(unlessString)) {
            unlessString = String.format("#(%s)", unlessString);
            String unlessBoolString = Kits.engineRender(unlessString, method, inv.getArgs());
            if ("true".equals(unlessBoolString)) {
                return;
            }
        }


        Class targetClass = inv.getTarget().getClass();
        String cacheName = cachePut.name();

        if (StringKits.isBlank(cacheName)) {
            throw new JbootException(String.format("CacheEvict.name()  must not empty in method [%s].",
                    ClassKits.getUsefulClass(targetClass).getName() + "." + method.getName()));
        }

        String cacheKey = Kits.buildCacheKey(cachePut.key(), targetClass, method, inv.getArgs());

        if (cachePut.liveSeconds() > 0) {
            Jboot.getCache().put(cacheName, cacheKey, result, cachePut.liveSeconds());
        } else {
            Jboot.getCache().put(cacheName, cacheKey, result);
        }
    }
}
