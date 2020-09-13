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
import io.jboot.aop.JbootAopFactory;
import io.jboot.aop.JbootInterceptorBuilder;
import io.jboot.utils.ArrayUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CglibCallback.
 */
class JbootCglibCallback implements MethodInterceptor {

    private static final Set<String> excludedMethodName = buildExcludedMethodName();
    private static final InterceptorManager interMan = InterceptorManager.me();

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (excludedMethodName.contains(method.getName())) {
            return methodProxy.invokeSuper(target, args);
        }

        Class<?> targetClass = target.getClass();
        if (targetClass.getName().indexOf("$$EnhancerBy") != -1) {
            targetClass = targetClass.getSuperclass();
        }


        JbootCglibProxyFactory.MethodKey key = JbootCglibProxyFactory.IntersCache.getMethodKey(targetClass, method);
        Interceptor[] inters = JbootCglibProxyFactory.IntersCache.get(key);
        if (inters == null) {

            // jfinal 原生的构建
            inters = interMan.buildServiceMethodInterceptor(targetClass, method);

            List<JbootInterceptorBuilder> interceptorBuilders = JbootAopFactory.me().getInterceptorBuilders();
            if (interceptorBuilders != null && interceptorBuilders.size() > 0) {
                for (JbootInterceptorBuilder builder : interceptorBuilders) {
                    Interceptor[] buildInters = builder.build(targetClass, method);
                    if (buildInters != null || buildInters.length > 0) {
                        inters = ArrayUtil.concat(inters, buildInters);
                    }
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
}


