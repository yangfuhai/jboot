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
package io.jboot.components.cache.interceptor;

import com.jfinal.render.*;
import io.jboot.web.render.JbootRender;
import io.jboot.web.render.JbootTemplateRender;
import io.jboot.web.render.JbootXmlRender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActionCachedContent implements Serializable {

    private static final int RENDER_DEFAULT = 0;
    private static final int RENDER_TEMPLATE = 1;
    private static final int RENDER_XML = 2;
    private static final int RENDER_JSON = 3;
    private static final int RENDER_TEXT = 4;

    private static IRenderFactory renderFactory = RenderManager.me().getRenderFactory();
    private static final Set<String> ignoreAttrs = new HashSet<>();


    private Map<String, String> headers;
    private Map<String, Object> attrs;

    private String contentType;
    private Integer renderType;
    private String viewOrText;
    private Map<String, Object> otherPara = null;

    public static Set<String> getIgnoreAttrs() {
        return ignoreAttrs;
    }

    public static void addIgnoreAttr(String attrName) {
        ignoreAttrs.add(attrName);
    }

    public ActionCachedContent(Render render) {
        if (render == null) {
            throw new IllegalArgumentException("Render can not be null.");
        }

        // xml
        if (render instanceof JbootXmlRender) {
            renderType = RENDER_XML;
            contentType = ((JbootXmlRender) render).getContentType();
            viewOrText = render.getView();
        }
        // template
        else if (render instanceof JbootTemplateRender) {
            renderType = RENDER_TEMPLATE;
            contentType = ((JbootTemplateRender) render).getContentType();
            viewOrText = render.getView();
        }
        // default
        else if (render instanceof JbootRender) {
            renderType = RENDER_DEFAULT;
            contentType = ((JbootRender) render).getContentType();
            viewOrText = render.getView();
        }
        // text
        else if (render instanceof TextRender) {
            renderType = RENDER_TEXT;
            contentType = ((TextRender) render).getContentType();
            viewOrText = ((TextRender) render).getText();
        }
        // json
        else if (render instanceof JsonRender) {
            renderType = RENDER_JSON;
            otherPara = new HashMap<>();

            JsonRender jsonRender = (JsonRender) render;
            otherPara.put("jsonText", jsonRender.getJsonText());
            otherPara.put("attrs", jsonRender.getAttrs());
            otherPara.put("forIE", jsonRender.getForIE());
        } else {
            throw new IllegalArgumentException("@Cacheable Can not support the render of the type: " + render.getClass().getName());
        }
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
        if (this.attrs != null) {
            for (String ignoreAttr : ignoreAttrs) {
                this.attrs.remove(ignoreAttr);
            }
        }
    }

    public void addAttr(String key, Object value) {
        if (ignoreAttrs.contains(key)) {
            return;
        }
        if (this.attrs == null) {
            this.attrs = new HashMap<>();
        }
        this.attrs.put(key, value);
    }

    public String getViewOrText() {
        return viewOrText;
    }

    public void setViewOrText(String viewOrText) {
        this.viewOrText = viewOrText;
    }

    public Integer getRenderType() {
        return renderType;
    }

    public void setRenderType(Integer renderType) {
        this.renderType = renderType;
    }

    public Map<String, Object> getOtherPara() {
        return otherPara;
    }

    public void setOtherPara(Map<String, Object> otherPara) {
        this.otherPara = otherPara;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
    }


    public Render createRender() {
        switch (renderType) {
            case RENDER_DEFAULT:
                return renderFactory.getRender(viewOrText);
            case RENDER_TEMPLATE:
                return renderFactory.getTemplateRender(viewOrText);
            case RENDER_JSON:
                JsonRender jsonRender;
                if (otherPara.get("jsonText") != null) {
                    jsonRender = (JsonRender) renderFactory.getJsonRender((String) otherPara.get("jsonText"));
                } else if (otherPara.get("attrs") != null) {
                    jsonRender = (JsonRender) renderFactory.getJsonRender((String[]) otherPara.get("attrs"));
                } else {
                    jsonRender = (JsonRender) renderFactory.getJsonRender();
                }
                if (Boolean.TRUE.equals(otherPara.get("forIE"))) {
                    jsonRender.forIE();
                }
                return jsonRender;
            case RENDER_TEXT:
                return renderFactory.getTextRender(viewOrText, contentType);
            case RENDER_XML:
                return renderFactory.getXmlRender(viewOrText);
            default:
                throw new IllegalStateException("@Cacheable can not support the renderType of the value: " + renderType);
        }
    }
}
