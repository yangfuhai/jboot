jboot 2.0 is developing


## 开始

注意：
> 由于Jboot2.0 还处于 rc 阶段，请不要使用在正式环境里。 

**maven 依赖**

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>2.0-rc.1</version>
</dependency>
```

**Hello World**

Controller：

```java
@RequestMapping("/helloworld")
public class HelloworldController extends JbootController {

    public void index(){
        renderText("hello world");
    }
}
```

HelloWorldApp:

```java
public class HelloWorldApp {

    public static void main(String[] args){
        JbootApplication.run(args);
    }
}
```


## 帮助文档

- [安装](./doc/docs/install.md)
- [2分钟快速开始](./doc/docs/quickstart.md)
- [配置文件](./doc/docs/config.md)
- [热加载](./doc/docs/config.md)
- [MVC](./)
- [数据库](./)
- [缓存](./)
- [RPC远程调用](./)
- [MQ消息队列](./)
- [任务调度](./)
- [序列化](./)
- [事件机制](./)
- [SPI扩展机制](./)
- [代码生成器](./)
- [项目构建](./)
- [项目部署](./)
- [Jboot与Docker](./)
- [Jboot与Devops](./)
- [交流社区、QQ群和微信群](./)
