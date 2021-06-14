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

    private static final Map<InterceptorCache.MethodKey, MockMethodInfo> methods = new HashMap();

    public static void addMethod(MockMethodInfo value) {
        InterceptorCache.MethodKey methodKey = InterceptorCache.getMethodKey(value.getTargetClass(), value.getTargetMethod());
        methods.put(methodKey, value);
    }


    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        Class<?> targetClass = ClassUtil.getUsefulClass(target.getClass());
        InterceptorCache.MethodKey methodKey = InterceptorCache.getMethodKey(targetClass, method);

        if (!methods.containsKey(methodKey)) {
            return super.intercept(target, method, args, methodProxy);
        }

        MockMethodInfo methodInfo = methods.get(methodKey);
        return methodInfo.invokeMock(target, args);
    }
}
