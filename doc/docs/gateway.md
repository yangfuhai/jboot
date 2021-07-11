# 网关

## 目录

- 概述
- path路由
- host路由
- query路由
- 多个 Gateway 配置
- 服务发现
- 注意事项


## 概述

Jboot 已经内置基础的网关，网关功能目前暂时只能通过在 jboot.properties 文件进行配置。

如下是一个正常的 gateway 配置。

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true


jboot.gateway.uriHealthCheckEnable = true
jboot.gateway.uriHealthCheckPath = /your-health-check-path

jboot.gateway.sentinelEnable = false
jboot.gateway.sentinelBlockPage = /block
jboot.gateway.sentinelBlockJsonMap = message:xxxx;code:200

jboot.gateway.proxyReadTimeout = 10000
jboot.gateway.proxyConnectTimeout = 5000
jboot.gateway.proxyContentType = text/html;charset=utf-8

jboot.gateway.interceptors = com.xxx.Interceptor1,com.xxx.Interceptor2
jboot.gateway.loadBalanceStrategy = com.xxx.loadBalanceStrategy1

jboot.gateway.pathEquals = /path
jboot.gateway.pathContains = /path
jboot.gateway.pathStartsWith = /path
jboot.gateway.pathEndswith = /path

jboot.gateway.hostEquals = xxx.com
jboot.gateway.hostContains = xxx.com
jboot.gateway.hostStartsWith = xxx.com
jboot.gateway.hostEndswith = xxx.com

jboot.gateway.queryEquals = aa:bb,cc:dd
jboot.gateway.queryContains = aa,bb
```



- name 设置路由的名称
- uri 设置路由目标网址，可以配置多个 uri，多个 uri 用英文逗号（,） 隔开，当有多个 uri 的时候，系统会 **随机** 使用其中一个去访问
- enable 是否启用该路由
- uriHealthCheckEnable 是否启用健康检查功能
- uriHealthCheckPath URI 健康检查路径，当配置 uriHealthCheckPath 后，健康检查的 url 地址为 uri + uriHealthCheckPath，当健康检查目标网址的 http code 为 200 时，表示健康状态，否则为非健康状态。
- sentinelEnable 是否启用 sentinel 限流功能
- sentinelBlockPage 若该路由被限流后，网页自动跳转到哪个网址
- sentinelBlockJsonMap 若该路由被限流后，自动渲染的 jsonMap，若 sentinelBlockPage 已经配置，则 sentinelBlockJsonMap 配置无效
- proxyReadTimeout 发生路由后，默认的请求超时时间，默认为 10 秒
- proxyConnectTimeout 发生路由后，默认的连接超时时间，默认为 5 秒
- proxyContentType 发生路由后，返回给浏览器的 http-content-type，默认为：text/html;charset=utf-8
- interceptors 网关拦截器，一般用于进行鉴权等功能，配置类名，多个拦截器用英文逗号隔开，拦截器必须实现 GatewayInterceptor 接口
- loadBalanceStrategy 负载均衡策略，当配置了多个 uri 的时候，可以通过此策略对 uri 进行获取

> 注意：开启健康检查后，当所有的目标地址都不健康的时候，会渲染 "none health url in gateway" 的错误信息。
> 我们可以通过 `JbootGatewayManager.me().setNoneHealthUrlErrorRender()` 来自定义渲染功能。

## Path 路由

Path 路由一般是最常用的路由之一，是根据域名之后的路径进行路由的，Jboot 对 Path 路由提供了 4 中方式：

**1、pathEquals**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathEquals = /user
```

当用户访问 www.xxx.com/user 的时候，自动路由到 `http://youdomain:8080/user`，但是当用户请求 `www.xxx.com/user/other` 的时候不会进行路由。

**2、pathContains**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathContains = /user
```

当 path 中，只要存在 `/user` 就会匹配到该路由，比如 `www.xxx.com/user/other` 或者 `www.xxx.com/other/user/xxx` 都会匹配到。


**3、pathStartsWith**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathStartsWith = /user
```

当 path 中，只要以 `/user` 开头就会匹配到该路由，比如 `www.xxx.com/user/other` ，但是 `www.xxx.com/other/user/xxx` 不会匹配到，因为它是以 `/other` 开头的。

**4、pathEndsWith**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathEndsWith = /user
```

当 path 中，只要以 `/user` 结束就会匹配到该路由，比如 `www.xxx.com/other/user` ，但是 `www.xxx.com/user/other` 不会匹配到，因为它是以 `/other` 结束的。


## Host 路由

Host 路由是根据域名进行路由的，Jboot 对 Host 路由提供了 4 中方式：

**1、hostEquals**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.hostEquals = xxx.xxx.com
```

当用户访问 xxx.xxx.com/user/xx 的时候，自动路由到 `http://youdomain:8080/user/xx`，但是当用户请求 `www.xxx.com/user/xxx` 的时候不会进行路由。

**2、hostContains**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.hostContains = xxx.xxx.com
```

在 Host 中，只要存在 `xxx.xxx.com` 就会匹配到该路由，比如 `aaa.bbb.xxx.xxx.com/user/other` 会自动路由到 `http://youdomain:8080/user/other`。


**3、hostStartsWith**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.hostStartsWith = xxx
```

在 Host 中，只要以 `xxx` 开头就会匹配到该路由，比如 `xxx.xxx.com/user/other` ，但是 `www.xxx.com/other/user/xxx` 不会匹配到，因为它的域名（host）是以 `xxx` 开头的。

**4、hostEndsWith**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.hostEndsWith = com
```

在 Host 中，只要以 `com` 结束就会匹配到该路由，比如 `www.xxx.com/other/user` ，但是 `www.xxx.org/user/other` 不会匹配到，因为它的域名是以 `org` 结束的。

## Query 路由

根据 get 请求的参数进行路由，注意 post 请求参数不会路由。


**1、queryEquals**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.queryEquals = aaa:bbb
```

以上配置中，如果用户访问 `www.xxx.com/controller?aaa=bbb` 会自动路由到 `http://youdomain:8080/controller?aaa=bbb` ，但是如果用户访问 `www.xxx.com/controller?aaa=ccc`不会路由。

**2、queryContains**

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.queryContains = aaa
```

以上配置中，如果用户访问 `www.xxx.com/controller?aaa=bbb` 会自动路由到 `http://youdomain:8080/controller?aaa=bbb` ，或者用户访问  `www.xxx.com/controller?aaa=ccc` 也会路由到 `http://youdomain:8080/controller?aaa=ccc`，因为 query 都包含了 `aaa=**` 的请求，但是如果用户访问 `www.xxx.com/controller?other=aaa`不会路由。



## 多个 Gateway 配置

```properties
jboot.gateway.aaa.name = name
jboot.gateway.aaa.uri = http://youdomain:8080
jboot.gateway.aaa.enable = true
jboot.gateway.aaa.sentinelEnable = false
jboot.gateway.aaa.sentinelBlockPage = /block
jboot.gateway.aaa.proxyReadTimeout = 10000
jboot.gateway.aaa.proxyConnectTimeout = 5000
jboot.gateway.aaa.proxyContentType = text/html;charset=utf-8
jboot.gateway.aaa.interceptors = com.xxx.Interceptor1,com.xxx.Interceptor2
jboot.gateway.aaa.pathEquals = /path
jboot.gateway.aaa.pathContains = /path
jboot.gateway.aaa.pathStartsWith = /path
jboot.gateway.aaa.pathEndswith = /path
jboot.gateway.aaa.hostEquals = xxx.com
jboot.gateway.aaa.hostContains = xxx.com
jboot.gateway.aaa.hostStartsWith = xxx.com
jboot.gateway.aaa.hostEndswith = xxx.com
jboot.gateway.aaa.queryEquals = aa:bb,cc:dd
jboot.gateway.aaa.queryContains = aa,bb


jboot.gateway.bbb.name = name
jboot.gateway.bbb.uri = http://youdomain:8080
jboot.gateway.bbb.enable = true
jboot.gateway.bbb.sentinelEnable = false
jboot.gateway.bbb.sentinelBlockPage = /block
jboot.gateway.bbb.proxyReadTimeout = 10000
jboot.gateway.bbb.proxyConnectTimeout = 5000
jboot.gateway.bbb.proxyContentType = text/html;charset=utf-8
jboot.gateway.bbb.interceptors = com.xxx.Interceptor1,com.xxx.Interceptor2
jboot.gateway.bbb.pathEquals = /path
jboot.gateway.bbb.pathContains = /path
jboot.gateway.bbb.pathStartsWith = /path
jboot.gateway.bbb.pathEndswith = /path
jboot.gateway.bbb.hostEquals = xxx.com
jboot.gateway.bbb.hostContains = xxx.com
jboot.gateway.bbb.hostStartsWith = xxx.com
jboot.gateway.bbb.hostEndswith = xxx.com
jboot.gateway.bbb.queryEquals = aa:bb,cc:dd
jboot.gateway.bbb.queryContains = aa,bb


jboot.gateway.xxx.name = name
jboot.gateway.xxx.uri = http://youdomain:8080
jboot.gateway.xxx.enable = true
jboot.gateway.xxx.sentinelEnable = false
jboot.gateway.xxx.sentinelBlockPage = /block
jboot.gateway.xxx.proxyReadTimeout = 10000
jboot.gateway.xxx.proxyConnectTimeout = 5000
jboot.gateway.xxx.proxyContentType = text/html;charset=utf-8
jboot.gateway.xxx.interceptors = com.xxx.Interceptor1,com.xxx.Interceptor2
jboot.gateway.xxx.pathEquals = /path
jboot.gateway.xxx.pathContains = /path
jboot.gateway.xxx.pathStartsWith = /path
jboot.gateway.xxx.pathEndswith = /path
jboot.gateway.xxx.hostEquals = xxx.com
jboot.gateway.xxx.hostContains = xxx.com
jboot.gateway.xxx.hostStartsWith = xxx.com
jboot.gateway.xxx.hostEndswith = xxx.com
jboot.gateway.xxx.queryEquals = aa:bb,cc:dd
jboot.gateway.xxx.queryContains = aa,bb
```

## 服务发现

Jboot Gateway 功能通过 Nacos（可以通过 SPI 进行扩展其他事项方式） 实现了自动发现服务。

相关代码示例可以参考：

https://gitee.com/JbootProjects/jboot/tree/master/simples/gateway


使用方法如下：

1、新增 nacos 依赖（Gateway 端和服务端都需要）

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <version>版本号</version>
</dependency>
```

2、启动 Nacos

参考文档：

https://nacos.io/zh-cn/docs/quick-start.html


3、在 Gateway 网关端添加如下配置：

```properties
jboot.gateway.name = myName
jboot.gateway.enable = true
jboot.gateway.pathStartsWith = /


jboot.gateway.discovery.enable = true

#若配置其他，则自行通过 SPI 进行扩展
jboot.gateway.discovery.type = nacos

#默认值为：DEFAULT_GROUP
jboot.gateway.discovery.group =

jboot.gateway.discovery.nacos.serverAddr = 127.0.0.1:8848
```

更多的 nacos 配置如下：
```properties
jboot.gateway.discovery.nacos.isUseCloudNamespaceParsing = xxx
jboot.gateway.discovery.nacos.isUseEndpointParsingRule = xxx
jboot.gateway.discovery.nacos.endpoint = xxx
jboot.gateway.discovery.nacos.endpointPort = xxx
jboot.gateway.discovery.nacos.namespace = xxx
jboot.gateway.discovery.nacos.username = xxx
jboot.gateway.discovery.nacos.password = xxx
jboot.gateway.discovery.nacos.accessKey = xxx
jboot.gateway.discovery.nacos.secretKey = xxx
jboot.gateway.discovery.nacos.ramRoleName = xxx
jboot.gateway.discovery.nacos.contextPath = xxx
jboot.gateway.discovery.nacos.clusterName = xxx
jboot.gateway.discovery.nacos.encode = xxx
jboot.gateway.discovery.nacos.configLongPollTimeout = xxx
jboot.gateway.discovery.nacos.configRetryTime = xxx
jboot.gateway.discovery.nacos.maxRetry = xxx
jboot.gateway.discovery.nacos.enableRemoteSyncConfig = xxx
```

4、在 服务端 添加如下配置

```properties
jboot.gateway.discovery.enable = true

#若配置其他，则自行通过 SPI 进行扩展
jboot.gateway.discovery.type = nacos

#默认值为：DEFAULT_GROUP，这个值必须和 gateway 配置的一致
jboot.gateway.discovery.group =

## 注意：这个配置的 myName 必须和 Gateway 里的 'jboot.gateway.name = myName' 中的 myName 一样
jboot.gateway.instance.name = myName

#不配置默认为 http,可以配置为 https
jboot.gateway.instance.uriScheme = http

#不配置默认为当前服务器的IP地址，可以获取错误
jboot.gateway.instance.uriHost = 

#默认为 undertow.port 的配置
jboot.gateway.instance.uriPort = http

jboot.gateway.instance.uriPath = /user/aaa

jboot.gateway.discovery.nacos.serverAddr = 127.0.0.1:8848
```

更多的 nacos 配置如下：
```properties
jboot.gateway.discovery.nacos.isUseCloudNamespaceParsing = xxx
jboot.gateway.discovery.nacos.isUseEndpointParsingRule = xxx
jboot.gateway.discovery.nacos.endpoint = xxx
jboot.gateway.discovery.nacos.endpointPort = xxx
jboot.gateway.discovery.nacos.namespace = xxx
jboot.gateway.discovery.nacos.username = xxx
jboot.gateway.discovery.nacos.password = xxx
jboot.gateway.discovery.nacos.accessKey = xxx
jboot.gateway.discovery.nacos.secretKey = xxx
jboot.gateway.discovery.nacos.ramRoleName = xxx
jboot.gateway.discovery.nacos.contextPath = xxx
jboot.gateway.discovery.nacos.clusterName = xxx
jboot.gateway.discovery.nacos.encode = xxx
jboot.gateway.discovery.nacos.configLongPollTimeout = xxx
jboot.gateway.discovery.nacos.configRetryTime = xxx
jboot.gateway.discovery.nacos.maxRetry = xxx
jboot.gateway.discovery.nacos.enableRemoteSyncConfig = xxx
```

## 注意事项
当配置中，如果一个内容存在多个值的时候，需要用英文逗号（,）隔开。

比如:

```properties
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathContains = /user,/article
```

当 path 中，只要存在 `/user`  或者 存在 `/article` 都会匹配到该路由，比如 `www.xxx.com/user/xxx` 或者 `www.xxx.com/article/xxx` 都会匹配到。

其他同理。