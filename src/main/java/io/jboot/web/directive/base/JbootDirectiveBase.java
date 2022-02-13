/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.template.TemplateException;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

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
            throw new TemplateException(e.getMessage(), location, e);
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

    public String getParaToString(String key, Scope scope) {
        Object object = getPara(key, scope, null);
        if (object == null || object instanceof String) {
            return (String) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : objStr;
    }


    public String getParaToString(String key, Scope scope, String defaultValue) {
        String v = getParaToString(key, scope);
        return v == null ? defaultValue : v;
    }


    public String getParaToString(int index, Scope scope) {
        Object object = getPara(index, scope, null);
        if (object == null || object instanceof String) {
            return (String) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : objStr;
    }


    public String getParaToString(int index, Scope scope, String defaultValue) {
        String v = getParaToString(index, scope);
        return v == null ? defaultValue : v;
    }


    public Integer getParaToInt(String key, Scope scope) {
        Object object = getPara(key, scope, null);
        if (object == null || object instanceof Integer) {
            return (Integer) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : Integer.valueOf(objStr);
    }

    public Integer getParaToInt(String key, Scope scope, Integer defaultValue) {
        Integer v = getParaToInt(key, scope);
        return v == null ? defaultValue : v;
    }

    public Integer getParaToInt(int index, Scope scope) {
        Object object = getPara(index, scope, null);
        if (object == null || object instanceof Integer) {
            return (Integer) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : Integer.valueOf(objStr);
    }

    public Integer getParaToInt(int index, Scope scope, Integer defaultValue) {
        Integer v = getParaToInt(index, scope);
        return v == null ? defaultValue : v;
    }


    public Long getParaToLong(String key, Scope scope) {
        Object object = getPara(key, scope, null);
        if (object == null || object instanceof Long) {
            return (Long) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : Long.valueOf(objStr);
    }

    public Long getParaToLong(String key, Scope scope, Long defaultValue) {
        Long v = getParaToLong(key, scope);
        return v == null ? defaultValue : v;
    }

    public Long getParaToLong(int index, Scope scope) {
        Object object = getPara(index, scope, null);
        if (object == null || object instanceof Long) {
            return (Long) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : Long.valueOf(objStr);
    }

    public Long getParaToLong(int index, Scope scope, Long defaultValue) {
        Long v = getParaToLong(index, scope);
        return v == null ? defaultValue : v;
    }

    public Boolean getParaToBool(String key, Scope scope) {
        Object object = getPara(key, scope, null);
        if (object == null || object instanceof Boolean) {
            return (Boolean) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : Boolean.valueOf(objStr);
    }

    public Boolean getParaToBool(String key, Scope scope, Boolean defaultValue) {
        Boolean v = getParaToBool(key, scope);
        return v == null ? defaultValue : v;
    }

    public Boolean getParaToBool(int index, Scope scope) {
        Object object = getPara(index, scope, null);
        if (object == null || object instanceof Boolean) {
            return (Boolean) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : Boolean.valueOf(objStr);
    }

    public Boolean getParaToBool(int index, Scope scope, Boolean defaultValue) {
        Boolean v = getParaToBool(index, scope);
        return v == null ? defaultValue : v;
    }

    public BigInteger getParaToBigInteger(String key, Scope scope) {
        Object object = getPara(key, scope, null);
        if (object == null || object instanceof BigInteger) {
            return (BigInteger) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : new BigInteger(objStr);
    }

    public BigInteger getParaToBigInteger(String key, Scope scope, BigInteger defaultValue) {
        BigInteger v = getParaToBigInteger(key, scope);
        return v == null ? defaultValue : v;
    }

    public BigInteger getParaToBigInteger(int index, Scope scope) {
        Object object = getPara(index, scope, null);
        if (object == null || object instanceof BigInteger) {
            return (BigInteger) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : new BigInteger(objStr);
    }

    public BigInteger getParaToBigInteger(int index, Scope scope, BigInteger defaultValue) {
        BigInteger v = getParaToBigInteger(index, scope);
        return v == null ? defaultValue : v;
    }


    public BigDecimal getParaToBigDecimal(String key, Scope scope) {
        Object object = getPara(key, scope, null);
        if (object == null || object instanceof BigDecimal) {
            return (BigDecimal) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : new BigDecimal(objStr);
    }

    public BigDecimal getParaToBigDecimal(String key, Scope scope, BigDecimal defaultValue) {
        BigDecimal v = getParaToBigDecimal(key, scope);
        return v == null ? defaultValue : v;
    }

    public BigDecimal getParaToBigDecimal(int index, Scope scope) {
        Object object = getPara(index, scope, null);
        if (object == null || object instanceof BigDecimal) {
            return (BigDecimal) object;
        }
        String objStr = object.toString();
        return StrUtil.isBlank(objStr) ? null : new BigDecimal(objStr);
    }


    public BigDecimal getParaToBigDecimal(int index, Scope scope, BigDecimal defaultValue) {
        BigDecimal v = getParaToBigDecimal(index, scope);
        return v == null ? defaultValue : v;
    }


    public Map getParas(Scope scope) {
        return scope.getData();
    }

    public Map getRootParas(Scope scope) {
        return scope.getRootData();
    }


}
