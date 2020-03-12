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
package io.jboot.aop;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.components.cache.interceptor.JbootCacheEvictInterceptor;
import io.jboot.components.cache.interceptor.JbootCacheInterceptor;
import io.jboot.components.cache.interceptor.JbootCachePutInterceptor;
import io.jboot.components.cache.interceptor.JbootCachesEvictInterceptor;
import io.jboot.components.limiter.LimiterInterceptor;
import io.jboot.support.metric.JbootMetricInterceptor;
import io.jboot.support.seata.interceptor.SeataGlobalTransactionalInterceptor;
import io.jboot.support.sentinel.SentinelInterceptor;

import java.util.LinkedList;
import java.util.List;

public class JbootAopInterceptor implements Interceptor {

    static final Interceptor[] JBOOT_INTERS = {
            new SentinelInterceptor(),
            new LimiterInterceptor(),
            new JbootMetricInterceptor(),
            new JbootCacheEvictInterceptor(),
            new JbootCachesEvictInterceptor(),
            new JbootCachePutInterceptor(),
            new JbootCacheInterceptor(),
            new SeataGlobalTransactionalInterceptor()
    };


    private static Interceptor[] aopInterceptors = JBOOT_INTERS;


    @Override
    public void intercept(Invocation inv) {
        JbootAopInvocation invocation = new JbootAopInvocation(inv, aopInterceptors);
        invocation.invoke();
    }


    /**
     * 添加新的拦截器
     *
     * @param interceptor
     * @param toIndex
     */
    public static void addInterceptor(Interceptor interceptor, int toIndex) {

        if (interceptor == null) {
            throw new NullPointerException("interceptor is null");
        }

        synchronized (JbootAopInterceptor.class) {

            int length = aopInterceptors.length;

            if (toIndex < 0) {
                toIndex = 0;
            }

            if (toIndex > length) {
                toIndex = length;
            }

            Interceptor[] temp = new Interceptor[length + 1];

            System.arraycopy(aopInterceptors, 0, temp, 0, toIndex);
            temp[toIndex] = interceptor;
            if (toIndex < length) {
                System.arraycopy(aopInterceptors, toIndex, temp, toIndex + 1, length - toIndex);
            }

            aopInterceptors = temp;
        }
    }


    /**
     * 移除拦截器
     *
     * @param interceptor
     */
    public static void removeInterceptor(Interceptor interceptor) {

        if (interceptor == null) {
            throw new NullPointerException("interceptor is null");
        }

        synchronized (JbootAopInterceptor.class) {

            int length = aopInterceptors.length;
            List<Interceptor> tempList = new LinkedList<>();

            for (int i = 0; i < length; i++) {
                if (aopInterceptors[i] != interceptor) {
                    tempList.add(aopInterceptors[i]);
                }
            }

            if (tempList.size() != length) {
                aopInterceptors = tempList.toArray(new Interceptor[]{});
            }
        }
    }


    /**
     * 移除拦截器
     *
     * @param clazz
     */
    public static void removeInterceptor(Class<? extends Interceptor> clazz) {

        if (clazz == null) {
            throw new NullPointerException("interceptor class is null");
        }

        synchronized (JbootAopInterceptor.class) {

            int length = aopInterceptors.length;
            List<Interceptor> tempList = new LinkedList<>();

            for (int i = 0; i < length; i++) {
                if (aopInterceptors[i].getClass() != clazz) {
                    tempList.add(aopInterceptors[i]);
                }
            }

            if (tempList.size() != length) {
                aopInterceptors = tempList.toArray(new Interceptor[]{});
            }
        }
    }


}
