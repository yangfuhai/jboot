package swagger;

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


    @SwaggerAPI(description = "测试description描述", summary = "测试summary", params = {@SwaggerParam(name = "账号", in = "name", description = "请输入账号名称")})
    public void index() {
        renderText("index");
    }
}
