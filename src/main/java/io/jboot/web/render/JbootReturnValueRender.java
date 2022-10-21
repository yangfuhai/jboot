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
package io.jboot.web.render;

import com.jfinal.kit.JsonKit;
import com.jfinal.render.IRenderFactory;
import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;
import io.jboot.utils.DateUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JbootReturnValueRender extends Render {

    protected IRenderFactory factory = RenderManager.me().getRenderFactory();
    protected Object value;
    protected Render render;

    protected String forwardTo;

    public JbootReturnValueRender(Object returnValue) {

        if (returnValue == null) {
            this.value = null;
        } else if (isBaseType(returnValue)) {
            this.value = String.valueOf(returnValue);
        } else {
            this.value = returnValue;
        }

        initRealRender();
    }


    protected void initRealRender() {
        if (this.value == null) {
            this.render = factory.getNullRender();
        } else if (this.value instanceof ResponseEntity) {
            this.render = new JbootResponseEntityRender((ResponseEntity) value);
        } else if (this.value instanceof String) {
            String newVal = ((String) value).toLowerCase();

            //template
            if (newVal.endsWith(".html") && !newVal.contains(":")) {
                this.render = factory.getTemplateRender((String) value);
            }

            //error
            else if (newVal.startsWith("error") && newVal.length() > 8) {
                String trim = ((String) value).substring(5).trim();
                if (trim.startsWith(":")) {
                    String errorCodeStr = trim.substring(1).trim();
                    if (StrUtil.isNumeric(errorCodeStr)) {
                        this.render = factory.getErrorRender(Integer.parseInt(errorCodeStr));
                    }
                }
                if (this.render == null) {
                    this.render = factory.getTextRender((String) value);
                }
            }

            //forward
            else if (newVal.startsWith("forward")) {
                String trim = ((String) value).substring(7).trim();
                if (trim.startsWith(":")) {
                    this.forwardTo = trim.substring(1).trim();
                } else {
                    this.render = factory.getTextRender((String) value);
                }
            }

            //redirect
            else if (newVal.startsWith("redirect")) {
                String trim = ((String) value).substring(8).trim();
                if (trim.startsWith(":")) {
                    String redirectTo = trim.substring(1).trim();
                    if (StrUtil.isNotBlank(redirectTo)) {
                        this.render = factory.getRedirectRender(redirectTo);
                    }
                }
                if (this.render == null) {
                    this.render = factory.getTextRender((String) value);
                }
            }

            //text
            else {
                this.render = factory.getTextRender((String) value);
            }
        } else if (this.value instanceof Date) {
            this.render = factory.getTextRender(DateUtil.toDateTimeString((Date) value));
        } else if (this.value instanceof File) {
            this.render = factory.getFileRender((File) value);
        } else if (this.value instanceof Render) {
            this.render = (Render) value;
        } else {
            this.render = factory.getJsonRender(JsonKit.toJson(value));
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


    protected boolean isBaseType(Object value) {
        Class<?> c = value.getClass();
        return c == String.class || c == char.class
                || c == Integer.class || c == int.class
                || c == Long.class || c == long.class
                || c == Double.class || c == double.class
                || c == Float.class || c == float.class
                || c == Boolean.class || c == boolean.class
                || c == Short.class || c == short.class
                || c == BigDecimal.class || c == BigInteger.class;
    }

    public Render getRealRender(){
        return render;
    }

    public String getForwardTo() {
        return forwardTo;
    }
}
