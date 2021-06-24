

## 开始

**maven 依赖**

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>3.9.20</version>
</dependency>
```

**Hello World**

```java
@RequestMapping("/")
public class HelloworldController extends JbootController {

    public void index(){
        renderText("hello world");
    }

    public static void main(String[] args){
        JbootApplication.run(args);
    }
}
```


## 帮助文档

- [demos](./src/test/java/io/jboot/test)
- [安装](./doc/docs/install.md)
- [2分钟快速开始](./doc/docs/quickstart.md)
- [热加载](./doc/docs/hotload.md)
- [Undertow](./doc/docs/undertow.md)
- [配置](./doc/docs/config.md)
- [JFinalConfig](./doc/docs/jfinalConfig.md)
- [WebSocket](./doc/docs/websocket.md)
- [MVC](./doc/docs/mvc.md)
- [AOP](./doc/docs/aop.md)
- [数据库](./doc/docs/db.md)
- [缓存](./doc/docs/cache.md)
- [RPC远程调用](./doc/docs/rpc.md)
- [MQ消息队列](./doc/docs/mq.md)
- [Gateway 网关](./doc/docs/gateway.md)
- [任务调度](./doc/docs/schedule.md)
- [限流](./doc/docs/limit.md)
- [监控](./doc/docs/metrics.md)
- [序列化](./doc/docs/serialize.md)
- [事件机制](./doc/docs/event.md)
- [SPI扩展机制](./doc/docs/spi.md)
- [单元测试](./doc/docs/junit.md)
- [代码生成器](./doc/docs/codegen.md)
- [项目打包](./doc/docs/build.md)
- [项目部署](./doc/docs/deploy.md)
- [Jboot与Docker](./doc/docs/docker.md)
- [1.x 升级到 2.x 教程](./doc/docs/upgrade.md)
- [交流社区、QQ群和微信群](./doc/docs/communication.md)
- 第三方组件的支持
  - [sentinel 限流降级](./doc/docs/sentinel.md) 
  - [redis](./doc/docs/redis.md)
  - [shiro](./doc/docs/shiro.md)
  - [jwt](./doc/docs/jwt.md)
  - [swagger](./doc/docs/swagger.md)
  


## 微信交流群

![](./doc/docs/static/images/jboot-wechat-group.png)

## JbootAdmin 

JbootAdmin 是 Jboot 官方推出的、收费的、企业级快速开发框架，关于 JbootAdmin 的功能详情或者演示，请咨询海哥（微信：wx198819880）。

![](./doc/jbootadmin/images/jbootadmin-demo.jpg)


更多关于 JbootAdmin：

- 福利1：https://mp.weixin.qq.com/s/UjaqF6v8yNufm7X5r4UFSw
- 福利2：https://mp.weixin.qq.com/s/7eZDyjxX3jD4QxgIWLfGBQ
- 福利3：https://mp.weixin.qq.com/s/reV48hBRkWY2c9O9Fi1TfA