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
package io.jboot.web.cors;

import com.jfinal.aop.Interceptor;
import com.jfinal.ext.cors.EnableCORS;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.annotation.AutoLoad;

import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
@AutoLoad
public class CORSInterceptorBuilder implements InterceptorBuilder {


    @Override
    public void build(Class<?> serviceClass, Method method, LinkedList<Interceptor> interceptors) {
        EnableCORS enableCORS = getAnnotation(serviceClass,method);
        if (enableCORS != null) {
            interceptors.add(new CORSInterceptor());
        }

    }

    private EnableCORS getAnnotation(Class<?> serviceClass, Method method) {
        EnableCORS enableCORS = serviceClass.getClass().getAnnotation(EnableCORS.class);
        return enableCORS != null ? enableCORS : method.getAnnotation(EnableCORS.class);
    }


}
