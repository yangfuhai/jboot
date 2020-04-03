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
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.render
 */
public class JbootReturnValueRender extends Render {

    private Action action;
    private Object value;

    public JbootReturnValueRender(Action action, Object returnValue) {
        this.action = action;
        this.value = returnValue;
    }

    @Override
    public void render() {
        if (isBaseType()) {
            renderText();
        } else {
            renderJson();
        }
    }


    private void renderText() {
        PrintWriter writer = null;
        try {
            response.setContentType("text/plain");
            writer = response.getWriter();
            writer.write(String.valueOf(value));
            // writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        }
    }


    private void renderJson() {
        PrintWriter writer = null;
        try {
            response.setContentType("application/json; charset=utf-8");
            writer = response.getWriter();
            writer.write(JsonKit.toJson(value));
            // writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        }
    }

    private boolean isBaseType() {
        if (value == null) {
            return true;
        }
        Class c = value.getClass();
        return c == String.class
                || c == Integer.class || c == int.class
                || c == Long.class || c == long.class
                || c == Double.class || c == double.class
                || c == Float.class || c == float.class
                || c == Boolean.class || c == boolean.class
                || c == BigDecimal.class || c == BigInteger.class;
    }
}
