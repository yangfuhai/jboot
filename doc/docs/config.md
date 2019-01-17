# 配置文件



## 目录

- 描述
- 读取配置
- 注入配置
- 注解配置
- 配置实体类
- 设计原因
- 常见问题
- Jboot所有配置参考

## 描述

在 Jboot 应用中，可以通过几下几种方式给 Jboot 应用进行配置。

- jboot.properties 配置文件
- 环境变量
- Jvm 系统属性
- 启动参数

> 注意：如果同一个属性被多处配置，那么 Jboot 读取配置的优先顺序是：
> `启动参数` > `Jvm 系统属性` > `环境变量` > `jboot.properties 配置`





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

## 注解配置

在应用开发中，我们通常会使用注解，Jboot 内置了多个注解。

例如：
- @RequestMapping
- @EnableCORS
- @RPCInject
- @RPCBean
- ...等等

在使用注解的时候，我们通常会这样来使用，例如：

```java
@RequestMapping("/user")
public class UserController extends Controller{
    //....
}
```
或者

```java
@RPCBean(group="myGroup",version="myVersion",port=...)
public class UserServiceProvider extends UserService{
    //....
}
```

但是，无论是 `@RequestMapping("/user")` 或者是 `@RPCBean(group="myGroup",version="myVersion",port=...)` , 其参数配置都是固定的，因此，Jboot 提供了一种动态的配置方法，可以用于读取配置文件的内容。

例如：

```java
@RequestMapping("${user.mapping}")
public class UserController extends Controller{
    //....
}
```

然后在配置文件 `jboot.properties` （也可以是启动参数、环境变量等）添加上：

```
user.mapping = /user
```

其作用是等效于：

```java
@RequestMapping("/user")
public class UserController extends Controller{
    //....
}
```

因此，在 Jboot 应用中，注解的值可以通过 `${key}` 的方式，读取到配置内容的 key 对于的 value 值。



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
> 答：在 Docker 下，启动 Docker 容器的时候，只需要添加 -e 参数即可，例如： `docker run -e undertow.port=8080 xxxx`
> Linux、Window、Mac 搜索引擎自行搜索关键字： `环境变量配置`


## Jboot 所有配置参考

```
undertow.devMode=true # 设置undertow为开发模式
undertow.port=80 #undertow 的端口号，默认 8080，配置 * 为随机端口
undertow.host=0.0.0.0 #默认为localhost
undertow.resourcePath = src/main/webapp, classpath:static
undertow.ioThreads=
undertow.workerThreads=
undertow.gzip.enable=true # gzip 压缩开关
undertow.gzip.level=-1 # 配置压缩级别，默认值 -1。 可配置 1 到 9。 1 拥有最快压缩速度，9 拥有最高压缩率
undertow.gzip.minLength=1024 # 触发压缩的最小内容长度
undertow.session.timeout=1800 # session 过期时间，注意单位是秒
undertow.session.hotSwap=true # 支持 session 热加载，避免依赖于 session 的登录型项目反复登录，默认值为 true。仅用于 devMode，生产环境无影响
undertow.ssl.enable=false # 是否开启 ssl
undertow.ssl.port=443 # ssl 监听端口号，部署环境设置为 443
undertow.ssl.keyStoreType=PKCS12 # 密钥库类型，建议使用 PKCS12
undertow.ssl.keyStore=demo.pfx # 密钥库文件
undertow.ssl.keyStorePassword=123456 # 密钥库密码
undertow.ssl.keyAlias=demo # 别名配置，一般不使用
undertow.http2.enable=true # ssl 开启时，是否开启 http2
undertow.http.toHttps=false # ssl 开启时，http 请求是否重定向到 https
undertow.http.toHttpsStatusCode=302 # ssl 开启时，http 请求跳转到 https 使用的状态码，默认值 302
undertow.http.disable=false # ssl 开启时，是否关闭 http

jboot.app.mode
jboot.app.bannerEnable
jboot.app.bannerFile
jboot.app.jfinalConfig

jboot.web.actionCacheEnable
jboot.web.actionCacheKeyGeneratorType
jboot.web.cookieEncryptKey

jboot.web.session.cookieName
jboot.web.session.cookieDomain
jboot.web.session.cookieContextPath
jboot.web.session.maxInactiveInterval
jboot.web.session.cookieMaxAge
jboot.web.session.cacheName
jboot.web.session.cacheType

jboot.web.jwt.httpHeaderName
jboot.web.jwt.secret
jboot.web.jwt.validityPeriod

jboot.web.cdn.enable
jboot.web.cdn.domain

jboot.rpc.type
jboot.rpc.callMode
jboot.rpc.requestTimeOut
jboot.rpc.registryType
jboot.rpc.registryAddress
jboot.rpc.registryName
jboot.rpc.registryUserName
jboot.rpc.registryPassword
jboot.rpc.registryFile
jboot.rpc.registryCheck
jboot.rpc.consumerCheck
jboot.rpc.providerCheck
jboot.rpc.directUrl
jboot.rpc.host
jboot.rpc.defaultPort
jboot.rpc.defaultGroup
jboot.rpc.defaultVersion
jboot.rpc.proxy
jboot.rpc.filter
jboot.rpc.serialization
jboot.rpc.retries
jboot.rpc.autoExportEnable

jboot.rpc.dubbo.protocolName
jboot.rpc.dubbo.protocolServer
jboot.rpc.dubbo.protocolContextPath
jboot.rpc.dubbo.protocolTransporter
jboot.rpc.dubbo.protocolThreads
jboot.rpc.dubbo.protocolHost
jboot.rpc.dubbo.protocolPort
jboot.rpc.dubbo.protocolContextpath
jboot.rpc.dubbo.protocolThreadpool
jboot.rpc.dubbo.protocolIothreads
jboot.rpc.dubbo.protocolQueues
jboot.rpc.dubbo.protocolAccepts
jboot.rpc.dubbo.protocolCodec
jboot.rpc.dubbo.protocolSerialization
jboot.rpc.dubbo.protocolCharset
jboot.rpc.dubbo.protocolPayload
jboot.rpc.dubbo.protocolBuffer
jboot.rpc.dubbo.protocolHeartbeat
jboot.rpc.dubbo.protocolAccesslog
jboot.rpc.dubbo.protocolExchanger
jboot.rpc.dubbo.protocolDispatcher
jboot.rpc.dubbo.protocolNetworker
jboot.rpc.dubbo.protocolClient
jboot.rpc.dubbo.protocolTelnet
jboot.rpc.dubbo.protocolPrompt
jboot.rpc.dubbo.protocolStatus
jboot.rpc.dubbo.protocolRegister
jboot.rpc.dubbo.protocolKeepAlive
jboot.rpc.dubbo.protocolOptimizer
jboot.rpc.dubbo.protocolExtension
jboot.rpc.dubbo.protocolIsDefault
jboot.rpc.dubbo.qosEnable
jboot.rpc.dubbo.qosPort
jboot.rpc.dubbo.qosAcceptForeignIp

jboot.rpc.zbus.serviceName
jboot.rpc.zbus.serviceToken

jboot.mq.type
jboot.mq.channel
jboot.mq.serializer
jboot.mq.syncRecevieMessageChannel

jboot.mq.redis.host
jboot.mq.redis.port
jboot.mq.redis.password
jboot.mq.redis.database
jboot.mq.redis.timeout
jboot.mq.redis.clientName
jboot.mq.redis.testOnCreate
jboot.mq.redis.testOnBorrow
jboot.mq.redis.testOnReturn
jboot.mq.redis.testWhileIdle
jboot.mq.redis.minEvictableIdleTimeMillis
jboot.mq.redis.timeBetweenEvictionRunsMillis
jboot.mq.redis.numTestsPerEvictionRun
jboot.mq.redis.maxAttempts
jboot.mq.redis.maxTotal
jboot.mq.redis.maxIdle
jboot.mq.redis.maxWaitMillis
jboot.mq.redis.serializer
jboot.mq.redis.type

jboot.mq.rabbitmq.username
jboot.mq.rabbitmq.password
jboot.mq.rabbitmq.host
jboot.mq.rabbitmq.port
jboot.mq.rabbitmq.virtualHost

jboot.mq.qpid.host
jboot.mq.qpid.username
jboot.mq.qpid.password
jboot.mq.qpid.virtualHost

jboot.mq.aliyun.accessKey
jboot.mq.aliyun.secretKey
jboot.mq.aliyun.addr
jboot.mq.aliyun.producerId
jboot.mq.aliyun.sendMsgTimeoutMillis

jboot.mq.zbus.queue
jboot.mq.zbus.broker

jboot.cache.type
jboot.cache.ehcache.configFileName
jboot.cache.redis.host
jboot.cache.redis.port
jboot.cache.redis.password
jboot.cache.redis.database
jboot.cache.redis.timeout
jboot.cache.redis.clientName
jboot.cache.redis.testOnCreate
jboot.cache.redis.testOnBorrow
jboot.cache.redis.testOnReturn
jboot.cache.redis.testWhileIdle
jboot.cache.redis.minEvictableIdleTimeMillis
jboot.cache.redis.timeBetweenEvictionRunsMillis
jboot.cache.redis.numTestsPerEvictionRun
jboot.cache.redis.maxAttempts
jboot.cache.redis.maxTotal
jboot.cache.redis.maxIdle
jboot.cache.redis.maxWaitMillis
jboot.cache.redis.serializer
jboot.cache.redis.type

jboot.schedule.cron4jFile
jboot.schedule.poolSize

jboot.model.scan
jboot.model.columnCreated
jboot.model.columnModified
jboot.model.idCacheEnable
jboot.model.idCacheType
jboot.model.idCacheTime

jboot.metric.url
jboot.metric.reporter
jboot.metric.reporter.cvr.path
jboot.metric.reporter.graphite.host
jboot.metric.reporter.graphite.port
jboot.metric.reporter.graphite.prefixedWith

jboot.wechat.debug
jboot.wechat.appId
jboot.wechat.appSecret
jboot.wechat.token
jboot.wechat.partner
jboot.wechat.paternerKey
jboot.wechat.cert

jboot.shiro.loginUrl
jboot.shiro.successUrl
jboot.shiro.unauthorizedUrl
jboot.shiro.ini
jboot.shiro.urlMapping
jboot.shiro.invokeListener

jboot.serializer.type

jboot.swagger.path
jboot.swagger.title
jboot.swagger.description
jboot.swagger.version
jboot.swagger.termsOfService
jboot.swagger.host
jboot.swagger.contactName
jboot.swagger.contactEmail
jboot.swagger.contactUrl
jboot.swagger.licenseName
jboot.swagger.licenseUrl

jboot.http.type

jboot.redis.host
jboot.redis.port
jboot.redis.password
jboot.redis.database
jboot.redis.timeout
jboot.redis.clientName
jboot.redis.testOnCreate
jboot.redis.testOnBorrow
jboot.redis.testOnReturn
jboot.redis.testWhileIdle
jboot.redis.minEvictableIdleTimeMillis
jboot.redis.timeBetweenEvictionRunsMillis
jboot.redis.numTestsPerEvictionRun
jboot.redis.maxAttempts
jboot.redis.maxTotal
jboot.redis.maxIdle
jboot.redis.maxWaitMillis
jboot.redis.serializer
jboot.redis.type
```

