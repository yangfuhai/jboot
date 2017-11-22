/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
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
import io.jboot.component.hystrix.HystrixRunnable;
import io.jboot.component.hystrix.annotation.EnableHystrixCommand;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 用户Hystrix命令调用，方便Hystrix控制
 */
public class JbootHystrixCommandInterceptor implements MethodInterceptor {

    static Log log = Log.getLog(JbootHystrixCommandInterceptor.class);


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        EnableHystrixCommand enableHystrixCommand = methodInvocation.getThis().getClass().getAnnotation(EnableHystrixCommand.class);

        return Jboot.hystrix(enableHystrixCommand.key(), new HystrixRunnable() {
            @Override
            public Object run() {
                try {
                    return methodInvocation.proceed();
                } catch (Throwable throwable) {
                    log.error(throwable.toString(), throwable);
                }
                return null;
            }
        });


    }


}
