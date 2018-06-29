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
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.aop.interceptor.cache.JbootCacheEvictInterceptor;
import io.jboot.aop.interceptor.cache.JbootCacheInterceptor;
import io.jboot.aop.interceptor.cache.JbootCachePutInterceptor;
import io.jboot.aop.interceptor.cache.JbootCachesEvictInterceptor;
import io.jboot.aop.interceptor.metric.*;
import io.jboot.component.hystrix.JbootHystrixCommand;
import io.jboot.component.hystrix.annotation.EnableHystrixCommand;
import io.jboot.component.metric.JbootMetricManager;
import io.jboot.utils.ClassKits;
import io.jboot.utils.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 用户Hystrix命令调用，方便Hystrix控制
 */
public class AopInterceptor implements MethodInterceptor {

    private static final Log LOG = Log.getLog(AopInterceptor.class);


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        Class targetClass = methodInvocation.getThis().getClass();

        //过滤掉controller，因为controller由action去执行@Before相关注解了
        if (Controller.class.isAssignableFrom(targetClass)) {
            return methodInvocation.proceed();
        }


        EnableHystrixCommand enableHystrixCommand = methodInvocation.getMethod().getAnnotation(EnableHystrixCommand.class);
        if (enableHystrixCommand == null) {
            return doJFinalAOPInvoke(methodInvocation);
        }


        String commandKey = enableHystrixCommand.key();
        if (StringUtils.isBlank(commandKey)) {
            commandKey = methodInvocation.getMethod().getName();
        }

        return Jboot.hystrix(new JbootHystrixCommand(commandKey) {
            @Override
            public Object run() throws Exception {
                try {
                    return doJFinalAOPInvoke(methodInvocation);
                } catch (Throwable throwable) {
                    throw (Exception) throwable;
                }
            }

            @Override
            protected Object getFallback() {

                String faillMethod = enableHystrixCommand.failMethod();

                if (StringUtils.isBlank(faillMethod)) {
                    LOG.error(getExecutionException().toString(), getExecutionException());
                    return null;
                }


                try {
                    Method method = ClassKits.getUsefulClass(targetClass).getMethod(faillMethod);
                    method.setAccessible(true);
                    return method.invoke(methodInvocation.getThis());
                } catch (Exception e) {
                    LOG.error(e.toString(), e);
                }

                return null;
            }
        });
    }


    private static final Interceptor[] ALL_INTERS = {
            new JbootMetricCounterAopInterceptor(),
            new JbootMetricConcurrencyAopInterceptor(),
            new JbootMetricMeterAopInterceptor(),
            new JbootMetricTimerAopInterceptor(),
            new JbootMetricHistogramAopInterceptor(),
            new JbootCacheEvictInterceptor(),
            new JbootCachesEvictInterceptor(),
            new JbootCachePutInterceptor(),
            new JbootCacheInterceptor()
    };

    private static final Interceptor[] NO_METRIC_INTERS = {
            new JbootCacheEvictInterceptor(),
            new JbootCachesEvictInterceptor(),
            new JbootCachePutInterceptor(),
            new JbootCacheInterceptor()
    };

    private static boolean metricConfigOk = JbootMetricManager.me().isConfigOk();

    private Object doJFinalAOPInvoke(MethodInvocation methodInvocation) throws Throwable {

        Class targetClass = ClassKits.getUsefulClass(methodInvocation.getThis().getClass());
        Method method = methodInvocation.getMethod();

        //service层的所有拦截器，包含了全局的拦截器 和 @Before 的拦截器
        Interceptor[] serviceInterceptors = metricConfigOk
                ? InterceptorManager.me().buildServiceMethodInterceptor(ALL_INTERS, targetClass, method)
                : InterceptorManager.me().buildServiceMethodInterceptor(NO_METRIC_INTERS, targetClass, method);

        JFinalBeforeInvocation invocation = new JFinalBeforeInvocation(methodInvocation, serviceInterceptors, methodInvocation.getArguments());
        invocation.invoke();

        return invocation.getReturnValue();
    }


}
