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
import io.jboot.utils.StrUtil;

import javax.validation.constraints.NotBlank;
import java.lang.reflect.Parameter;

public class NotBlankInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        Parameter[] parameters = inv.getMethod().getParameters();
        for (int index = 0; index < parameters.length; index++) {
            NotBlank notBlank = parameters[index].getAnnotation(NotBlank.class);
            if (notBlank != null) {
                Object validObject = inv.getArg(index);
                if (validObject == null || (validObject instanceof String && StrUtil.isBlank((String) validObject))) {
                    String msg = parameters[index].getName() + " is blank at method: " + ClassUtil.buildMethodString(inv.getMethod());
                    ValidUtil.throwValidException(parameters[index].getName(), notBlank.message(), msg);
                }
            }
        }

        inv.invoke();
    }


}