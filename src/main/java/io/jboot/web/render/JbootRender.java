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

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import io.jboot.Jboot;
import io.jboot.utils.StrUtil;
import io.jboot.web.render.cdn.JbootWebCdnConfig;
import io.jboot.ext.MixedByteArrayOutputStream;
import io.jboot.web.render.cdn.CdnUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JbootRender extends Render {

    private static Engine engine;
    private static String contentType = "text/html; charset=" + getEncoding();
    private static JbootWebCdnConfig cdnConfig = Jboot.config(JbootWebCdnConfig.class);

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }

    public JbootRender(String view) {
        if (StrUtil.isBlank(view)){
            throw new IllegalArgumentException("view cannot be null or empty.");
        }
        this.view = view;
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

        try {
            if (cdnConfig.isEnable()) {
                renderWithCdn(data);
            } else {
                getEngine().getTemplate(view).render(data, response.getWriter());
            }
        } catch (RuntimeException e) {    // 捕获 ByteWriter.close() 抛出的 RuntimeException
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {    // ClientAbortException、EofException 直接或间接继承自 IOException
                String name = cause.getClass().getSimpleName();
                if ("ClientAbortException".equals(name) || "EofException".equals(name)) {
                    return;
                }
            }
            throw e;
        } catch (IOException e) {
            throw new RenderException(e);
        }
    }

    private void renderWithCdn(Map<Object, Object> data) throws IOException {
        MixedByteArrayOutputStream baos = new MixedByteArrayOutputStream();
        getEngine().getTemplate(view).render(data, baos);

        PrintWriter responseWriter = response.getWriter();
        responseWriter.write(CdnUtil.toHtml(baos.getInputStream(), cdnConfig.getDomain()));
    }


    @Override
    public String toString() {
        return view;
    }


}
