

## JBoot 是什么

Jboot，专为大型分布式项目和微服务而生。

QQ交流群： 601440615

## maven 依赖

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>1.6.7</version>
</dependency>

```
**注意：** 很多些时候，Jboot版本已经更新，但是文档没有更新的情况下，请自行查看jboot的maven中央仓库最新版本。

## Jboot的核心组件

* [x] MVC （基于jfinal）
* [x] ORM （基于jfinal）
* [x] AOP （基于guice）
* 安全控制
    * [x] shiro
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
* 等等
