/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.limitation.web;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;
import io.jboot.exception.JbootException;
import io.jboot.utils.ClassKits;
import io.jboot.web.limitation.LimitationConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.limitation.web
 */
public class LimitationControllerInter implements Interceptor {


    @Override
    public void intercept(Invocation inv) {

        if (!getAuthorizer().onAuthorize(inv.getController())) {
            inv.getController().renderError(404);
            return;
        }

        inv.invoke();
    }

    private static Authorizer authorizer;

    private Authorizer getAuthorizer() {

        if (authorizer == null) {
            String authorizerClass = Jboot.config(LimitationConfig.class).getWebAuthorizer();
            authorizer = ClassKits.newInstance(authorizerClass);
            if (authorizer == null) {
                throw new JbootException("can not init authorizer for class : " + authorizerClass);
            }
        }

        return authorizer;
    }


}
