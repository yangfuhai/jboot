/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.aop.Aop;
import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Jfinal 指令的基类
 */
public abstract class JbootDirectiveBase extends Directive {


    public JbootDirectiveBase() {
        Aop.inject(this);
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


    public void renderBody(Env env, Scope scope, Writer writer) {
        stat.exec(env, scope, writer);
    }

    public void renderText(Writer writer, String text) {
        try {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T getPara(String key, Scope scope) {
        return getPara(key, scope, null);
    }

    public <T> T getPara(String key, Scope scope, T defaultValue) {
        Object data = scope.getLocal(key);
        return (T) (data == null ? defaultValue : data);
    }

    public <T> T getPara(int index, Scope scope) {
        return getPara(index, scope, null);
    }

    public <T> T getPara(int index, Scope scope, T defaultValue) {
        if (index < 0 || index >= exprList.length()) {
            return defaultValue;
        }
        Object data = exprList.getExpr(index).eval(scope);
        return (T) (data == null ? defaultValue : data);
    }

    public Integer getParaToInt(int index, Scope scope) {
        Object object = getPara(index, scope, null);
        if (object == null || object instanceof Integer) return (Integer) object;
        return Integer.valueOf(object.toString());
    }

    public Integer getParaToInt(int index, Scope scope, Integer defaultValue) {
        Integer v = getParaToInt(index, scope);
        return v == null ? defaultValue : v;
    }


    public Long getParaToLang(int index, Scope scope) {
        Object object = getPara(index, scope, null);
        if (object == null || object instanceof Long) return (Long) object;
        return Long.valueOf(object.toString());
    }

    public Long getParaToLang(int index, Scope scope, Long defaultValue) {
        Long v = getParaToLang(index, scope);
        return v == null ? defaultValue : v;
    }


    public BigInteger getParaToBigInteger(int index, Scope scope) {
        Object object = getPara(index, scope, null);
        if (object == null || object instanceof BigInteger) return (BigInteger) object;
        return new BigInteger(object.toString());
    }


    public BigInteger getParaToBigInteger(int index, Scope scope, BigInteger defaultValue) {
        BigInteger v = getParaToBigInteger(index, scope);
        return v == null ? defaultValue : v;
    }


}
