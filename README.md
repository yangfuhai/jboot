jboot 2.0 is developing


## 开始

注意：
> 由于Jboot2.0 还处于 alpha 阶段，请不要使用在正式环境里。 

**maven 依赖**

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>2.0-alpha.1</version>
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