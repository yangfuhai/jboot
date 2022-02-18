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
import com.jfinal.kit.Ret;
import io.jboot.components.valid.ValidUtil;
import io.jboot.utils.ClassUtil;

import javax.validation.constraints.DecimalMax;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DecimalMaxInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        Parameter[] parameters = inv.getMethod().getParameters();

        for (int index = 0; index < parameters.length; index++) {
            DecimalMax decimalMax = parameters[index].getAnnotation(DecimalMax.class);
            if (decimalMax != null) {
                Object validObject = inv.getArg(index);
                if (validObject != null && !matches(decimalMax, validObject)) {
                    String reason = parameters[index].getName() + " max value is " + decimalMax.value()
                            + ", but current value is " + validObject + " at method: " + ClassUtil.buildMethodString(inv.getMethod());
                    Ret paras = Ret.by("value", decimalMax.value());
                    ValidUtil.throwValidException(parameters[index].getName(), decimalMax.message(), paras, reason);
                }
            }
        }

        inv.invoke();
    }

    private boolean matches(DecimalMax decimalMax, Object validObject) {
        if (validObject instanceof BigInteger) {
            return ((BigInteger) validObject).compareTo(new BigInteger(decimalMax.value())) <= 0;
        } else if (validObject instanceof BigDecimal) {
            return ((BigDecimal) validObject).compareTo(new BigDecimal(decimalMax.value())) <= 0;
        } else if (validObject instanceof CharSequence) {
            return (new BigDecimal(validObject.toString())).compareTo(new BigDecimal(decimalMax.value())) <= 0;
        } else if (validObject instanceof Float) {
            return ((Float) validObject) <= Float.parseFloat(decimalMax.value());
        } else if (validObject instanceof Double) {
            return ((Double) validObject) <= Double.parseDouble(decimalMax.value());
        } else if (validObject instanceof Number) {
            return ((Number) validObject).longValue() <= Long.parseLong(decimalMax.value());
        }
        return false;
    }


}