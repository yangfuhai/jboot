# 配置



## 目录

- 描述
- 读取配置
- 注入配置
- 动态配置
- 注解配置
- 配置实体类
- 开启 Nacos 分布式配置中心
- 开启 Apollo 分布式配置中心
- 配置内容加密解密
- 常见问题
- Jboot所有配置参考

## 描述

在 Jboot 应用中，可以通过几下几种方式给 Jboot 应用进行配置。

- jboot.properties 配置文件
- jboot-xxx.properties 配置文件
- 环境变量
- Jvm 系统属性
- 启动参数
- 分布式配置中心（目前支持 Apollo 和 Nacos）

> 注意：
> 1、如果同一个属性被多处配置，那么 Jboot 读取配置的优先顺序是：
> `分布式配置中心` > `启动参数` > `Jvm 系统属性` > `环境变量` > `jboot-xxx.properties` > `jboot.properties`。
> 
> 2、jboot-xxx.properties 的含义是：当配置 jboot.app.mode=dev 时，默认去读取 jboot-dev.properties，同理当配置 jboot.app.mode=product 时，默认去读取 jboot-product.properties，jboot-xxx.properties 的文件名称是来源于 jboot.app.mode 的配置。jboot-xxx.properties 这个文件并不是必须的，但当该配置文件存在时，其优读取顺序先于 jboot.properties。





## 读取配置

```java

String host = Jboot.configValue("undertow.host")
String port = Jboot.configValue("undertow.port")
```



## 注入配置

```java

public class AopController extends JbootController {

    @ConfigValue("undertow.host")
    private String host;

    @ConfigValue("undertow.port")
    private int port;

    public void index(){
        renderText("host:" + host +"   port:" + port);
    }
}
```

## 动态配置
在 Jboot 的所有配置中，我们可以通过 ${key} 来指定替换为 value。

示例1：

```xml
key1 = value1
key2 = ${key1}/abc
```

那么读取到的 key2 的值为 `value1/abc`。

示例2：

```xml
key1 = value1
key2 = ${key1}/abc
key3 = abc/${key2}/xyz
```
那么，key2 的值为 `value1/abc` ，key3 的值为 `abc/value1/abc/xyz`


示例2：

```xml
key1 = value1
key2 = ${otherkey}/abc
```
那么，因为系统中找不到 otherkey 的值，key2 的值为 `/abc`，如果我们在系统中，通过 `java -jar xxx.jar --otherkey=othervalue`，
那么， key2 的值为 `othervalue/abc`



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

但是，无论是 `@RequestMapping("/user")` 或者是 `@RPCBean(group="myGroup",version="myVersion",port=...)` , 
其参数配置都是固定的，因此，Jboot 提供了一种动态的配置方法，可以用于读取配置文件的内容。

例如：

```java
@RequestMapping("${user.mapping}/abc")
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
@RequestMapping("/use/abcr")
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

    // 下方应还有 getter setter， 此处略
}
```



这样，我们就可以通过如下代码读 `Component1Config` 信息。

```java

Component1Config config = Jboot.config(Component1Config.class);

```

配置内如如下：

```properties
component1.host = xxx
component1.port = xxx
component1.accout = xxx
component1.password = xxx
component1.timeout = xxx
```

> 备注：`@ConfigModel(prefix="component1")` 注解的含义是 `Component1Config` 的前缀是 `component1` ，因此，其属性 `host` 是来至配置文件的 `component1.host` 的值。




## 开启 Nacos 分布式配置中心

**第一步，添加 nacos 客户端的 Maven 依赖**

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <version>1.3.2</version>
</dependency>
```



**第二步：启动 nacos**

- Clone Nacos 项目

```
git clone https://github.com/nacos-group/nacos-docker.git
cd nacos-docker
```

单机模式 Derby
```
docker-compose -f example/standalone-derby.yaml up
```

单机模式 Mysql
```
docker-compose -f example/standalone-mysql.yaml up
```

集群模式
```
docker-compose -f example/cluster-hostname.yaml up 
```

Nacos 控制台

link：http://127.0.0.1:8848/nacos/

nacos 的相关文档在 

https://nacos.io/zh-cn/docs/quick-start.html 

或者

https://nacos.io/zh-cn/docs/quick-start-docker.html


**第三步，在 jboot.properties 添加如下配置**

```properties
jboot.config.nacos.enable = true
jboot.config.nacos.serverAddr = 127.0.0.1:8848
jboot.config.nacos.dataId = jboot
jboot.config.nacos.group = jboot
```

nacos 支持如下更多配置，但是只需要以上配置就可以正常运行。
    

```properties
jboot.config.nacos.isUseCloudNamespaceParsing = xxx
jboot.config.nacos.isUseEndpointParsingRule = xxx
jboot.config.nacos.endpoint = xxx
jboot.config.nacos.endpointPort = xxx
jboot.config.nacos.namespace = xxx
jboot.config.nacos.username = xxx
jboot.config.nacos.password = xxx
jboot.config.nacos.accessKey = xxx
jboot.config.nacos.secretKey = xxx
jboot.config.nacos.ramRoleName = xxx
jboot.config.nacos.serverAddr = xxx
jboot.config.nacos.contextPath = xxx
jboot.config.nacos.clusterName = xxx
jboot.config.nacos.encode = xxx
jboot.config.nacos.configLongPollTimeout = xxx
jboot.config.nacos.configRetryTime = xxx
jboot.config.nacos.maxRetry = xxx
jboot.config.nacos.enableRemoteSyncConfig = xxx
```

## 开启 Apollo 分布式配置中心

**第一步：添加 Apollo 客户端的 Maven 依赖**

```xml
<dependency>
    <groupId>com.ctrip.framework.apollo</groupId>
    <artifactId>apollo-client</artifactId>
    <version>1.7.0</version>
</dependency>
```

**第二步，启动 Apollo**

相关文档在 https://github.com/ctripcorp/apollo/wiki/Quick-Start

**第三步，在 jboot.properties 添加如下配置**

```
jboot.config.apollo.enable = true
jboot.config.apollo.appId = SampleApp
jboot.config.apollo.meta = http://106.54.227.205:8080
```


## 配置内容加密解密

为了安全起见，我们需要对配置里的一些内容进行加密，比如数据库的账号、密码等，防止 web 服务器被黑客入侵时保证数据库的安全。

配置的内容加密是由用户自己编写加密算法。此时，Jboot 读取的只是加密的内容，为了能正常还原解密之后的内容，用户需要给 `JbootConfigManager` 配置上解密的实现 JbootConfigDecryptor。

一般情况下，我们需要在 JbootAppListener 的 `onInit()` 里去配置。例如：

```java
public MyApplicationListener implements JbootAppListener {

    public void onInit() {
        JbootConfigManager.me().setDecryptor(new MyConfigDecriptor());
    }

}
```

我们需要在 `MyConfigDecriptor` 的 `decrypt` 方法里去实现自己的解密算法。例如：

```java
public MyConfigDecriptor implements JbootConfigDecryptor {

    public String decrypt(String key, String originalContent){
        //在这里实现你自己的解密算法
        //key : 很多时候我们并不是针对所有的配置都进行加密，只是加密了个别配置
        //此时，我们可以通过 key 来判断那些无需加密的内容，不需要加密直接返回 originalContent 即可
    }
}
```



## 常见问题

1、如何设置启动参数 ？
> 答：在 fatjar 模式下，可以通过添加 `--`（两个中划线） 来指定配置，例如：java -jar --undertow.port=8080  --undertow.host=0.0.0.0


2、如何设置 Jvm 系统属性 ？
> 答：和启动参数一样，只需要把 `--` 换成 `-D`，例如： java -jar -Dundertow.port=8080 -Dundertow.host=0.0.0.0


3、如何设置系统环境变量 ？
> 答：在 Docker 下，启动 Docker 容器的时候，只需要添加 -e 参数即可，例如： `docker run -e undertow.port=8080 xxxx`
> Linux、Window、Mac 搜索引擎自行搜索关键字： `环境变量配置`

注意：在设置的系统环境变量的key、value中，例如：jboot.app.mode = dev 可以修改为 JBOOT_APP_MODE  = dev ，其他同理把全部小写
修改为大写，符号点（.）修改为下划线（_）。


## RPC 配置

参考 [这里](./rpc.md) 

## Jboot 其他配置参考

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

jboot.web.webSocketEndpoint
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


jboot.datasource.name
jboot.datasource.type
jboot.datasource.url
jboot.datasource.user
jboot.datasource.password
jboot.datasource.driverClassName = com.mysql.jdbc.Driver
jboot.datasource.connectionInitSql
jboot.datasource.poolName
jboot.datasource.cachePrepStmts = true
jboot.datasource.prepStmtCacheSize = 500
jboot.datasource.prepStmtCacheSqlLimit = 2048
jboot.datasource.maximumPoolSize = 10
jboot.datasource.maxLifetime
jboot.datasource.idleTimeout
jboot.datasource.minimumIdle = 0
jboot.datasource.sqlTemplatePath
jboot.datasource.sqlTemplate
jboot.datasource.factory
jboot.datasource.shardingConfigYaml
jboot.datasource.dbProFactory
jboot.datasource.containerFactory
jboot.datasource.transactionLevel
jboot.datasource.table //此数据源包含哪些表
jboot.datasource.exTable //该数据源排除哪些表
jboot.datasource.dialectClass
jboot.datasource.activeRecordPluginClass
jboot.datasource.needAddMapping = true //是否需要添加到映射，当不添加映射的时候，只能通过 model.use("xxx").save()这种方式去调用该数据源



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


jboot.limit.enable
jboot.limit.rule
jboot.limit.fallbackProcesser
jboot.limit.defaultHttpCode
jboot.limit.defaultAjaxContent
jboot.limit.defaultHtmlView
```

