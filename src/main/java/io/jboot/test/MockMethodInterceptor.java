/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.test;

import io.jboot.aop.InterceptorCache;
import io.jboot.aop.cglib.JbootCglibCallback;
import io.jboot.utils.ClassUtil;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class MockMethodInterceptor extends JbootCglibCallback {

    private static final Map<InterceptorCache.MethodKey, MockMethodInfo> METHOD_INFO_CACHE = new HashMap();

    public static void addMethodInfo(MockMethodInfo value) {
        InterceptorCache.MethodKey methodKey = InterceptorCache.getMethodKey(value.getTargetClass(), value.getTargetMethod());
        METHOD_INFO_CACHE.put(methodKey, value);
    }


    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        Class<?> targetClass = ClassUtil.getUsefulClass(target.getClass());
        if (targetClass == Object.class){
            targetClass = method.getDeclaringClass();
        }

        InterceptorCache.MethodKey methodKey = InterceptorCache.getMethodKey(targetClass, method);

        if (!METHOD_INFO_CACHE.containsKey(methodKey)) {
            return super.intercept(target, method, args, methodProxy);
        }

        MockMethodInfo methodInfo = METHOD_INFO_CACHE.get(methodKey);
        return methodInfo.invokeMock(target, args);
    }
}
