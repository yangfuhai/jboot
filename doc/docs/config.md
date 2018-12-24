# 配置文件

在 Jboot 应用中，可以通过几下几种方式给 Jboot 应用进行配置。

- jboot.properties 配置文件
- 环境变量
- Jvm 系统属性
- 启动参数

> 注意：如果同一个属性被多处配置，那么 Jboot 读取配置的优先顺序是：
> `启动参数` > `Jvm 系统属性` > `环境变量` > `jboot.properties 配置`


## 目录

- 读取配置
- 注入配置
- 配置实体类
- 设计原因
- 常见问题



## 读取配置

```java

String host = Jboot.configValue("undertow.host")
String port = Jboot.configValue("undertow.port")
```



## 注入配置

```java

public class AopController extends JbootController {

    @ConfigInject("undertow.host")
    private String host;

    @ConfigInject("undertow.port")
    private int port;

    public void index(){
        renderText("host:" + host +"   port:" + port);
    }
}
```


## 配置实体类

很多时候，某个功能或组件可能需要 `一堆` 的配置，而不是一个配置，无论是手动编码读取 或者 是通过注入，就可以让我们的项目产生重复的代码。

Jboot 提供了配置实体类功能，该功能自动把配置信息 `映射`  给一个 JavaBean 实体类，方便我们 `批量` 读取配置信息。

例如：

某个组件叫 `component1` ，它需要如下几个配置信息。

- 主机
- 端口号
- 账号
- 密码
- 超时时间

那么，我们可以创建一个叫 `Component1Config` 的实体类，定义好其属性，如下代码 ：

```java

@ConfigModel(prefix="component1")
public class Component1Config{
    private String host;
    private int port;
    private String accout;
    private String password;
    private long timeout;

    // 下方应该还有 getter setter， 略
}
```



这样，我们就可以通过如下代码读 `Component1Config` 信息。

```java

Component1Config config = Jboot.config(Component1Config.class);

```

> 备注：`@ConfigModel(prefix="component1")` 注解的含义是 `Component1Config` 的前缀是 `component1` ，因此，其属性 `host` 是来至配置文件的 `component1.host` 的值。

## 设计原因

由于 Jboot 定位是微服务框架，同时 Jboot 假设：基于 Jboot 开发的应用部署在 Docker 之上。

因此，在做 Devops 的时候，编排工具（例如：k8s、mesos）会去修改应用的相关配置，而通过环境变量和启动配置，无疑是最方便快捷的。


## 常见问题

1、如何设置启动参数 ？
> 答：在 fatjar 模式下，可以通过添加 `--`（两个中划线） 来指定配置，例如：java -jar --undertow.port=8080  --undertow.host=0.0.0.0


2、如何设置 Jvm 系统属性 ？
> 答：和启动参数一样，只需要把 `--` 换成 `-D`，例如： java -jar -Dundertow.port=8080 -Dundertow.host=0.0.0.0


2、如何设置系统环境变量 ？
> 答：在 Docker 下，启动 Docker 容器的时候，只需要添加 -e 参数即可，Linux、Window、Mac 搜索引擎自行搜索关键字： `环境变量配置`

