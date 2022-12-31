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
package io.jboot.aop;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.aop.annotation.FilterBy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AutoLoad
public class ValueFilterInterceptor implements Interceptor, InterceptorBuilder {

    @Override
    public void intercept(Invocation inv) {
        Parameter[] parameters = inv.getMethod().getParameters();
        for (int index = 0; index < parameters.length; index++) {
            FilterBy filter = parameters[index].getAnnotation(FilterBy.class);
            if (filter != null) {
                Object original = inv.getArg(index);

                Class<? extends ValueFilter>[] classes = filter.value();
                for (Class<? extends ValueFilter> aClass : classes) {
                    ValueFilter vf = Aop.get(aClass);
                    original = vf.doFilter(original);
                }

                inv.setArg(index, original);
            }
        }

        inv.invoke();
    }


    @Override
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {
        Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length > 0) {
            for (Parameter p : parameters) {
                if (p.getAnnotation(FilterBy.class) != null) {
                    interceptors.addIfNotExist(this);
                    break;
                }
            }
        }
    }
}