/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.limiter;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import java.lang.reflect.Method;

public class LimiterInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        String packageOrTarget = getPackageOrTarget(inv);
        LimiterManager.TypeAndRate typeAndRate = LimiterManager.me().matchConfig(packageOrTarget);

        if (typeAndRate != null) {


            return;
        }


    }

    private String getPackageOrTarget(Invocation inv) {
        return inv.isActionInvocation() ? inv.getActionKey() : buildMethodKey(inv.getMethod());
    }

    private String buildMethodKey(Method method) {
        String packageAndClass = method.getDeclaringClass().getName();
        String methodName = method.getName();

        if (method.getParameterCount() > 0) {
            Class[] paraClasses = method.getParameterTypes();
            methodName = methodName + ".(";
            for (Class c : paraClasses) {
                methodName = methodName + c.getSimpleName();
            }
            methodName = methodName + ")";
        } else {
            methodName = methodName + "()";
        }
        return packageAndClass + "." + methodName;
    }
}
