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
package io.jboot.support.swagger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.common.collect.Maps;
import com.jfinal.core.JFinal;
import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;
import io.jboot.web.cors.EnableCORS;
import io.swagger.models.Swagger;
import io.swagger.models.properties.RefProperty;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.component.swagger
 */
public class JbootSwaggerController extends JbootController {

    JbootSwaggerConfig config = Jboot.config(JbootSwaggerConfig.class);

    public void index() {
        String html;
        try {
            String viewPath = config.getPath().endsWith("/") ? config.getPath() : config.getPath() + "/";
            html = renderToString(viewPath + "index.html", Maps.newHashMap());
        } catch (Exception ex) {
            renderHtml("error，please put  <a href=\"https://github.com/swagger-api/swagger-ui\" target=\"_blank\">swagger-ui</a> " +
                    "into your project path :  " + config.getPath() + " <br />" +
                    "or click <a href=\"" + config.getPath() + "/json\">here</a>  show swagger json.");
            return;
        }


        String jsonUrl = getRequest().getRequestURL().toString() + "/json";
        String basePath = JFinal.me().getContextPath() + "/" + config.getPath() + "/";

        html = html.replace("http://petstore.swagger.io/v2/swagger.json", jsonUrl);
        html = html.replace("https://petstore.swagger.io/v2/swagger.json", jsonUrl); // 可能是 https ，看下载的 swagger 版本
        html = html.replace("src=\"./", "src=\"" + basePath);
        html = html.replace("href=\"./", "href=\"" + basePath);

        renderHtml(html);
    }

    /**
     * 渲染json
     * 参考：http://petstore.swagger.io/ 及json信息 http://petstore.swagger.io/v2/swagger.json
     */
    @EnableCORS
    public void json() {
        Swagger swagger = JbootSwaggerManager.me().getSwagger();
        if (swagger == null) {
            renderText("swagger config error.");
            return;
        }

        // 适配swaggerUI, 解决页面"Unknown Type : ref"问题。
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(RefProperty.class, new RefPropertySerializer());
        renderJson(JSON.toJSONString(swagger, serializeConfig));
    }

}
