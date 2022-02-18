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

import javax.validation.constraints.Pattern;
import java.lang.reflect.Parameter;

public class PatternInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        Parameter[] parameters = inv.getMethod().getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Pattern pattern = parameters[index].getAnnotation(Pattern.class);
            if (pattern != null) {
                Object validObject = inv.getArg(index);
                if (validObject == null || !matches(pattern, validObject.toString())) {
                    String reason = parameters[index].getName() + " is null or not matches the regex at method: " + ClassUtil.buildMethodString(inv.getMethod());
                    Ret paras = Ret.by("regexp", pattern.regexp());
                    ValidUtil.throwValidException(parameters[index].getName(), pattern.message(), paras, reason);
                }
            }
        }


        inv.invoke();
    }

    private static boolean matches(Pattern pattern, String value) {
        Pattern.Flag[] flags = pattern.flags();
        if (flags.length == 0) {
            return value.matches(pattern.regexp());
        }

        int intFlag = 0;
        for (Pattern.Flag flag : flags) {
            intFlag = intFlag | flag.getValue();
        }

        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern.regexp(), intFlag);
        return p.matcher(value).matches();

    }


}