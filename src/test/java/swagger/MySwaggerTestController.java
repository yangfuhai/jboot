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
package swagger;

import com.jfinal.kit.Ret;
import io.jboot.component.swagger.ParamType;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package swagger
 */
@RequestMapping("/swaggerTest")
@Api(description = "用户相关接口文档", basePath = "/swaggerTest", tags = "abc")
public class MySwaggerTestController extends JbootController {

    @ApiOperation(value = "用户列表", httpMethod = "GET", notes = "user list")
    public void index() {
        renderJson(Ret.ok("k1", "v1").set("name", getPara("name")));
    }


    @ApiOperation(value = "添加用户", httpMethod = "POST", notes = "add user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = ParamType.FORM, dataType = "string", required = true),
            @ApiImplicitParam(name = "k1", value = "k1", paramType = ParamType.FORM, dataType = "string", required = true),
    })
    public void add(String username) {
        renderJson(Ret.ok("k1", "v1").set("username", username));
    }


}
