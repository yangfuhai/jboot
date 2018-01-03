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
package io.jboot.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;


/**
 * 用户已经身份验证通过，Subject.login登录成功
 * #shiroAuthenticated()
 * body
 * #end
 */
@JFinalDirective("shiroAuthenticated")
public class ShiroAuthenticatedDirective extends JbootShiroDirectiveBase {

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        
        if (getSubject() != null && getSubject().isAuthenticated()) {
            renderBody(env, scope, writer);
        }

    }


    public boolean hasEnd() {
        return true;
    }
}
