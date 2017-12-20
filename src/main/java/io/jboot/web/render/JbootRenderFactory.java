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

import com.jfinal.render.ContentType;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;

public class JbootRenderFactory extends RenderFactory {

    private static final JbootRenderFactory ME = new JbootRenderFactory();

    public static final JbootRenderFactory me() {
        return ME;
    }


    @Override
    public Render getRender(String view) {
        return new JbootRender(view);
    }

    @Override
    public Render getHtmlRender(String htmlText) {
        return new JbootHtmlRender(htmlText);
    }

    @Override
    public Render getTextRender(String text) {
        return new JbootTextRender(text);
    }

    @Override
    public Render getTextRender(String text, String contentType) {
        return new JbootTextRender(text, contentType);
    }

    @Override
    public Render getTextRender(String text, ContentType contentType) {
        return new JbootTextRender(text, contentType);
    }

    @Override
    public Render getJavascriptRender(String jsText) {
        return new JbootJavascriptRender(jsText);
    }

    @Override
    public Render getErrorRender(int errorCode) {
        return new JbootErrorRender(errorCode);
    }

    @Override
    public Render getJsonRender() {
        return new JbootJsonRender();
    }

    @Override
    public Render getJsonRender(String key, Object value) {
        return new JbootJsonRender(key, value);
    }

    @Override
    public Render getJsonRender(String[] attrs) {
        return new JbootJsonRender(attrs);
    }

    @Override
    public Render getJsonRender(String jsonText) {
        return new JbootJsonRender(jsonText);
    }

    @Override
    public Render getJsonRender(Object object) {
        return new JbootJsonRender(object);
    }
}
