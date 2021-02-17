# MVC


## 目录

- 描述
- Controller : 控制器
- Action ：请求的基本单位
- Interceptor ： 拦截器
- Handler ： 处理器
- Render ：渲染器
- Session 
- Cookie



## 描述
由于 Jboot 是基于 JFinal 进行二次开发的，因此 Jboot 的 MVC 相关功能是来至于 JFinal 提供的功能。

JFinal 的相关文档： [https://www.jfinal.com/doc/3-1](https://www.jfinal.com/doc/3-1)


## Controller ：控制器

 Controller 是 JFinal 的核心类之一，是 MVC 设计模式中的控制器。基于 Jboot 开发的控制器需要继承 Controller。Controller 也是定义 Action 方法的地点，一个 Controller 可以包含多个 Action 。

 另外，JbootController 扩展了 JFinal 的 Controller 类，增加了 Jwt、FlashMessage 和 其他一些实用的方法。

 建议基于 Jboot 开发的应用，都继承至 JbootController。（备注：只是建议、而非必须。）


 ## Action

Action 的相关文档请参考： [https://www.jfinal.com/doc/3-2](https://www.jfinal.com/doc/3-2) 

## Interceptor ： 拦截器

Interceptor 拦截器的相关文档请参考 [https://www.jfinal.com/doc/4-2](https://www.jfinal.com/doc/4-2) 


## Handler ： 处理器

Handler  处理器是 JFinal 的核心，用于预先处理 Web 的所有请求，其架构可以参考 [https://www.jfinal.com/doc/13-2](https://www.jfinal.com/doc/13-2) ，用法参考：[https://www.jfinal.com/doc/2-7](https://www.jfinal.com/doc/2-7) 

## Render ：渲染器

Render 请参加 JFinal 的文档 [https://www.jfinal.com/doc/3-7](https://www.jfinal.com/doc/3-7) 

## Session

Jboot 增强了 JFinal 的 Session 功能，同时 Session 默认使用了 Jboot 自带的缓存实现，当 Jboot 开启分布式缓存之后（比如 redis ）。Session 就会自动有了分布式 Session 的功能。

开启分布式缓存，值需要添加如下配置：

```properties
jboot.cache.type = redis

jboot.cache.redis.host = 127.0.0.1
jboot.cache.redis.port = 3306
jboot.cache.redis.password
jboot.cache.redis.database
jboot.cache.redis.timeout
```

> 更多关于缓存的配置请参考【缓存】章节



添加以上配置后，我们在 Controller 中就可以使用如下代码操作 Session 了。

```java
@RequestMapping("/")
public class MyController extends JbootController {

    public void index() {
        
        //设置 session 内容
        setSessionAttr("attr", "your session value");

        renderText("hello world");
    }
}
```

当然，也可以通过如下来对 Session 进行更多的配置：

```
jboot.web.session.cookieName            #cookie 的名称
jboot.web.session.cookieDomain          #cookie 的域名
jboot.web.session.cookieContextPath     #cookie 的路径
jboot.web.session.maxInactiveInterval   #cookie 的刷新时间
jboot.web.session.cookieMaxAge          #cookie 的有效时间
jboot.web.session.cacheName             #Session的缓存名称
jboot.web.session.cacheType             #Session的缓存类型（不配置的情况使用jboot的默认缓存）
```

## Cookie

Jboot 增强了 JFinal 的 Cookie 功能，同时提供了 CookieUtil 工具类，用于对 Cookie 进行加密安全保护 Cookie 信息安全。

```java

//设置 Cookie 数据
CookieUtil.put(controller,"key","value");

//读取 Cookie 数据
CookieUtil.get(controller,"key")
```

```properties
jboot.web.cookieEncryptKey = cookie安全秘钥
jboot.web.cookieMaxAge = 60 * 60 * 24 * 2
```



