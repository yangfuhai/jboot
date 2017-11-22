/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.component.swagger;

import com.google.common.collect.Maps;
import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.component.swagger
 */
public class JbootSwaggerController extends JbootController {

    JbootSwaggerConfig config = Jboot.config(JbootSwaggerConfig.class);

    public void index() {
        String html = null;
        try {
            html = renderToString("index.html", Maps.newHashMap());
        } catch (Throwable ex) {
        }

        if (html == null) {
            renderHtml("error，please put  <a href=\"https://github.com/swagger-api/swagger-ui\" target=\"_blank\">swagger-ui</a> into your project path :  " + config.getPath() + " <br />" +
                    "or click <a href=\"" + config.getPath() + "/json\">here</a>  show swagger json.");
            return;
        }

        html = html.replace("http://petstore.swagger.io/v2/swagger.json", getRequest().getRequestURL() + "/json");
        html = html.replace("src=\"./", "src=\"" + config.getPath() + "/");
        html = html.replace("href=\"./", "href=\"" + config.getPath() + "/");

        renderHtml(html);
    }

    /**
     * 渲染json
     * 参考：http://petstore.swagger.io/ 及json信息 http://petstore.swagger.io/v2/swagger.json
     */
    public void json() {

        HttpServletResponse response = getResponse();
        response.setHeader("Access-Control-Allow-Origin", "*"); //解决跨域访问报错
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600"); //设置过期时间
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, client_id, uuid, Authorization");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // 支持HTTP 1.1.
        response.setHeader("Pragma", "no-cache");

        Swagger swagger = SwaggerManager.me().getSwagger();
        if (swagger == null) {
            renderText("swagger config error.");
            return;
        }

        renderJson(swagger);
    }

}
