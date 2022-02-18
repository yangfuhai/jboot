/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.ArrayUtil;
import io.jboot.web.directive.annotation.JFinalDirective;


/**
 * 没有该角色
 * #shiroNotHasRole(roleName)
 * body
 * #end
 */
@JFinalDirective("shiroNotHasRole")
public class ShiroNotHasRoleDirective extends JbootShiroDirectiveBase {

    @Override
    public void setExprList(ExprList exprList) {
        if (exprList.getExprArray().length == 0) {
            throw new IllegalArgumentException("#shiroNotHasRole argument must not be empty");
        }
        super.setExprList(exprList);
    }

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        boolean hasAnyRole = false;
        if (getSubject() != null && ArrayUtil.isNotEmpty(exprList.getExprArray())) {
            for (Expr expr : exprList.getExprArray()) {
                if (getSubject().hasRole(expr.eval(scope).toString())) {
                    hasAnyRole = true;
                    break;
                }
            }
        }
        if (!hasAnyRole) {
            renderBody(env, scope, writer);
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }


}