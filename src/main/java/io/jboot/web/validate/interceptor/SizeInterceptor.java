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
package io.jboot.web.validate.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.core.weight.Weight;
import io.jboot.utils.ClassUtil;

import javax.validation.constraints.Size;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AutoLoad
@Weight(100)
public class SizeInterceptor implements Interceptor, InterceptorBuilder {

    @Override
    public void intercept(Invocation inv) {
        Parameter[] parameters = inv.getMethod().getParameters();

        for (int index = 0; index < parameters.length; index++) {
            Size size = parameters[index].getAnnotation(Size.class);
            if (size != null) {
                Object validObject = inv.getArg(index);
                if (validObject != null && (size.min() > ((Number) validObject).intValue() || size.max() < ((Number) validObject).intValue())) {
                    String reason = parameters[index].getName() + " size value is " + size.min() + " ~ " + size.max() + ", but current value is " + validObject + " at method:" + ClassUtil.buildMethodString(inv.getMethod());
                    Ret paras = Ret.by("max", size.max()).set("min", size.min());
                    Util.renderError(inv.getController(), size.message(), paras, reason);
                    return;
                }
            }
        }

        inv.invoke();
    }


    @Override
    public void build(Class<?> serviceClass, Method method, Interceptors interceptors) {
        if (Controller.class.isAssignableFrom(serviceClass)) {
            Parameter[] parameters = method.getParameters();
            if (parameters != null && parameters.length > 0) {
                for (Parameter p : parameters) {
                    if (p.getAnnotation(Size.class) != null) {
                        interceptors.add(this);
                        return;
                    }
                }
            }
        }
    }
}