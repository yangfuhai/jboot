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


import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.component.hystrix.JbootHystrixCommand;
import io.jboot.component.hystrix.annotation.EnableHystrixCommand;
import io.jboot.exception.JbootException;
import io.jboot.utils.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 用户Hystrix命令调用，方便Hystrix控制
 */
public class JbootHystrixCommandInterceptor implements MethodInterceptor {

    static Log log = Log.getLog(JbootHystrixCommandInterceptor.class);


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        EnableHystrixCommand enableHystrixCommand = methodInvocation.getThis().getClass().getAnnotation(EnableHystrixCommand.class);
        final String faillMethod = enableHystrixCommand.failMethod();
        final String key = enableHystrixCommand.key();
        if (StringUtils.isBlank(key)) {
            throw new JbootException("key must not empty in @EnableHystrixCommand at " + methodInvocation.getThis().getClass().getName() + "." + methodInvocation.getMethod());
        }

        return Jboot.hystrix(new JbootHystrixCommand(key) {
            @Override
            public Object run() throws Exception {
                try {
                    return methodInvocation.proceed();
                } catch (Throwable throwable) {
                    throw (Exception) throwable;
                }
            }

            @Override
            protected Object getFallback() {
                if (StringUtils.isBlank(faillMethod)) {
                    getExecutionException().printStackTrace();
                    return null;
                }


                Method method = null;
                try {
                    method = methodInvocation.getThis().getClass().getMethod(faillMethod, JbootHystrixCommand.class);
                } catch (NoSuchMethodException ex) {
                }

                if (method != null) {
                    try {
                        method.setAccessible(true);
                        return method.invoke(methodInvocation.getThis(), this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    method = methodInvocation.getThis().getClass().getMethod(faillMethod);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                

                if (method != null) {
                    try {
                        method.setAccessible(true);
                        return method.invoke(methodInvocation.getThis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                return super.getFallback();
            }
        });


    }


}
