# RPC 远程调用

## 目录

- 配置
- 开始使用
- 高级功能

## 配置
在 Jboot 中，默认实现了对 Dubbo、motan 的 RPC 调用支持。在使用 RPC 远程调用之前，需要做一些基本的配置。


例如 ：
```
jboot.rpc.type = dubbo
jboot.rpc.callMode = direct
jboot.rpc.directUrl = 127.0.0.1:8000
```

- jboot.rpc.type ： RPC 的类型，不配置默认为 Dubbo
- jboot.rpc.callMode ： RPC 的调用方式
  - direct ： 直联模式
  - registry ： 注册中心模式（服务注册和服务发现）
- jboot.rpc.directUrl ： 当 callMode 为 direct 直联模式时，需要配置直联模式的服务器 IP 地址和端口号。

> 更多的配置请查看 [config.md](./config.md)

## 开始使用

一般情况下，RPC 调用需要以下几个步骤：

- 1、定义接口
- 2、编写实现类
- 3、启动 Server 暴露服务
- 4、启动客户端、通过 RPC 调用 Server 提供的服务


**定义接口**

```java

public interface BlogService {

    public String findById();
    public List<String> findAll();
}
```

**编写实现类**

```java


@RPCBean
public class BlogServiceProvider implements BlogService {

    @Override
    public String findById() {
        return "id from provider";
    }

    @Override
    public List<String> findAll() {
        return Lists.newArrayList("item1","item2");
    }
}
```

**启动 Server 暴露服务**

```java

public class DubboServer {

    public static void main(String[] args)  {

        JbootApplication.run(args);
        System.out.println("DubboServer started...");

    }
}
```


**启动客户端、通过 RPC 调用 Server 提供的服务**
```java

@RequestMapping("/dubbo")
public class DubboClient extends JbootController{

    public static void main(String[] args)  {

        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "8888");

        JbootApplication.run(args);
    }


    @RPCInject
    private  BlogService blogService;

    public void index() {
        System.out.println(blogService);
        renderText("blogId : " + blogService.findById());
    }

}
```


## 高级功能

在以上的示例中，使用到了两个注解：
- @RPCBean，标示当前服务于RPC服务
- @RPCInject，RPC注入赋值

虽然以上示例中，`@RPCBean` 和 `@RPCInject` 没有添加任何的参数，但实际是他们提供了非常丰富的配置。

以下分别是 `@RPCBean` 和 `@RPCInject` 的定义：

RPCBean ：
```java

public @interface RPCBean {

    int port() default 0;
    int timeout() default -1;
    int actives() default -1; 
    String group() default "";
    String version() default "";

    //当一个Service类实现对个接口的时候，
    //可以通过这个排除不暴露某个实现接口
    Class[] exclude() default Void.class;
}
```


RPCInject ：

```java

public @interface RPCInject {

    int port() default 0;
    int timeout() default -1;
    int retries() default -1;
    int actives() default -1;
    String group() default "";
    String version() default "";
    String loadbalance() default "";
    String async() default "";
    String check() default "";
}
```