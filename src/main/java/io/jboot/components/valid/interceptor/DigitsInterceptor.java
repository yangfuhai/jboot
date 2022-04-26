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

import javax.validation.constraints.Digits;
import java.lang.reflect.Parameter;

public class DigitsInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        Parameter[] parameters = inv.getMethod().getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Digits digits = parameters[index].getAnnotation(Digits.class);
            if (digits == null) {
                continue;
            }

            Object validObject = inv.getArg(index);
            if (validObject != null && !matchesDigits(digits, validObject)) {
                String reason = parameters[index].getName() + " not matches @Digits at method: " + ClassUtil.buildMethodString(inv.getMethod());
                Ret paras = Ret.by("integer", digits.integer()).set("fraction", digits.fraction());
                ValidUtil.throwValidException(parameters[index].getName(), digits.message(), paras, reason);
            }
        }

        inv.invoke();
    }

    private boolean matchesDigits(Digits digits, Object validObject) {
        String validString = validObject.toString();
        String[] valids = validString.split("\\.");

        int integer = removeStartZero(valids[0]).length();
        int fraction = valids[0].length() == 1 ? 0 : removeEndZero(valids[1]).length();

        return integer <= digits.integer() && fraction <= digits.fraction();
    }

    private String removeStartZero(String string) {
        while (string.startsWith("0")) {
            string = string.substring(1);
        }
        return string;
    }


    private String removeEndZero(String string) {
        while (string.endsWith("0")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }


}