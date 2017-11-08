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
package io.jboot.component.swagger;

import com.google.common.collect.Maps;
import com.jfinal.render.RenderManager;
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
        if (!getRequest().getRequestURI().endsWith("/")) {
            redirect(config.getPath() + "/");
            return;
        }

        String html = RenderManager.me().getEngine().getTemplate(config.getPath() + "/index.html").renderToString(Maps.newHashMap());
        html = html.replace("http://petstore.swagger.io/v2/swagger.json", config.getPath() + "/json");
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
