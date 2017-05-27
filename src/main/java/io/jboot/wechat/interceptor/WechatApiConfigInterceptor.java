/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.wechat.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import io.jboot.wechat.controller.JbootWechatController;

public class WechatApiConfigInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        try {
            JbootWechatController controller = (JbootWechatController) inv.getController();
            ApiConfig config = controller.getApiConfig();

            if (config == null) {
                inv.getController().renderText("error : cannot get apiconfig,please config jboot.properties");
                return;
            }

            ApiConfigKit.setThreadLocalAppId(config.getAppId());
            inv.invoke();
        } finally {
            ApiConfigKit.removeThreadLocalAppId();
        }
    }

}
