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

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.aop.annotation.DefaultValue;
import io.jboot.core.weight.Weight;
import io.jboot.utils.ObjectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AutoLoad
@Weight(999)
public class DefaultValueInterceptor implements Interceptor, InterceptorBuilder {

    @Override
    public void intercept(Invocation inv) {
        Parameter[] parameters = inv.getMethod().getParameters();
        for (int index = 0; index < parameters.length; index++) {
            DefaultValue defaultValue = parameters[index].getAnnotation(DefaultValue.class);
            if (defaultValue != null) {
                Object arg = inv.getArg(index);
                if (arg == null || isPrimitiveDefaultValue(arg, parameters[index].getType())) {
                    Object value = ObjectUtil.convert(defaultValue.value(), parameters[index].getType());
                    if (value != null) {
                        inv.setArg(index, value);
                    }
                }
            }
        }

        inv.invoke();
    }

    public static boolean isPrimitiveDefaultValue(Object value, Class<?> paraClass) {
        if (paraClass == int.class || paraClass == long.class || paraClass == float.class || paraClass == double.class || paraClass == short.class) {
            return ((Number) value).intValue() == 0;
        } else if (paraClass == boolean.class) {
            return !(boolean) value;
        }
        return false;
    }


    @Override
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {
        Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length > 0) {
            for (Parameter p : parameters) {
                if (p.getAnnotation(DefaultValue.class) != null) {
                    interceptors.addIfNotExist(this);
                    break;
                }
            }
        }

    }
}