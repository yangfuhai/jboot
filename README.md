

## JBoot 是什么

Jboot，专为大型分布式项目和微服务而生。

JBoot并不是一个新的发明，而是一个整理了大型分布式常用的技术解决方案，而形成的一个"最佳实践"。

例如：

* JBoot的RPC的通过新浪开源的成熟的框架motan（支持其他实现方案grpc、dubbo等）来实现的；
* ORM + MVC 是通过著名的JFinal来实现的；
* 缓存部分则是通过 EHcache 和 Redis 来实现的；
* 容错和隔离则是通过Netflix公司的Hystrix来实现的等等。

QQ交流群： 601440615

## 开始第一个Jboot应用
### 1、添加 maven 依赖

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>1.0-rc.3</version>
</dependency>

```
注意：有某些时候，Jboot版本已经更新，但是文档没有更新的情况下，请自行查看maven中央仓库最新的版本。

### 2、编写控制器 HelloController

```java
@RequestMapping("/")
public class HelloController extend JbootController{
   public void index(){
        renderText("hello jboot");
   }
}
```

### 3、启动应用

```java
public class MyStarter{
   public static void main(String [] args){
       Jboot.run(args);
   }
}
```

### 4、浏览器访问

* 访问网址：http://127.0.0.1:8080
* 浏览器显示： hello jboot

## Jboot的核心组件
通过以上几个步骤，我们就能完成一个Jboot应用的demo实例。然而在大型的分布式应用中，这些远远不够。因此，Jboot还提供了在分布式应用常用的分布式组件。

* MVC
* 安全控制 （基于shiro）
* ORM 
* AOP （基于guice）
* RPC远程调用 （支持可选方案有 motan，dubbo等）
* MQ消息队列 (支持可选方案有：redis、activemq，rabbitmq等)
* 分布式缓存
* 分布式session
* 调用监控 (基于metrics)
* 容错隔离（基于 hystrix）
* 轻量级的Http客户端（包含了get、post请求，文件上传和下载等）
* 分布式下的微信和微信第三方
* 自定义序列化组件
* 事件机制
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
- [监控](./DOC.md#监控)
	- 添加metrics数据
	- metrics与Ganglia
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

