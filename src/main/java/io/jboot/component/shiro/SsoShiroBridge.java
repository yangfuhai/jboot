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

import com.jfinal.core.Controller;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: SSO 和 shiro 链接的桥梁
 * @Description: 通过 SSO 获取用户信息，再通过用户信息构建 shiro 的 subject
 */
public interface SsoShiroBridge {

    /**
     * Sbuject 认证
     * @param controller
     * @return
     */
    public void subjectLogin(Controller controller);

    /**
     * 是否 sso 回调请求
     * @param controller
     * @return
     */
    public boolean isSsoCallBackRequest(Controller controller);
}
