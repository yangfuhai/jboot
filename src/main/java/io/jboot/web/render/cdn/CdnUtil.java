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
package io.jboot.web.render.cdn;

import com.jfinal.core.JFinal;
import io.jboot.Jboot;
import io.jboot.utils.StrUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class CdnUtil {

    private static String charSet = JFinal.me().getConstants().getEncoding();
    private static JbootWebCdnConfig cdnConfig = Jboot.config(JbootWebCdnConfig.class);

    public static String appendCdnDomain(String path) {
        if (StrUtil.isBlank(path)) {
            return path;
        }

        if (cdnConfig.isEnable() && StrUtil.isNotBlank(cdnConfig.getDomain())) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return cdnConfig.getDomain() + path;
        }

        return path;
    }


    public static String toHtml(InputStream content, String domain) throws IOException {
        Document doc = Jsoup.parse(content, charSet, "");

        Elements jsElements = doc.select("script[src]");
        replace(jsElements, "src", domain);

        Elements imgElements = doc.select("img[src]");
        replace(imgElements, "src", domain);

        Elements linkElements = doc.select("link");
        replace(linkElements, "href", domain);

        return doc.toString();

    }

    private static void replace(Elements elements, String attrName, String domain) {
        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()) {

            Element element = iterator.next();

            if (element.hasAttr("cdn-exclude")) {
                continue;
            }

            String url = element.attr(attrName);
            if (StrUtil.isBlank(url) || !url.startsWith("/") || url.startsWith("//")) {
                continue;
            }

            url = domain + url;

            element.attr(attrName, url);
        }
    }
}
