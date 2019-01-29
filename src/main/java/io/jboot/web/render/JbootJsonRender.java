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
package io.jboot.web.render;

import com.jfinal.kit.JsonKit;
import com.jfinal.render.JsonRender;
import com.jfinal.render.RenderException;
import io.jboot.JbootConsts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.render
 */
public class JbootJsonRender extends JsonRender {

    private static final String contentType = "application/json; charset=" + getEncoding();
    private static final String contentTypeForIE = "text/html; charset=" + getEncoding();

    private static final Set<String> excludedAttrs = new HashSet<String>() {
        private static final long serialVersionUID = 9186138395157680676L;

        {
            add("javax.servlet.request.ssl_session");
            add("javax.servlet.request.ssl_session_id");
            add("javax.servlet.request.ssl_session_mgr");
            add("javax.servlet.request.key_size");
            add("javax.servlet.request.cipher_suite");
            add("_res");    // I18nInterceptor 中使用的 _res
            add(JbootConsts.ATTR_REQUEST);
            add(JbootConsts.ATTR_CONTEXT_PATH);
        }
    };

    public JbootJsonRender() {

    }

    public JbootJsonRender(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("The parameter key can not be null.");
        }
        this.jsonText = JsonKit.toJson(new HashMap<String, Object>() {{
            put(key, value);
        }});
    }

    public JbootJsonRender(String[] attrs) {
        this.attrs = attrs;
    }

    public JbootJsonRender(String jsonText) {
        this.jsonText = jsonText;
    }

    public JbootJsonRender(Object object) {
        this.jsonText = JsonKit.toJson(object);
    }

    private boolean forIE = false;

    public JsonRender forIE() {
        forIE = true;
        return this;
    }

    private String jsonText;
    private String[] attrs;

    public void render() {

        if (jsonText == null) {
            buildJsonText();
        }

        RenderHelpler.actionCacheExec(jsonText, forIE ? contentTypeForIE : contentType);

        try {
            response.setHeader("Pragma", "no-cache");    // HTTP/1.0 caches might not implement Cache-Control and might only implement Pragma: no-cache
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            response.setContentType(forIE ? contentTypeForIE : contentType);
            PrintWriter writer = response.getWriter();
            writer.write(jsonText);
        } catch (IOException e) {
            throw new RenderException(e);
        }
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void buildJsonText() {
        Map map = new HashMap();
        if (attrs != null) {
            for (String key : attrs) {
                map.put(key, request.getAttribute(key));
            }
        } else {
            for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements(); ) {
                String key = attrs.nextElement();
                if (excludedAttrs.contains(key)) {
                    continue;
                }

                Object value = request.getAttribute(key);
                map.put(key, value);
            }
        }

        this.jsonText = JsonKit.toJson(map);
    }

    @Override
    public String getJsonText() {
        return jsonText;
    }
}
