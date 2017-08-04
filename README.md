

# <center>欢迎使用JBoot</center>


# JBoot 简介

## JBoot 是什么

JBoot 定位是一个大型的分布式WEB应用开发框架。

JBoot并不是一个新的发明，而是一个整理了大型分布式常用的技术解决方案，而形成的一个"最佳实践"。例如：JBoot的RPC的通过新浪开源的成熟的框架motan来实现的；针对ORM + MVC 是通过注明的JFinal来实现的；缓存部分则是通过 EHcache 和 Redis 来实现的；容错和隔离则是通过Netflix公司的Hystrix来实现的等等。

QQ交流群： 601440615

## 开始
### 添加 maven 依赖

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>1.0-beta3</version>
</dependency>

```
注意：有某些时候，Jboot版本已经更新，但是文档没有更新的情况下，请自行查看maven中央仓库最新的版本。

### 编写控制器 HelloController

```java
@RequestMapping("/")
public class HelloController extend JbootController{
   public void index(){
        renderText("hello jboot");
   }
}
```

### 启动应用

```java
public class MyStarter{
   public static void main(String [] args){
       Jboot.run(args);
   }
}
```

### 浏览器访问

* 访问网址：http://127.0.0.1:8080
* 浏览器显示： hello jboot

### 其他核心组件
通过以上几个步骤，我们就能完成一个Jboot应用的demo实例。然而在大型的分布式应用中，这些远远不够。因此，Jboot还提供了在分布式应用常用的分布式组件。

* MVC
* 安全控制 
* ORM 
* AOP
* RPC远程调用
* MQ消息队列
* 分布式缓存
* 分布式session
* 调用监控
* 容错隔离
* 轻量级的Http客户端
* 分布式下的微信和微信第三方
* 自定义序列化组件
* 事件机制
* 等等

### 更多

请查看[JBoot文档](./DOC.md)


# 联系作者
* qq:1506615067
* wechat：wx198819880
* email:fuhai999#gmail.com

