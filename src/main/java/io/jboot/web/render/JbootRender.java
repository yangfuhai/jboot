/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JbootRender extends Render {

    private static Engine engine;
    private static final String contentType = "text/html; charset=" + getEncoding();
    private static JbootWebCdnConfig cdnConfig = Jboot.config(JbootWebCdnConfig.class);

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }

    public JbootRender(String view) {
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

        if (cdnConfig.isEnable()){
            renderWithCdnConfig(data);
        }else {
            renderNormal(data);
        }
    }

    private void renderWithCdnConfig(Map<Object, Object> data){
        String html = getEngine().getTemplate(view).renderToString(data);
        RenderHelpler.renderHtml(response, RenderHelpler.processCDN(html, cdnConfig.getDomain()) , contentType);
    }

    private void renderNormal(Map<Object, Object> data){
        try {
            OutputStream os = response.getOutputStream();
            engine.getTemplate(view).render(data, os);
            os.flush();
        } catch (RuntimeException e) {	// 捕获 ByteWriter.close() 抛出的 RuntimeException
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {	// ClientAbortException、EofException 直接或间接继承自 IOException
                String name = cause.getClass().getSimpleName();
                if ("ClientAbortException".equals(name) || "EofException".equals(name)) {
                    return ;
                }
            }
            throw e;
        } catch (IOException e) {
            throw new RenderException(e);
        }
    }


    @Override
    public String toString() {
        return view;
    }


}
