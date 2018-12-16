/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.render.RenderException;
import io.jboot.Jboot;
import io.jboot.kits.StrUtils;
import io.jboot.web.cache.ActionCacheContent;
import io.jboot.web.cache.ActionCacheContext;
import io.jboot.web.cache.ActionCacheInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.render
 */
public class RenderHelpler {

    public static void actionCacheExec(String html, String contentType) {
        ActionCacheInfo info = ActionCacheContext.get();
        if (info != null) {
            ActionCacheContent actionCache = new ActionCacheContent(contentType, html);
            Jboot.me().getCache().put(info.getGroup(), info.getKey(), actionCache, info.getLiveSeconds());
        }
    }


    public static void renderHtml(HttpServletResponse response, String html, String contentType) {
        response.setContentType(contentType);
        try {
            PrintWriter responseWriter = response.getWriter();
            responseWriter.write(html);
        } catch (Exception e) {
            throw new RenderException(e);
        }
    }


    public static String processCDN(String content) {
        if (StrUtils.isBlank(content)) {
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

    private static void replace(Elements elements, String attrName) {
        String cdnDomain = Jboot.config(JbootRenderConfig.class).getCdn();
        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()) {

            Element element = iterator.next();

            if (element.hasAttr("cdn-exclude")) {
                continue;
            }

            String url = element.attr(attrName);
            if (StrUtils.isBlank(url) || !url.startsWith("/") || url.startsWith("//")) {
                continue;
            }

            url = cdnDomain + url;

            element.attr(attrName, url);
        }
    }
}
