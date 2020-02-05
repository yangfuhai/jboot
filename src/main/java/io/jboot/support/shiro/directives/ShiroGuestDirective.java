/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;


/**
 * 游客访问时。
 * 但是，当用户登录成功了就不显示了
 * #shiroGuest()
 * body
 * #end
 */
@JFinalDirective("shiroGuest")
public class ShiroGuestDirective extends JbootShiroDirectiveBase {

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        if (getSubject() == null || getSubject().getPrincipal() == null) {
            renderBody(env, scope, writer);
        }

    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
