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
package io.jboot.component.shiro;

import io.jboot.Jboot;
import io.jboot.component.shiro.error.ShiroErrorProcess;
import io.jboot.component.shiro.processer.AuthorizeResult;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

/**
 * Shiro 拦截器
 */
public class JbootShiroInterceptor implements FixedInterceptor {

    private static JbootShiroConfig config = Jboot.config(JbootShiroConfig.class);

    @Override
    public void intercept(FixedInvocation inv) {
        if (!config.isConfigOK()) {
            inv.invoke();
            return;
        }

        SsoShiroBridge ssoShiroBridge = JbootShiroManager.me().getSsoShiroBridge();
        if (ssoShiroBridge != null && ssoShiroBridge.isSsoCallBackRequest(inv.getController())) {
            ssoShiroBridge.subjectLogin(inv.getController());
        }

        AuthorizeResult result = JbootShiroManager.me().invoke(inv.getActionKey());

        if (result == null || result.isOk()) {
            inv.invoke();
            return;
        }

        ShiroErrorProcess shiroErrorProcess = JbootShiroManager.me().getShiroErrorProcess();
        shiroErrorProcess.doProcessError(inv, result.getErrorCode());

    }

}
