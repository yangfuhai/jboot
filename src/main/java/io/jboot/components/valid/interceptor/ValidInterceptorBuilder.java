/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {
        Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length > 0) {
            for (Parameter p : parameters) {

                if (p.getAnnotation(DecimalMax.class) != null) {
                    interceptors.addIfNotExist(DecimalMaxInterceptor.class);
                }

                if (p.getAnnotation(DecimalMin.class) != null) {
                    interceptors.addIfNotExist(DecimalMinInterceptor.class);
                }

                if (p.getAnnotation(Digits.class) != null) {
                    interceptors.addIfNotExist(DigitsInterceptor.class);
                }

                if (p.getAnnotation(Email.class) != null) {
                    interceptors.addIfNotExist(EmailInterceptor.class);
                }

                if (p.getAnnotation(Max.class) != null) {
                    interceptors.addIfNotExist(MaxInterceptor.class);
                }

                if (p.getAnnotation(Min.class) != null) {
                    interceptors.addIfNotExist(MinInterceptor.class);
                }

                if (p.getAnnotation(Negative.class) != null) {
                    interceptors.addIfNotExist(NegativeInterceptor.class);
                }

                if (p.getAnnotation(NegativeOrZero.class) != null) {
                    interceptors.addIfNotExist(NegativeOrZeroInterceptor.class);
                }

                if (p.getAnnotation(NotBlank.class) != null) {
                    interceptors.addIfNotExist(NotBlankInterceptor.class);
                }

                if (p.getAnnotation(NotEmpty.class) != null) {
                    interceptors.addIfNotExist(NotEmptyInterceptor.class);
                }

                if (p.getAnnotation(NotNull.class) != null) {
                    interceptors.addIfNotExist(NotNullInterceptor.class);
                }

                if (p.getAnnotation(Pattern.class) != null) {
                    interceptors.addIfNotExist(PatternInterceptor.class);
                }

                if (p.getAnnotation(Positive.class) != null) {
                    interceptors.addIfNotExist(PositiveInterceptor.class);
                }

                if (p.getAnnotation(PositiveOrZero.class) != null) {
                    interceptors.addIfNotExist(PositiveOrZeroInterceptor.class);
                }

                if (p.getAnnotation(Size.class) != null) {
                    interceptors.addIfNotExist(SizeInterceptor.class);
                }

                if (p.getAnnotation(Valid.class) != null) {
                    interceptors.addIfNotExist(ValidInterceptor.class);
                }
            }
        }
    }
}