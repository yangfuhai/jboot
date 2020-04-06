/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.render;

import com.jfinal.core.Action;
import com.jfinal.kit.JsonKit;
import com.jfinal.render.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.render
 */
public class JbootReturnValueRender extends Render {

    private Action action;
    private Object value;
    private Render render;

    public JbootReturnValueRender(Action action, Object returnValue) {

        this.action = action;

        if (returnValue == null) {
            this.value = null;
        } else if (isBaseType(returnValue)) {
            this.value = String.valueOf(returnValue);
        } else {
            this.value = returnValue;
        }

        if (this.value == null) {
            this.render = new NullRender();
        } else if (this.value instanceof File) {
            this.render = new FileRender((File) value);
        } else if (this.value instanceof String) {
            this.render = new TextRender((String) value);
        } else if (this.value instanceof Date) {
            this.render = new TextRender(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value));
        } else {
            this.render = new JsonRender(JsonKit.toJson(value));
        }
    }


    @Override
    public Render setContext(HttpServletRequest request, HttpServletResponse response) {
        render.setContext(request, response);
        return this;
    }

    @Override
    public Render setContext(HttpServletRequest request, HttpServletResponse response, String viewPath) {
        render.setContext(request, response, viewPath);
        return this;
    }

    @Override
    public void render() {
        this.render.render();
    }


    private boolean isBaseType(Object value) {
        Class c = value.getClass();
        return c == String.class || c == char.class
                || c == Integer.class || c == int.class
                || c == Long.class || c == long.class
                || c == Double.class || c == double.class
                || c == Float.class || c == float.class
                || c == Boolean.class || c == boolean.class
                || c == Short.class || c == short.class
                || c == BigDecimal.class || c == BigInteger.class;
    }
}
