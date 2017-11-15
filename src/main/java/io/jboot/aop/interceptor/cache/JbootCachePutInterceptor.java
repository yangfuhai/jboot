/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.aop.interceptor.cache;


import io.jboot.Jboot;
import io.jboot.core.cache.annotation.CachePut;
import io.jboot.exception.JbootAssert;
import io.jboot.utils.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 缓存设置拦截器
 */
public class JbootCachePutInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        Class targetClass = methodInvocation.getThis().getClass();
        Method method = methodInvocation.getMethod();

        Object result = methodInvocation.proceed();

        CachePut cachePut = method.getAnnotation(CachePut.class);
        if (cachePut == null) {
            return result;
        }

        String unlessString = cachePut.unless();
        if (StringUtils.isNotBlank(unlessString)) {
            unlessString = String.format("#(%s)", unlessString);
            String unlessBoolString = Kits.engineRender(unlessString, method, methodInvocation.getArguments());
            if ("true".equals(unlessBoolString)) {
                return result;
            }
        }


        String cacheName = cachePut.name();
        JbootAssert.assertTrue(StringUtils.isNotBlank(cacheName),
                String.format("CachePut.name()  must not empty in method [%s]!!!", targetClass.getName() + "#" + method.getName()));

        String cacheKey = Kits.buildCacheKey(cachePut.key(), targetClass, method, methodInvocation.getArguments());

        if (cachePut.liveSeconds() > 0) {
            Jboot.me().getCache().put(cacheName, cacheKey, result, cachePut.liveSeconds());
        } else {
            Jboot.me().getCache().put(cacheName, cacheKey, result);
        }
        return result;
    }


}
