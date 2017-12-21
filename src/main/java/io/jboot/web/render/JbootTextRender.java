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
import com.jfinal.render.RenderException;
import com.jfinal.render.TextRender;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.render
 */
public class JbootTextRender extends TextRender {

    // 与 encoding 与 contentType 在 render() 方法中分开设置，效果相同
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";

    private String text;
    private String contentType;

    public JbootTextRender(String text) {
        super(text);
        this.text = text;
        this.contentType = DEFAULT_CONTENT_TYPE;
    }

    public JbootTextRender(String text, String contentType) {
        super(text, contentType);
        this.text = text;
        this.contentType = contentType;
    }

    public JbootTextRender(String text, ContentType contentType) {
        super(text, contentType);
        this.text = text;
        this.contentType = contentType.value();
    }

    public void render() {
        PrintWriter writer = null;
        try {

            RenderHelpler.actionCacheExec(text, contentType);

            response.setHeader("Pragma", "no-cache");    // HTTP/1.0 caches might not implement Cache-Control and might only implement Pragma: no-cache
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            response.setContentType(contentType);
            response.setCharacterEncoding(getEncoding());    // 与 contentType 分开设置

            writer = response.getWriter();
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        }
    }

    public String getText() {
        return text;
    }

    public String getContentType() {
        return contentType;
    }
}
