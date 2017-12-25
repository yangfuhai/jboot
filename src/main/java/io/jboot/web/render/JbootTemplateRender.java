/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.render.TemplateRender;
import com.jfinal.template.Engine;
import io.jboot.Jboot;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.render
 */
public class JbootTemplateRender extends TemplateRender {

    private JbootRenderConfig config;


    public JbootTemplateRender(String view) {
        super(view);
        this.config = Jboot.config(JbootRenderConfig.class);
    }

    private static Engine engine;

    private static final String contentType = "text/html; charset=" + getEncoding();

    static void init(Engine engine) {
        if (engine == null) {
            throw new IllegalArgumentException("engine can not be null");
        }
        JbootTemplateRender.engine = engine;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public void render() {
        response.setContentType(getContentType());

        Map<Object, Object> data = new HashMap<Object, Object>();
        for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements(); ) {
            String attrName = attrs.nextElement();
            data.put(attrName, request.getAttribute(attrName));
        }

        String html = engine.getTemplate(view).renderToString(data);
        html = config.isEnableCdn() ? RenderHelpler.processCDN(html) : html;
        RenderHelpler.actionCacheExec(html, contentType);
        RenderHelpler.renderHtml(response, html, contentType);
    }


    public String toString() {
        return view;
    }
}
