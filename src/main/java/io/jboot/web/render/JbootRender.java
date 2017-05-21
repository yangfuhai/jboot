/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
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
import io.jboot.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JbootRender extends Render {


    private static Engine engine;

    private static final String contentType = "text/html; charset=" + getEncoding();

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }

    private JbootRenderConfig config;

    public JbootRender(String view) {
        this.view = view;
        this.config = Jboot.config(JbootRenderConfig.class);
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

        if (!config.isEnableCdn()) {
            renderByEngine(data);
            return;
        }

        String html = getEngine().getTemplate(view).renderToString(data);
        renderHtml(processCDN(html), contentType);
    }

    private void renderByEngine(Map<Object, Object> data) {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            getEngine().getTemplate(view).render(data, writer);
        } catch (Exception e) {
            throw new RenderException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void renderHtml(String htmlContent, String contentType) {
        response.setContentType(contentType);
        PrintWriter responseWriter = null;
        try {
            responseWriter = response.getWriter();
            responseWriter.write(htmlContent);
            responseWriter.flush();
        } catch (Exception e) {
            throw new RenderException(e);
        } finally {
            if (responseWriter != null)
                responseWriter.close();
        }
    }


    private String processCDN(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }


        Document doc = Jsoup.parse(content);

        Elements jsElements = doc.select("script[src]");
        replace(jsElements, "src");

        Elements imgElements = doc.select("img[src]");
        replace(imgElements, "src");

        Elements lazyElements = doc.select("img[data-original]");
        replace(lazyElements, "data-original");

        Elements linkElements = doc.select("link[href]");
        replace(linkElements, "href");

        return doc.toString();

    }

    private void replace(Elements elements, String attrName) {
        String cdnDomain = config.getCdn();
        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()) {

            Element element = iterator.next();

            if (element.hasAttr("cdn-exclude")) {
                continue;
            }

            String url = element.attr(attrName);
            if (StringUtils.isBlank(url) || !url.startsWith("/")) {
                continue;
            }

            url = cdnDomain + url;

            element.attr(attrName, url);
        }
    }

    public String toString() {
        return view;
    }


}
