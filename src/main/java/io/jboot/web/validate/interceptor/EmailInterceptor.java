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
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.core.weight.Weight;
import io.jboot.utils.ClassUtil;
import io.jboot.web.validate.Regex;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AutoLoad
@Weight(100)
public class EmailInterceptor implements Interceptor, InterceptorBuilder {

    @Override
    public void intercept(Invocation inv) {
        Parameter[] parameters = inv.getMethod().getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Email email = parameters[index].getAnnotation(Email.class);
            if (email != null) {
                Object validObject = inv.getArg(index);
                if (validObject == null || !matches(email, validObject.toString())) {
                    String reason = parameters[index].getName() + " is not email at method:" + ClassUtil.buildMethodString(inv.getMethod());
                    Util.renderError(inv.getController(), email.message(), reason);
                    return;
                }
            }
        }


        inv.invoke();
    }

    private static boolean matches(Email email, String value) {
        Pattern.Flag[] flags = email.flags();
        String regexp = ".*".equals(email.regexp()) ? Regex.EMAIL : email.regexp();
        if (flags.length == 0) {
            return value.matches(regexp);
        }

        int intFlag = 0;
        for (Pattern.Flag flag : flags) {
            intFlag = intFlag | flag.getValue();
        }

        java.util.regex.Pattern p = java.util.regex.Pattern.compile(regexp, intFlag);
        return p.matcher(value).matches();

    }


    @Override
    public void build(Class<?> serviceClass, Method method, Interceptors interceptors) {
        if (Controller.class.isAssignableFrom(serviceClass)) {
            Parameter[] parameters = method.getParameters();
            if (parameters != null && parameters.length > 0) {
                for (Parameter p : parameters) {
                    if (p.getAnnotation(Email.class) != null) {
                        interceptors.add(this);
                        return;
                    }
                }
            }
        }
    }
}