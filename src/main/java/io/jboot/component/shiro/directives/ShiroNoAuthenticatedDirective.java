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
package io.jboot.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;

import java.io.Writer;

/**
 * 未进行身份验证时，即没有调用Subject.login进行登录。
 * 注意：记住我 自动登录属于未进行身份认证。
 * #shiroNoAuthenticated()
 * body
 * #end
 */
@JFinalDirective("shiroNoAuthenticated")
public class ShiroNoAuthenticatedDirective extends JbootShiroDirectiveBase {

    public void exec(Env env, Scope scope, Writer writer) {
        if (getSubject() != null && !getSubject().isAuthenticated())
            stat.exec(env, scope, writer);
    }

    public boolean hasEnd() {
        return true;
    }
}
