# Jboot 与 Swagger

Jboot 已经整合了对 Swagger 的支持，但是默认情况下并未依赖 Swagger 的相关依赖，因此，在使用 Swagger 之前需要添加如下的依赖：

```xml
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-core</artifactId>
    <version>1.5.21</version>
</dependency>
```

由于 Swagger 运行的时候，需要 html 页面来显示，因此还需要去 Swagger 的官方网站把对应的 html 资源下载下来，并放入
`webapp/swaggerui` 目录下。

swagger ui 的下载地址是：https://github.com/swagger-api/swagger-ui ,下载其 dist 目录即可，只需要这个目录里的文件。


接下来需要添加 Swagger 的相关配置，例如：

```java
JbootApplication.setBootArg("jboot.swagger.path", "/swaggerui");
JbootApplication.setBootArg("jboot.swagger.title", "Jboot API 测试");
JbootApplication.setBootArg("jboot.swagger.description", "这是一个Jboot对Swagger支持的测试demo。");
JbootApplication.setBootArg("jboot.swagger.version", "1.0");
JbootApplication.setBootArg("jboot.swagger.termsOfService", "http://jboot.io");
JbootApplication.setBootArg("jboot.swagger.contactEmail", "fuhai999@gmail.com");
JbootApplication.setBootArg("jboot.swagger.contactName", "fuhai999");
JbootApplication.setBootArg("jboot.swagger.contactUrl", "http://jboot.io");
JbootApplication.setBootArg("jboot.swagger.host", "127.0.0.1:8080");
```

这部分的配置可以写入到 jboot.properties 文件里，此时，我们在 Controller 里添加 Swagger 的相关注解，例如：

```java
@RequestMapping("/swagger")
@Api(description = "用户相关接口文档", basePath = "/swagger", tags = "abc")
public class MySwaggerController extends JbootController {

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
```

运行 Jboot 应用之后，可以访问 `http://127.0.0.1:8080/swaggerui`  查看到Swagger生成的API页面。