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
package io.jboot.aop.interceptor;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;
import io.jboot.utils.ClassKits;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 兼容 JFinal Service 的拦截器
 */
public class JFinalBeforeInterceptor implements MethodInterceptor {

    InterceptorManager manger = InterceptorManager.me();

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        Class targetClass = methodInvocation.getThis().getClass();
        Method method = methodInvocation.getMethod();

        targetClass = ClassKits.getUsefulClass(targetClass);

        Interceptor[] finalInters = manger.buildServiceMethodInterceptor(InterceptorManager.NULL_INTERS, targetClass, method);
        JFinalBeforeInvocation invocation = new JFinalBeforeInvocation(methodInvocation, finalInters);
        invocation.invoke();
        return invocation.getReturnValue();
    }

}
