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
package io.jboot.web.directive.base;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.Jboot;

/**
 * Jfinal 指令的基类
 */
public abstract class JbootDirectiveBase extends Directive {


    public JbootDirectiveBase() {
        Jboot.injectMembers(this);
    }


    @Override
    public void setExprList(ExprList exprList) {
        super.setExprList(exprList);
    }


    @Override
    public void exec(Env env, Scope scope, Writer writer) {
        scope = new Scope(scope);
        scope.getCtrl().setLocalAssignment();
        exprList.eval(scope);

        onRender(env, scope, writer);

    }


    public abstract void onRender(Env env, Scope scope, Writer writer);


    public <T> T getParam(String key, T defaultValue, Scope scope) {
        Object data = scope.getLocal(key);
        return (T) (data == null ? defaultValue : data);
    }


    public <T> T getParam(String key, Scope scope) {
        return getParam(key, null, scope);
    }


    public <T> T getParam(int index, T defaultValue, Scope scope) {
        if (index < 0 || index >= exprList.length()) {
            return defaultValue;
        }
        Object data = exprList.getExpr(index).eval(scope);
        return (T) (data == null ? defaultValue : data);
    }


    public <T> T getParam(int index, Scope scope) {
        return getParam(index, null, scope);
    }


    public void renderBody(Env env, Scope scope, Writer writer) {
        stat.exec(env, scope, writer);
    }


}
