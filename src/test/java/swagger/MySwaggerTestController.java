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
package swagger;

import com.jfinal.kit.Ret;
import io.jboot.component.swagger.annotation.SwaggerAPI;
import io.jboot.component.swagger.annotation.SwaggerAPIs;
import io.jboot.component.swagger.annotation.SwaggerParam;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package swagger
 */
@SwaggerAPIs(name = "测试接口", description = "这个接口集合的描述")
@RequestMapping("/swaggerTest")
public class MySwaggerTestController extends JbootController {


    @SwaggerAPI(description = "测试description描述", summary = "测试summary",
            params = {@SwaggerParam(name = "账号", in = "name", description = "请输入账号名称")}
    )
    public void index() {
        renderJson(Ret.ok("k1", "v1"));
    }


    @SwaggerAPI(description = "进行用户登录操作", summary = "用户登录API",
            params = {
                    @SwaggerParam(name = "账号", in = "name", description = "请输入账号名称"),
                    @SwaggerParam(name = "密码", in = "password", description = "请输入密码", definition = "MySwaggerPeople")
            }
    )
    public void login() {
        renderJson(Ret.ok("k2", "vv"));
    }
}
