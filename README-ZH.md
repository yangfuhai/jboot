![](./docs/files/logo.png)
## [English document](./README.md)
## JBoot 是什么

Jboot，专为大型分布式项目和微服务而生。

QQ交流群： 601440615

## maven 依赖

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>1.3.6</version>
</dependency>

```
注意：有某些时候，Jboot版本已经更新，但是文档没有更新的情况下，请自行查看maven中央仓库最新的版本。

## Jboot的核心组件

* [x] MVC （基于jfinal）
* [x] ORM （基于jfinal）
* [x] AOP （基于guice）
* 安全控制
    * [x] shiro
    * [x] jwt
* RPC远程调用 
    * [x] motan
    * [x] dubbo
    * [ ] grpc
    * [x] zbus
* MQ消息队列 
    * [x] rabbitmq
    * [x] redismq
    * [x] 阿里云商业MQ
    * [ ] activemq
    * [x] zbus
* 缓存
    * [x] ehcache
    * [x] redis
    * [x] 分布式二级缓存ehredis
    * [x] J2Cache ([https://gitee.com/ld/J2Cache](https://gitee.com/ld/J2Cache))
* [x] 分布式session
* [x] 分布式锁
* 任务调度
    * [x] cron4j
    * [x] ScheduledThreadPoolExecutor
    * [x] 分布式任务调度
* [x] 调用监控 (基于metrics)
* [x] 限流、降级、熔断机制（基于hystrix）
* [x] Opentracing数据追踪
    * [x] zipkin
    * [x] skywalking
* [x] 统一配置中心
* [x] swagger api
* [x] Http客户端（包含了get、post请求，文件上传和下载等）
    * [x] httpUrlConnection
    * [x] okHttp
    * [ ] httpClient
* [x] 分布式下的微信和微信第三方
* [x] 自定义序列化组件
* [x] 事件机制
* [x] 代码生成器


## 文档

文档URL地址 ： [点击这里](./DOC.md)

#### 文档目录

- [JBoot核心组件](./DOC.md#jboot核心组件)
- [MVC](./DOC.md#mvc)
- [安全控制](./DOC.md#安全控制)
- [ORM](./DOC.md#orm)
- [AOP](./DOC.md#aop)
- [RPC](./DOC.md#rpc远程调用)
- [MQ](./DOC.md#mq消息队列)
- [Cache](./DOC.md#cache缓存)
- [http客户端](./DOC.md#http客户端)
- [metrics数据监控](./DOC.md#metrics数据监控)
- [容错与隔离](./DOC.md#容错与隔离)
- [Opentracing数据追踪](./DOC.md#opentracing数据追踪)	
- [统一配置中心](./DOC.md#统一配置中心)	
- [Swagger api](./DOC.md#swagger-api自动生成)
- 其他
	- [SPI扩展](./DOC.md#spi扩展)
	- [JbootEvnet事件机制](./DOC.md#jbootEvnet事件机制)
	- [配置文件](./DOC.md#配置文件)
	- [代码生成器](./DOC.md#代码生成器)
- [项目构建](./DOC.md#项目构建)
- [联系作者](./DOC.md#联系作者)
- [常见问题](./DOC.md#常见问题)


## Contributors
* Michael Yang（EMAIL:fuhai999@gmail.com，GITHUB:[@yangfuhai](https://github.com/yangfuhai)，QQ:1506615067，WECHAT：wx198819880)
* Rlax（EMAIL:popkids@qq.com，GITHUB:[@pkanyue](https://github.com/pkanyue)，QQ:441420519，WECHAT:RlaxUC)
* 徐海峰（EMAIL:xhf6731202@126.com，GITHUB:[@xhf6731202](https://github.com/xhf6731202)，QQ:27533892，WECHAT:atm27533892）
* 周洛熙 (EMAIL:78793093@qq.com，GITHUB:[@zhoufengjob](https://github.com/zhoufengjob)，QQ:78793093)
* lsup (EMAIL:egox.vip@gmail.com，GITHUB:[@lsup](https://github.com/lsup))

## QQ群

Jboot交流QQ群：601440615 ，欢迎加入讨论Jboot和微服务。

