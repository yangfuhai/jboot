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

import com.jfinal.kit.LogKit;
import io.jboot.aop.InterceptorCache;
import io.jboot.aop.cglib.JbootCglibCallback;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.ClassUtil;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MockMethodInterceptor extends JbootCglibCallback {

    private static final Map<InterceptorCache.MethodKey, MockMethodInfo> METHOD_INFO_CACHE = new HashMap<>();

    public static void addMethodInfo(MockMethodInfo value) {
        InterceptorCache.MethodKey methodKey = InterceptorCache.getMethodKey(value.getTargetClass(), value.getTargetMethod());
        METHOD_INFO_CACHE.put(methodKey, value);
    }

    public static void addMockClass(Class<?> mockClass) {
        Class<?>[] interfaces = mockClass.getInterfaces();
        List<MockMethodInfo> mockMethodInfos = new ArrayList<>();
        for (Class<?> inter : interfaces) {
            for (MockMethodInfo mockMethodInfo : METHOD_INFO_CACHE.values()) {
                //相同的代理对象
                if (mockMethodInfo.getTargetClass() == inter) {
                    mockMethodInfos.add(new MockMethodInfo(mockMethodInfo, mockClass));
                }
            }
        }
        mockMethodInfos.forEach(MockMethodInterceptor::addMethodInfo);
    }


    private boolean autoMockInterface;

    public MockMethodInterceptor(boolean autoMockInterface) {
        this.autoMockInterface = autoMockInterface;
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        Class<?> targetClass = ClassUtil.getUsefulClass(target.getClass());

        //对于接口而且没有实现类的情况，target 是一个 Object 类
        if (targetClass == Object.class && method.getDeclaringClass() != Object.class) {
            targetClass = method.getDeclaringClass();
        }

        InterceptorCache.MethodKey methodKey = InterceptorCache.getMethodKey(targetClass, method);

        if (METHOD_INFO_CACHE.containsKey(methodKey)) {
            MockMethodInfo methodInfo = METHOD_INFO_CACHE.get(methodKey);
            return methodInfo.invokeMock(target, args);
        }

        if (autoMockInterface && Modifier.isInterface(targetClass.getModifiers())) {
            if (!("toString".equals(method.getName()) && args.length == 0)) {
                LogKit.warn("Return null for Mock Method: \"" + ClassUtil.buildMethodString(method) + "\", " +
                        "Because the class \"" + targetClass.getName() + "\" is an interface and has no any implementation classes.");
            }
            return null;
        }

        try {
            return super.intercept(target, method, args, methodProxy);
        } catch (Exception ex) {
            if ("initDao".equals(method.getName()) && JbootServiceBase.class == method.getDeclaringClass()) {
                return null;
            } else {
                throw ex;
            }
        }

    }
}
