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

import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.core.weight.Weight;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AutoLoad
@Weight(100)
public class ValidInterceptorBuilder implements InterceptorBuilder {

    @Override
    public void build(Class<?> serviceClass, Method method, Interceptors interceptors) {
        Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length > 0) {
            for (Parameter p : parameters) {

                if (p.getAnnotation(DecimalMax.class) != null) {
                    interceptors.add(DecimalMaxInterceptor.class);
                }

                if (p.getAnnotation(DecimalMin.class) != null) {
                    interceptors.add(DecimalMinInterceptor.class);
                }

                if (p.getAnnotation(Digits.class) != null) {
                    interceptors.add(DigitsInterceptor.class);
                }

                if (p.getAnnotation(Email.class) != null) {
                    interceptors.add(EmailInterceptor.class);
                }

                if (p.getAnnotation(Max.class) != null) {
                    interceptors.add(MaxInterceptor.class);
                }

                if (p.getAnnotation(Min.class) != null) {
                    interceptors.add(MinInterceptor.class);
                }

                if (p.getAnnotation(Negative.class) != null) {
                    interceptors.add(NegativeInterceptor.class);
                }

                if (p.getAnnotation(NegativeOrZero.class) != null) {
                    interceptors.add(NegativeOrZeroInterceptor.class);
                }

                if (p.getAnnotation(NotBlank.class) != null) {
                    interceptors.add(NotBlankInterceptor.class);
                }

                if (p.getAnnotation(NotEmpty.class) != null) {
                    interceptors.add(NotEmptyInterceptor.class);
                }

                if (p.getAnnotation(NotNull.class) != null) {
                    interceptors.add(NotNullInterceptor.class);
                }

                if (p.getAnnotation(Pattern.class) != null) {
                    interceptors.add(PatternInterceptor.class);
                }

                if (p.getAnnotation(Positive.class) != null) {
                    interceptors.add(PositiveInterceptor.class);
                }

                if (p.getAnnotation(PositiveOrZero.class) != null) {
                    interceptors.add(PositiveOrZeroInterceptor.class);
                }

                if (p.getAnnotation(Size.class) != null) {
                    interceptors.add(SizeInterceptor.class);
                }

                if (p.getAnnotation(Valid.class) != null) {
                    interceptors.add(ValidInterceptor.class);
                }
            }
        }
    }
}