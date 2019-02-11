# 限流

## 目录

- 限流的场景
- 限流的类型
- 限流的使用
- 限流的实现

## 限流的场景

在应用的开发中，我们经常会遇到这样的一些场景，例如：
- 秒杀
- 抢红包

等等情况，这些业务都有一个明显的特征：并发量非常高。倘若没做好限流，往往会造成系统崩溃的情况。

## 限流的类型

在 Jboot 中，我们可以对某个url请求进行限流，也可以对某个`java方法`进行限流。

Jboot 提供了两种方案：

- TOKEN BUCKET ： 令牌桶，它可以配置1秒钟内，允许执行（或请求）多少次。
- CONCURRENCY ： 并发量，可以配置为某个url地址或者某个方法名的并发量支持多少。

## 限流的使用

**使用方案1：通过配置来实现**

在 `jboot.properties` 文件中定义如下：
```
jboot.limit.enable = true
jboot.limit.rule = /user*:tb:1,io.jboot.aop*.get*(*):tb:1
```

- jboot.limit.enable : 限流功能的开关
- jboot.limit.rule : 限流规则

> 规则说明：
> - 1、可以配置多个规则，每个规则用英文逗号隔开
> - 2、规则分为三个部分，用冒号（:）隔开，分别是：资源、限流类型、限流参数值
> - 3、限流的类型有2种、分别是：tb 和 cc。tb：TOKEN BUCKET（令牌桶），cc：CONCURRENCY（并发量）
> - 4、星号（*）匹配任意字符，也可以是空字符。

在以上配置中，配置了两个规则，分别是：

- `/user*:tb:1`
- `io.jboot.aop*.get*(*):cc:1`

第一个规则：匹配 `/user` 开头的所有url地址，每个 url 地址，1秒钟之内只允许访问1次。
第二个规则：匹配 `io.jboot.aop` 开头的所有包名，并且 `get` 开头的所有任意参数的方法。并发量为 1。

**使用方案2：通过注解 `@EnableLimit`**

例如：

```java
@RequestMapping("/")
public class IndexController extends JbootController {

    @EnableLimit(rate = 1,fallback = "fallbackMethod")
    public void index() {
        renderText("index...." );
    }

    public void fallbackMethod(){
        renderText("fallback...");
    }
}
```
通过使用 `@EnableLimit(rate = 1,fallback = "fallbackMethod")` 在方法 `index()` 方法配置后，当用户访问：`http://127.0.0.1:8080`时，1秒钟内只有一次访问到 `index()` 方法，若有多次访问后，自动调用降级放方法 `fallbackMethod()` 执行。

`@EnableLimit` 支持的配置如下：

- resource ： 资源名称（不配置的时候默认为方法名，Controller 默认为对应的 url 映射）
- type：限流的类型，默认为令牌桶。
- rate：限流的数值，必须配置
- fallback：降级方法，若配置，此方法必须在当前的类下定义。


## 限流的实现
上文中提到，Jboot 提供了两种限流类型，他们分别是：
- TOKEN BUCKET ： 令牌桶
- CONCURRENCY ： 并发量

TOKEN BUCKET 令牌桶，是通过 Google Guava 的 RateLimiter 来实现的。

CONCURRENCY 并发量，是通过 `Semaphore` 实现的，具体代码实现请查看 LimiterInterceptor 。



