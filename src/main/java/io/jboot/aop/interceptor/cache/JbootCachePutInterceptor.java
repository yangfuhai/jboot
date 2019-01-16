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
import io.jboot.components.cache.annotation.CachePut;
import io.jboot.exception.JbootException;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;

/**
 * 缓存设置拦截器
 */
public class JbootCachePutInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {

        //先执行，之后再保存数据
        inv.invoke();

        Method method = inv.getMethod();
        CachePut cachePut = method.getAnnotation(CachePut.class);
        if (cachePut == null) {
            return;
        }

        Object result = inv.getReturnValue();

        String unless = AnnotationUtil.get(cachePut.unless());
        if (Utils.isUnless(unless, method, inv.getArgs())) {
            return;
        }

        Class targetClass = inv.getTarget().getClass();
        String cacheName = AnnotationUtil.get(cachePut.name());

        if (StrUtil.isBlank(cacheName)) {
            throw new JbootException(String.format("CacheEvict.name()  must not empty in method [%s].",
                    ClassUtil.getUsefulClass(targetClass).getName() + "." + method.getName()));
        }

        String cacheKey = Utils.buildCacheKey(AnnotationUtil.get(cachePut.key()), targetClass, method, inv.getArgs());

        if (cachePut.liveSeconds() > 0) {
            Jboot.getCache().put(cacheName, cacheKey, result, cachePut.liveSeconds());
        } else {
            Jboot.getCache().put(cacheName, cacheKey, result);
        }
    }
}
