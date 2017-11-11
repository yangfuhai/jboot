

## JBoot 是什么

Jboot，专为大型分布式项目和微服务而生。

QQ交流群： 601440615

## maven 依赖

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>1.0-rc.4</version>
</dependency>

```
注意：有某些时候，Jboot版本已经更新，但是文档没有更新的情况下，请自行查看maven中央仓库最新的版本。

## Jboot的核心组件

* MVC （基于jfinal）
* Shiro安全控制 
* ORM  （基于jfinal）
* AOP （基于guice）
* RPC远程调用 （支持可选方案有 motan，dubbo等）
* MQ消息队列 (支持可选方案有：redis、activemq，rabbitmq等)
* 分布式缓存
* 分布式session
* 分布式锁
* 调用监控 (基于metrics)
* 容错隔离（基于 hystrix）
* Opentracing数据追踪
* 统一配置中心
* swagger api
* 轻量级的Http客户端（包含了get、post请求，文件上传和下载等）
* 分布式下的微信和微信第三方
* 自定义序列化组件
* 事件机制
* 代码生成器
* 等等

## 文档

文档URL地址 ： [点击这里](./DOC.md)

#### 文档目录

- [JBoot核心组件](./DOC.md#jboot核心组件)
- [MVC](./DOC.md#mvc)
	- MVC的概念
	- JbootController
	- @RquestMapping
		- 使用@RquestMapping
		- render
	- session 与 分布式session
- [安全控制](./DOC.md#安全控制)
	- shiro简介
	- shiro的配置
	- shiro的使用
		- 12个模板指令（用在html上）
		- 5个Requires注解功能（用在Controller上）
- [ORM](./DOC.md#orm)
	- 配置
		- 高级配置
	- Model
	- @Table注解
	- Record
	- DAO
	- 多数据源
	- 分库和分表
		- 分库
		- 分表
- [AOP](./DOC.md#aop)
	- Google Guice
	- @Inject
	- @Bean
- [RPC远程调用](./DOC.md#rpc远程调用)
	- 使用步骤
	- 其他注意
- [MQ消息队列](./DOC.md#mq消息队列)
	- 使用步骤
	- RedisMQ
	- ActiveMQ
	- RabbitMq
	- 阿里云商业MQ
- [Cache缓存](./DOC.md#cache缓存)
	- 使用步骤
	- 注意事项
	- ehcache
	- redis
	- ehredis
- [http客户端](./DOC.md#http客户端)
	- Get请求
	- Post 请求
	- 文件上传
	- 文件下载
- [metrics数据监控](./DOC.md#metrics数据监控)
	- 添加metrics数据
	- metrics与Ganglia
	- metrics与grafana
	- metrics与jmx
- [容错与隔离](./DOC.md#容错与隔离)
	- hystrix配置
	- Hystrix Dashboard 部署
	- 通过 Hystrix Dashboard 查看数据
	
- [Opentracing数据追踪](./DOC.md#opentracing数据追踪)
	- [Opentracing简介](./DOC.md#opentracing简介)
	- [Opentracing在Jboot上的配置](./DOC.md#opentracing在jboot上的配置)
	- [Zipkin](./DOC.md#zipkin)
		- [Zipkin快速启动](./DOC.md#zipkin快速启动)
		- [使用zipkin](./DOC.md#使用zipkin)
	- SkyWalking
		- [SkyWalking快速启动](./DOC.md#skywalking快速启动)
		- [使用SkyWalking](./DOC.md#使用skywalking)
	- 其他
	
- [统一配置中心](./DOC.md#统一配置中心)
	- [部署统一配置中心服务器](./DOC.md#部署统一配置中心服务器)
	- [连接统一配置中心](./DOC.md#连接统一配置中心)

	
- [Swagger api自动生成](./DOC.md#swagger-api自动生成)
	- [swagger简介](./DOC.md#swagger简介)
	- [swagger使用](./DOC.md#swagger使用)
	- [5个swagger注解](./DOC.md#swagger使用)

- 其他
	- [SPI扩展](./DOC.md#spi扩展)
	- [JbootEvnet事件机制](./DOC.md#jbootEvnet事件机制)
	- 自定义序列化
	- 配置文件
	- 代码生成器
- [项目构建](./DOC.md#项目构建)
- 鸣谢
- [联系作者](./DOC.md#联系作者)
- [常见问题](./DOC.md#常见问题)
	- 使用Jboot后还能自定义JfinalConfig等配置文件吗？


## 联系作者
* qq:1506615067
* wechat：wx198819880
* email:fuhai999#gmail.com

## QQ群

Jboot交流QQ群：601440615 ，欢迎加入讨论Jboot和微服务。

