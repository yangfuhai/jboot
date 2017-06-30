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
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.ArrayUtils;
import io.jboot.web.directive.annotation.JFinalDirective;

import java.io.Writer;

/**
 * 没有该权限
 * #shiroNotHasPermission(permissionName)
 * body
 * #end
 */
@JFinalDirective("shiroNotHasPermission")
public class ShiroNotHasPermissionDirective extends JbootShiroDirectiveBase {
    private Expr[] exprs;

    public void setExprList(ExprList exprList) {
        exprs = exprList.getExprArray();
    }

    public void exec(Env env, Scope scope, Writer writer) {
        if (getSubject() != null && ArrayUtils.isNotEmpty(exprs))
            if (!getSubject().isPermitted(exprs[0].toString()))
                stat.exec(env, scope, writer);
    }

    public boolean hasEnd() {
        return true;
    }
}
