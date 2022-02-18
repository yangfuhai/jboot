/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.shiro;

import io.jboot.Jboot;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;

import java.lang.reflect.Method;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
@AutoLoad
public class ShiroInterceptorBuilder implements InterceptorBuilder {

    private static final JbootShiroConfig config = Jboot.config(JbootShiroConfig.class);

    @Override
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {

        if (config.isConfigOK() &&
                Util.isController(targetClass)  // 暂时只对 controller 层的方法进行拦截
        ) {
            boolean needIntercept = JbootShiroManager.me().buildShiroInvoker(targetClass, method);
            if (needIntercept) {
                interceptors.add(JbootShiroInterceptor.class);
            }
        }
    }


}
