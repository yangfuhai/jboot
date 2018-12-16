/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.core.config.server;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.Ret;
import io.jboot.Jboot;
import io.jboot.core.config.JbootConfigConfig;


public class JbootConfigInterceptor implements Interceptor {

    static JbootConfigConfig config = Jboot.config(JbootConfigConfig.class);


    @Override
    public void intercept(Invocation inv) {
        if (!config.isServerEnable()) {
            inv.getController().renderJson(Ret.fail("msg", "sorry,  you have no permission to visit this page. "));
            return;
        }

        inv.invoke();
    }
}
