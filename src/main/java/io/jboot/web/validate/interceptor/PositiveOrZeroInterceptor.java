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
import io.jboot.utils.ClassUtil;

import javax.validation.constraints.PositiveOrZero;
import java.lang.reflect.Parameter;

public class PositiveOrZeroInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        Parameter[] parameters = inv.getMethod().getParameters();

        for (int index = 0; index < parameters.length; index++) {
            PositiveOrZero positiveOrZero = parameters[index].getAnnotation(PositiveOrZero.class);
            if (positiveOrZero != null) {
                Object validObject = inv.getArg(index);
                if (validObject == null || ((Number) validObject).longValue() < 0) {
                    String reason = parameters[index].getName() + " is null or less than 0 at method:" + ClassUtil.buildMethodString(inv.getMethod());
                    Util.throwValidException(positiveOrZero.message(), reason);
                }
            }
        }

        inv.invoke();
    }


}