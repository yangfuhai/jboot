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
package io.jboot.components.valid.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.components.valid.ValidUtil;
import io.jboot.utils.ClassUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import java.lang.reflect.Parameter;
import java.util.Set;

public class ValidInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {

        Parameter[] parameters = inv.getMethod().getParameters();

        for (int index = 0; index < parameters.length; index++) {
            if (parameters[index].getAnnotation(Valid.class) != null) {
                Object validObject = inv.getArg(index);
                if (validObject == null) {
                    continue;
                }
                Set<ConstraintViolation<Object>> constraintViolations = ValidUtil.validate(validObject);
                if (constraintViolations != null && constraintViolations.size() > 0) {
                    StringBuilder msg = new StringBuilder();
                    for (ConstraintViolation<?> cv : constraintViolations) {
                        msg.append(cv.getRootBeanClass().getName())
                                .append(".")
                                .append(cv.getPropertyPath())
                                .append(cv.getMessage());
                    }
                    String reason = parameters[index].getName() + " is valid failed at method: " + ClassUtil.buildMethodString(inv.getMethod());
                    ValidUtil.throwValidException(parameters[index].getName(), msg.toString(), reason);

                }
            }
        }

        inv.invoke();
    }


}