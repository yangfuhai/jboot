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
import com.jfinal.core.ActionException;
import com.jfinal.core.Controller;
import com.jfinal.render.RenderManager;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.core.weight.Weight;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;
import java.util.StringJoiner;

@AutoLoad
@Weight(100)
public class ValidInterceptor implements Interceptor, InterceptorBuilder {

    private static Validator validator;

    @Override
    public void intercept(Invocation inv) {

        Parameter[] parameters = inv.getMethod().getParameters();

        for (int index = 0; index < parameters.length; index++) {
            if (parameters[index].getAnnotation(Valid.class) != null) {
                Object validObject = inv.getArg(index);
                if (validObject != null) {
                    Set<ConstraintViolation<Object>> constraintViolations = getValidator().validate(validObject);
                    if (constraintViolations != null && constraintViolations.size() > 0) {
                        StringJoiner msg = new StringJoiner("; ");
                        for (ConstraintViolation cv : constraintViolations) {
                            msg.add(cv.getRootBeanClass().getName() + "." + cv.getPropertyPath() + cv.getMessage());
                        }
                        throw new ActionException(400, RenderManager.me().getRenderFactory().getErrorRender(400), msg.toString());
                    }
                }
            }
        }

        inv.invoke();
    }


    private static Validator getValidator() {
        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
        return validator;
    }


    @Override
    public void build(Class<?> serviceClass, Method method, Interceptors interceptors) {
        if (Controller.class.isAssignableFrom(serviceClass)) {
            Parameter[] parameters = method.getParameters();
            if (parameters != null && parameters.length > 0) {
                for (Parameter p : parameters) {
                    if (p.getAnnotation(Valid.class) != null) {
                        interceptors.add(this);
                        return;
                    }
                }
            }
        }
    }
}