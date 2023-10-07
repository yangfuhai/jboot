# RPC 远程调用

## 说明

此文档只适用于 jboot v3.1.0 以上，之前的版本请参考 [这里](./rpc2.x.md) 。

## 目录

- 添加依赖
- 配置
- 开始使用
- restful 暴露
- 高级功能

## 添加依赖

Jboot 支持 dubbo 和 motan，假设我们需要使用 dubbo 作为底层的 RPC 框架，需要添加如下依赖：

```xml
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo</artifactId>
    <version>${dubbo.version}</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.alibaba.spring</groupId>
            <artifactId>spring-context-support</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-dependencies-zookeeper</artifactId>
    <version>${dubbo.version}</version>
    <type>pom</type>
</dependency>
```

如果使用 motan，需要添加如下依赖：

```xml
<dependency>
    <groupId>com.weibo</groupId>
    <artifactId>motan-core</artifactId>
    <version>${motan.version}</version>
</dependency>

<dependency>
    <groupId>com.weibo</groupId>
    <artifactId>motan-transport-netty4</artifactId>
    <version>${motan.version}</version>
</dependency>

<!-- 以下的 consul 和 zookeeper 只需要一个即可，使用哪个注册中心就导入哪个依赖  -->
<dependency>
    <groupId>com.weibo</groupId>
    <artifactId>motan-registry-consul</artifactId>
    <version>${motan.version}</version>
</dependency>

<dependency>
    <groupId>com.weibo</groupId>
    <artifactId>motan-registry-zookeeper</artifactId>
    <version>${motan.version}</version>
</dependency>
```

## 配置
在 Jboot 中默认实现了对 Dubbo、motan 的 RPC 调用支持。在使用 RPC 远程调用之前，需要做一些基本的配置。


例如 ：

```properties
jboot.rpc.type = dubbo
jboot.rpc.urls = com.yourdomain.AAAService:127.0.0.1:8080,com.yourdomain.XXXService:127.0.0.1:8080
jboot.rpc.providers = com.yourdomain.AAAService:providerName,com.yourdomain.XXXService:providerName
jboot.rpc.consumers = com.yourdomain.AAAService:consumerName,com.yourdomain.XXXService:consumerName
jboot.rpc.defaultVersion = 1.0.0
jboot.rpc.versions = com.yourdomain.AAAService:1.0.0,com.yourdomain.XXXService:1.0.1
jboot.rpc.defaultGroup = 
jboot.rpc.groups = com.yourdomain.AAAService:group1,com.yourdomain.XXXService:group2
jboot.rpc.autoExportEnable = true
```

- jboot.rpc.type ： RPC 的类型，目前只支持 dubbo 和 motan
- jboot.rpc.urls ： 一般不用配置，只有直连模式下才会去配置，此处是配置 Service接口和URL地址的映射关系。
- jboot.rpc.providers ： 配置 Service 和 Provider 的映射关系（ Motan下配置的是 Service 和 BasicService 的映射关系）。
- jboot.rpc.consumers ： 配置 Reference 和 consumer 的映射关系（ Motan下配置的是 Referer 和 BaseReferer 的映射关系）。
- jboot.rpc.defaultVersion ： 当service不配置版本时，默认的版本号，默认值为 1.0.0
- jboot.rpc.versions ： 每个服务对应的版本号
- jboot.rpc.defaultGroup ： 当服务不配置 group 时，默认的 gourp
- jboot.rpc.groups ： 每个服务对应的 group
- jboot.rpc.autoExportEnable ： 当 Jboot 启动的时候，是否自动暴露 @RPCBean 注解的接口。

在 以上 示例中，`jboot.rpc.providers` 配置中，可以对每个 Service 进行配置，但是，在绝大多数的情况下，我们可能只需要一个配置，这个配置应用于所有的 Service 服务，此时我们需要做如下配置：

```properties
# 名称为 default 的 provider 配置（当不配置其名称的时候，名称默认为 default）
jboot.rpc.dubbo.provider.timeout = xx.xx.xx
jboot.rpc.dubbo.provider.loadbalance = xx.xx.xx
jboot.rpc.dubbo.provider.group = xx.xx.xx
jboot.rpc.dubbo.provider.host = xx.xx.xx
jboot.rpc.dubbo.provider.default = true  #设置当前 provider 为默认配置，既所有未指定 provider 的 service 服务都使用此配置。 

# 名称为 name1 的 provider 配置
jboot.rpc.dubbo.provider.name1.timeout = xx.xx.xx
jboot.rpc.dubbo.provider.name1.loadbalance = xx.xx.xx
jboot.rpc.dubbo.provider.name1.group = xx.xx.xx
jboot.rpc.dubbo.provider.name1.host = xx.xx.xx

# 名称为 name2 的 provider 配置
jboot.rpc.dubbo.provider.name2.timeout = xx.xx.xx
jboot.rpc.dubbo.provider.name2.loadbalance = xx.xx.xx
jboot.rpc.dubbo.provider.name2.group = xx.xx.xx
jboot.rpc.dubbo.provider.name2.host = xx.xx.xx


# 配置 com.yourdomain.AAAService 使用的 provider 配置为 name1
# 配置 com.yourdomain.AAAService 使用的 provider 配置为 name2
# 其他所有服务的 provider 配置使为 default，原因是名称为 default 的 provider 其属性 default 为 true 了
# 此处要注意，如果我们给 name2 的 provider 添加配置 jboot.rpc.dubbo.provider.name2.default = true，
# 那么所有的未配置 providers 的服务都使用 name2 作为其默认位置。
jboot.rpc.providers = com.yourdomain.AAAService:name1,com.yourdomain.XXXService:name2
```

provider 的更多配置情况参考：https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-provider/


## 开始使用

一般情况下，RPC 调用需要以下几个步骤：

- 1、定义接口
- 2、编写实现类
- 3、启动 Server（Provider） 暴露服务
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

### Dubbo 
**启动 Server 暴露服务**


```java
public class DubboServer {

    public static void main(String[] args)  {
        
        JbootApplication.setBootArg("undertow.port", "9998");

        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");

        // 开启 @RPCBean 自动暴露功能，默认情况下是开启的，无需配置，
        // 但是此测试代码的 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);

        //dubbo 的通信协议配置
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.name", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.port", "28080");


        // dubbo 注册中心的配置，
        // 当不配置注册中心的时候，默认此服务只提供了直联模式的请求
        // JbootApplication.setBootArg("jboot.rpc.dubbo.registry.protocol", "zookeeper");
        // JbootApplication.setBootArg("jboot.rpc.dubbo.registry.address", "127.0.0.1:2181");


        JbootApplication.run(args);
        System.out.println("DubboServer started...");

    }
}
```

>备注：以上的 JbootApplication.setBootArg() 里设置的内容，都可以配置到 jboot.properties 里。例如： `JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);` 在 jboot.properties
>里对应的配置是 `jboot.rpc.autoExportEnable = true` 。



**启动 dubbo 客户端、通过 RPC 调用 Server 提供的服务**

```java
@RequestMapping("/dubbo")
public class DubboClient extends JbootController{

    public static void main(String[] args)  {

       //Undertow端口号配置
       JbootApplication.setBootArg("undertow.port", "9999");

       //RPC配置
       JbootApplication.setBootArg("jboot.rpc.type", "dubbo");

       //设置直连模式，方便调试，默认为注册中心
       JbootApplication.setBootArg("jboot.rpc.urls", "io.jboot.test.rpc.commons.BlogService:127.0.0.1:28080");

        JbootApplication.run(args);
    }


    @RPCInject
    private  BlogService blogService;
    //当不在controller的生命周期中调用RPC接口时，用Jboot.service系列方法
    //@RPCInject
    //private  BlogService blogService = Jboot.service(BlogService.class);

    public void index() {
        System.out.println(blogService);
        renderText("blogId : " + blogService.findById());
    }
}
```

自定义Dubbo异常处理
resouces下创建 META-INF.dubbo文件夹，并创建org.apache.dubbo.rpc.Filter文件
增加配置代码： 
```java
##覆盖dubbo默认的异常拦截
#exception=org.apache.dubbo.rpc.filter.ExceptionFilter
exception=com.sample.dubbo.CustomRpcExceptionFilter
```

### Motan

**Motan 服务端**

```java
public class MotanServer {

    public static void main(String[] args) throws InterruptedException {


        JbootApplication.setBootArg("jboot.rpc.type", "motan");

        // 开启 @RPCBean 自动暴露功能，默认情况下是开启的，无需配置，
        // 但是此测试代码的 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);

        // motan 与 dubbo 不一样，motan 需要配置 export，
        // export 配置内容为 协议ID:端口号，默认的协议 id 为 default
        JbootApplication.setBootArg("jboot.rpc.motan.defaultExport", "default:28080");

        // motan 的注册中心的协议
        // JbootApplication.setBootArg("jboot.rpc.motan.registry.regProtocol", "zookeeper");
        //注册中心地址，即zookeeper的地址
        // JbootApplication.setBootArg("jboot.rpc.motan.registry.address", "127.0.0.1:2181");




        JbootSimpleApplication.run(args);

        System.out.println("MotanServer started...");

    }
}

```


**Motan 客户端**

```java
@RequestMapping("/motan")
public class MotanClient extends JbootController {

    public static void main(String[] args) {


        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "9999");

        //RPC配置
        JbootApplication.setBootArg("jboot.rpc.type", "motan");
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", false);

        //设置直连模式，方便调试，默认为注册中心
        JbootApplication.setBootArg("jboot.rpc.urls", "io.jboot.test.rpc.commons.BlogService:127.0.0.1:28080");


        // motan 的注册中心的协议
        // JbootApplication.setBootArg("jboot.rpc.motan.registry.regProtocol", "zookeeper");
        //注册中心地址，即zookeeper的地址
        // JbootApplication.setBootArg("jboot.rpc.motan.registry.address", "127.0.0.1:2181");

        JbootApplication.run(args);
    }


    @RPCInject
    private BlogService blogService;

//    @Before(MotanInterceptor.class)
    public void index() {

        System.out.println("blogService:" + blogService);

        renderText("blogId : " + blogService.findById());
    }


}
```


## restful 暴露

在某些情况下，我们希望 rpc service 通过 restful 协议暴露给其他客户端（或者其他编程语言）去使用，我们需要添加如下的依赖。

PS：目前只有 dubbo 支持了 restful 协议，其他 rpc 框架暂时不支持。


```xml
<!-- 用于支持 dubbo 的 restful 注解 start -->
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>${io.netty.version}</version>
</dependency>

<dependency>
    <groupId>org.jboss.resteasy</groupId>
    <artifactId>resteasy-jaxrs</artifactId>
    <version>${org.jboss.resteasy.version}</version>
</dependency>

<dependency>
    <groupId>org.jboss.resteasy</groupId>
    <artifactId>resteasy-client</artifactId>
    <version>${org.jboss.resteasy.version}</version>
</dependency>

<dependency>
    <groupId>org.jboss.resteasy</groupId>
    <artifactId>resteasy-netty4</artifactId>
    <version>${org.jboss.resteasy.version}</version>
</dependency>

<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>1.1.0.Final</version>
</dependency>

<dependency>
    <groupId>org.jboss.resteasy</groupId>
    <artifactId>resteasy-jackson-provider</artifactId>
    <version>${org.jboss.resteasy.version}</version>
</dependency>

<dependency>
    <groupId>org.jboss.resteasy</groupId>
    <artifactId>resteasy-jaxb-provider</artifactId>
    <version>${org.jboss.resteasy.version}</version>
</dependency>
<!-- 用于支持 dubbo 的 restful 注解 end -->
```

其中版本号对应为

```xml
<io.netty.version>4.1.9.Final</io.netty.version>
<org.jboss.resteasy.version>3.9.0.Final</org.jboss.resteasy.version>
```

第二步需要在 接口添加 相关注解

```java
@Path("users") // #1
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML}) // #2
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
public interface UserService {
    @GET // #3
    @Path("{id: \\d+}")
    User getUser(@PathParam("id") Long id);

    @POST // #4
    @Path("register")
    Long registerUser(User user);
}
```

这部分的内容，可以添加在接口里，也可以添加在实现类里，具体参考：http://dubbo.apache.org/zh-cn/blog/dubbo-rest.html


第三步，在 jboot.properties 添加 restful 协议：

```properties
jboot.rpc.dubbo.protocol.name = dubbo
jboot.rpc.dubbo.protocol.host = 127.0.0.1
jboot.rpc.dubbo.protocol.port = 28080

jboot.rpc.dubbo.protocol.rest.name = rest
jboot.rpc.dubbo.protocol.rest.host = 127.0.0.1
jboot.rpc.dubbo.protocol.rest.port = 8080
jboot.rpc.dubbo.protocol.rest.server = netty
```

第四步：给 Service 配置暴露协议


```properties
jboot.rpc.dubbo.provider.protocal = default,rest //使用 dubbo 和 rest 两种协议同时暴露
jboot.rpc.dubbo.provider.default = true // 给应用配置默认的 provider
```



## 高级功能

### 更多的 dubbo 配置

目前，jboot 不支持通过 jboot.properties 直接对 service 和 reference 配置，但是可以对 provider 和 consumer 的配置，
在通过 @RPCInject 来再次复制给 service  或者 reference，结果也等同于对 service 和 reference 配置进行配置了。


#### application

https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-application/

对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.application`

例如：

```properties
jboot.rpc.dubbo.application.name = xx.xx.xx
jboot.rpc.dubbo.application.version = xx.xx.xx
```


#### registry

https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-registry/


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.registry`

例如：

```properties
jboot.rpc.dubbo.registry.address = xx.xx.xx
```

> 例如使用 ncaos 注册中心时，配置内容如下：
> 
> `jboot.rpc.dubbo.registry.address=nacos://127.0.0.1:8848?namespace=test`




更多的注册中心配置内容如下：

```properties
jboot.rpc.dubbo.registry.address = xx.xx.xx
jboot.rpc.dubbo.registry.port = xx.xx.xx
jboot.rpc.dubbo.registry.username = xx.xx.xx
jboot.rpc.dubbo.registry.password = xx.xx.xx
```

**多个注册中心**

多注册中心可以配置如下（多 protocol 、多 consumer、多provider 都是同理）：


```properties
jboot.rpc.dubbo.registry.address = xx.xx.xx
jboot.rpc.dubbo.registry.port = xx.xx.xx
jboot.rpc.dubbo.registry.username = xx.xx.xx
jboot.rpc.dubbo.registry.password = xx.xx.xx


jboot.rpc.dubbo.registry.other1.address = xx.xx.xx
jboot.rpc.dubbo.registry.other1.port = xx.xx.xx
jboot.rpc.dubbo.registry.other1.username = xx.xx.xx
jboot.rpc.dubbo.registry.other1.password = xx.xx.xx


jboot.rpc.dubbo.registry.other2.address = xx.xx.xx
jboot.rpc.dubbo.registry.other2.port = xx.xx.xx
jboot.rpc.dubbo.registry.other2.username = xx.xx.xx
jboot.rpc.dubbo.registry.other2.password = xx.xx.xx
```

这样，在系统中就存在了多个注册中心，第一个配置的名称（name）分别为 default，第二个和第三个为
other1、other2，这样，当一个服务（或者接口）需要在多个注册中心暴露的时候，只需要在其 registry 配置相应的 name 即可。

例如：

```properties
jboot.rpc.dubbo.provider.address = default,other1
```

若当服务没有指定注册中心，注册中心默认为 default。


#### protocol

https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-protocol/


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.protocol`

例如：

```properties
jboot.rpc.dubbo.protocol.host = 127.0.0.1
jboot.rpc.dubbo.protocol.port = 28080
```



#### provider

https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-provider/


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.provider`

例如：

```
jboot.rpc.dubbo.provider.host = 127.0.0.1
```



#### consumer

https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-consumer/


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.consumer`

例如：

```
jboot.rpc.dubbo.consumer.timeout = 127.0.0.1
```



#### monitor

https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-monitor/


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.monitor`

例如：

```
jboot.rpc.dubbo.monitor.protocol = xxx
```


#### metrics


对应的配置类： org.apache.dubbo.config.MetricsConfig


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.metrics`

例如：

```
jboot.rpc.dubbo.metrics.protocol = xxx
```


#### module


https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-module/


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.module`

例如：

```
jboot.rpc.dubbo.module.name = xxx
```


#### MetadataReport


对应的配置类： org.apache.dubbo.config.MetadataReportConfig


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.metadata-report`

例如：

```
jboot.rpc.dubbo.metadata-report.group = xxx
```


#### ConfigCenter


https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-config-center/


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.config-center`

例如：

```
jboot.rpc.dubbo.config-center.group = xxx
```

#### method


https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-method/


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.method`

例如：

```
jboot.rpc.dubbo.method.name = xxx
```

#### argument


https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-argument/


对应的 jboot 的配置前缀为： `jboot.rpc.dubbo.argument`

例如：

```
jboot.rpc.dubbo.argument.name = xxx
```

