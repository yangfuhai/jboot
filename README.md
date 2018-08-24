## website

http://jboot.io

## [中文文档](./README-ZH.md)
## what is JBoot 

Jboot，she is born for distributed project and micro service.

QQ-Group： 601440615

Jboot Demos ： https://gitee.com/fuhai/jboot/tree/master/src/test/java

## Jboot maven dependency

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>1.6.4</version>
</dependency>

```

## Jboot core component

* [x] MVC （base on JFinal）
* [x] ORM （base on JFinal）
* [x] AOP （base on Guice）
* security control
    * [x] shiro
    * [x] jwt
* RPC (Remote Procedure Call) 
    * [x] motan
    * [x] dubbo
    * [ ] grpc
    * [x] zbus
* MQ 
    * [x] rabbitmq
    * [x] redismq
    * [x] Aliyun MQ
    * [ ] activemq
    * [x] zbus
* cache
    * [x] ehcache
    * [x] redis
    * [x] ehredis (tow level distributed cache)
    * [x] J2Cache ([https://gitee.com/ld/J2Cache](https://gitee.com/ld/J2Cache))
* [x] distributed session
* [x] distributed lock
* task schedule
    * [x] cron4j
    * [x] ScheduledThreadPoolExecutor
    * [x] distributed task schedule (base on redis)
* [x] component procedure monitor (base on metrics)
* [x] Hystrix control
* [x] Opentracing
    * [x] zipkin
    * [x] skywalking
* [x] distributed config center
* [x] swagger api
* [x] HttpClient
    * [x] httpUrlConnection
    * [x] okHttp
    * [ ] httpClient
* [x] wechat api
* [x] serialization component 
* [x] event mechanism
* [x] code generator


## document

document url ： [click here](./DOC.md)

#### document directories 

- [JBoot core component](./DOC.md#jboot核心组件)
- [MVC](./DOC.md#mvc)
- [security control](./DOC.md#安全控制)
- [ORM](./DOC.md#orm)
- [AOP](./DOC.md#aop)
- [RPC](./DOC.md#rpc远程调用)
- [MQ](./DOC.md#mq消息队列)
- [Cache](./DOC.md#cache缓存)
- [http Client](./DOC.md#http客户端)
- [metrics monitor](./DOC.md#metrics数据监控)
- [Hystrix control](./DOC.md#容错与隔离)
- [Opentracing](./DOC.md#opentracing数据追踪)	
- [distributed config center](./DOC.md#统一配置中心)	
- [Swagger api](./DOC.md#swagger-api自动生成)
- Other
	- [SPI](./DOC.md#spi扩展)
	- [JbootEvnet](./DOC.md#jbootEvnet事件机制)
	- [properties reader](./DOC.md#配置文件)
	- [code generator](./DOC.md#代码生成器)
- [project build](./DOC.md#项目构建)
- [contact author](./DOC.md#联系作者)
- [FAQ](./DOC.md#常见问题)


## Contributors
* Michael Yang（EMAIL:fuhai999@gmail.com，GITHUB:[@yangfuhai](https://github.com/yangfuhai))
* Rlax（EMAIL:popkids@qq.com，GITHUB:[@pkanyue](https://github.com/pkanyue))
* 徐海峰（EMAIL:xhf6731202@126.com，GITHUB:[@xhf6731202](https://github.com/xhf6731202)）
* 周洛熙 (EMAIL:78793093@qq.com，GITHUB:[@zhoufengjob](https://github.com/zhoufengjob))
* lsup (EMAIL:egox.vip@gmail.com，GITHUB:[@lsup](https://github.com/lsup))



