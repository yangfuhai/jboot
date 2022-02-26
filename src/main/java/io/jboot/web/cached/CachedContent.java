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
package io.jboot.web.cached;

import com.jfinal.render.Render;
import com.jfinal.render.TextRender;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CachedContent implements Serializable {

    private String content;
    private String contentType;
    private Map<String, String> headers;

    public CachedContent() {
    }

    public static CachedContent fromResponseProxy(CacheSupportResponseProxy responseProxy) {
        CachedContent cachedContent = new CachedContent();
        cachedContent.setContentType(responseProxy.getContentType());
        cachedContent.setContent(responseProxy.getResponseString());

        Collection<String> headerNames = responseProxy.getHeaderNames();
        if (headerNames != null) {
            headerNames.forEach(s -> cachedContent.addHeader(s, responseProxy.getHeader(s)));
        }
        return cachedContent;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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
        return new TextRender(getContent(), getContentType());
    }
}
