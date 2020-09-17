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
import com.jfinal.core.Controller;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.web.validate.CaptchaValidate;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.RegexValidate;

import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
@AutoLoad
public class ValidateInterceptorBuilder implements InterceptorBuilder {


    @Override
    public void build(Class<?> serviceClass, Method method, LinkedList<Interceptor> interceptors) {


        EmptyValidate emptyParaValidate = method.getAnnotation(EmptyValidate.class);
        if (emptyParaValidate != null && Controller.class.isAssignableFrom(serviceClass)) {
            interceptors.add(new EmptyValidateInterceptor());
        }

        RegexValidate regexValidate = method.getAnnotation(RegexValidate.class);
        if (regexValidate != null && Controller.class.isAssignableFrom(serviceClass)) {
            interceptors.add(new RegexValidateInterceptor());
        }

        CaptchaValidate captchaValidate = method.getAnnotation(CaptchaValidate.class);
        if (captchaValidate != null && Controller.class.isAssignableFrom(serviceClass)) {
            interceptors.add(new CaptchaValidateInterceptor());
        }
    }


}
