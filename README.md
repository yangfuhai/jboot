

## 开始

**maven 依赖**

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>3.10.0</version>
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

- 文档请访问：[www.jboot.io](http://www.jboot.io)
- Demos 请访问：[这里](./src/test/java/io/jboot/test)


## 微信交流群

![](./doc/docs/static/images/jboot-wechat-group.png)

## JbootAdmin 

JbootAdmin 是 Jboot 官方推出的、收费的、企业级快速开发框架，真诚的为各位开发者提供一站式、保姆式的开发服务。
关于 JbootAdmin 的功能详情或者演示，请咨询海哥（微信：wx198819880）。

![](./doc/jbootadmin/images/jbootadmin-demo.jpg)


更多关于 JbootAdmin：

- 福利1：https://mp.weixin.qq.com/s/UjaqF6v8yNufm7X5r4UFSw
- 福利2：https://mp.weixin.qq.com/s/7eZDyjxX3jD4QxgIWLfGBQ
- 福利3：https://mp.weixin.qq.com/s/reV48hBRkWY2c9O9Fi1TfA