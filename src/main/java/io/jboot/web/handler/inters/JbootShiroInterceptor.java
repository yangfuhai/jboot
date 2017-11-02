/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.handler.inters;

import com.jfinal.core.Controller;
import io.jboot.Jboot;
import io.jboot.component.shiro.JbootShiroConfig;
import io.jboot.component.shiro.JbootShiroManager;
import io.jboot.component.shiro.processer.AuthorizeResult;
import io.jboot.utils.StringUtils;
import io.jboot.web.handler.HandlerInterceptor;
import io.jboot.web.handler.HandlerInvocation;

/**
 * Shiro 拦截器
 */
public class JbootShiroInterceptor implements HandlerInterceptor {


    private JbootShiroConfig config = Jboot.config(JbootShiroConfig.class);


    @Override
    public void intercept(HandlerInvocation inv) {
        if (!config.isConfigOK()) {
            inv.invoke();
            return;
        }

        AuthorizeResult result = JbootShiroManager.me().invoke(inv.getActionKey());

        if (result == null || result.isOk()) {
            inv.invoke();
            return;
        }

        int errorCode = result.getErrorCode();
        switch (errorCode) {
            case AuthorizeResult.ERROR_CODE_UNAUTHENTICATED:
                doProcessUnauthenticated(inv.getController());
                break;
            case AuthorizeResult.ERROR_CODE_UNAUTHORIZATION:
                doProcessuUnauthorization(inv.getController());
                break;
            default:
                inv.getController().renderError(404);
        }
    }


    /**
     * 未认证处理
     *
     * @param controller
     */
    private void doProcessUnauthenticated(Controller controller) {
        if (StringUtils.isBlank(config.getLoginUrl())) {
            controller.renderError(401);
            return;
        }
        controller.redirect(config.getLoginUrl());
    }


    /**
     * 未授权处理
     *
     * @param controller
     */
    private void doProcessuUnauthorization(Controller controller) {
        if (StringUtils.isBlank(config.getUnauthorizedUrl())) {
            controller.renderError(403);
            return;
        }
        controller.redirect(config.getUnauthorizedUrl());
    }


}
