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
package io.jboot.aop.cglib;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;
import com.jfinal.aop.Invocation;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.JbootAopFactory;
import io.jboot.utils.ClassUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


class JbootCglibCallback implements MethodInterceptor {

    private static final Set<String> excludedMethodName = buildExcludedMethodName();
    private static final InterceptorManager interMan = InterceptorManager.me();

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (excludedMethodName.contains(method.getName())) {
            return methodProxy.invokeSuper(target, args);
        }

        Class<?> targetClass = ClassUtil.getUsefulClass(target.getClass());

        JbootCglibProxyFactory.MethodKey key = JbootCglibProxyFactory.IntersCache.getMethodKey(targetClass, method);
        Interceptor[] inters = JbootCglibProxyFactory.IntersCache.get(key);
        if (inters == null) {

            // jfinal 原生的构建
            inters = interMan.buildServiceMethodInterceptor(targetClass, method);

            // 通过 InterceptorBuilder 去构建
            List<InterceptorBuilder> interceptorBuilder = JbootAopFactory.me().getInterceptorBuilders();
            if (interceptorBuilder != null && interceptorBuilder.size() > 0) {
                LinkedList<Interceptor> list = toLinkedList(inters);
                for (InterceptorBuilder builder : interceptorBuilder) {
                    builder.build(targetClass, method, list);
                }
                if (list.isEmpty()) {
                    inters = InterceptorManager.NULL_INTERS;
                } else {
                    inters = list.toArray(new Interceptor[0]);
                }
            }

            JbootCglibProxyFactory.IntersCache.put(key, inters);
        }

        Invocation invocation = new Invocation(target, method, inters,
                x -> {
                    return methodProxy.invokeSuper(target, x);
                }
                , args);


        invocation.invoke();
        return invocation.getReturnValue();
    }



    private static final Set<String> buildExcludedMethodName() {
        Set<String> excludedMethodName = new HashSet<String>(64, 0.25F);
        Method[] methods = Object.class.getDeclaredMethods();
        for (Method m : methods) {
            excludedMethodName.add(m.getName());
        }
        // getClass() registerNatives() can not be enhanced
        // excludedMethodName.remove("getClass");
        // excludedMethodName.remove("registerNatives");
        return excludedMethodName;
    }



    private static LinkedList<Interceptor> toLinkedList(Interceptor[] interceptors) {
        LinkedList<Interceptor> linkedList = new LinkedList<>();
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                linkedList.add(interceptor);
            }
        }
        return linkedList;
    }
}


