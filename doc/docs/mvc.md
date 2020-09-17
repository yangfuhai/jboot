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
- Jwt ： Json Web Token
- Validate ： 验证器
- 安全


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

Jboot 增强了 JFinal 的 Session 功能，同时 Session 默认使用了 Jboot 自带的缓存实现，当 Jboot 的缓存使用分布式缓存之后（比如 redis ）。Session 就会自动有了分布式 Session 的功能。

当然，也可以通过如下来配置 Session 特殊功能：

```
jboot.web.session.cookieName            #cookie 的名称
jboot.web.session.cookieDomain          #cookie 的域名
jboot.web.session.cookieContextPath     #cookie 的路径
jboot.web.session.maxInactiveInterval   #cookie 的刷新时间
jboot.web.session.cookieMaxAge          #cookie 的有效时间
jboot.web.session.cacheName             #Session的缓存名称
jboot.web.session.cacheType             #Session的缓存类型（不配置的情况使用jboot的默认缓存）
```


# Jwt

 Json web token (JWT), 是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准（RFC 7519).该token被设计为紧凑且安全的，特别适用于分布式站点的单点登录（SSO）场景。JWT的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，该token也可直接被用于认证，也可被加密。


**JWT的方法：**

|方法调用 | 描述 |
| ------------- | -----|
| setJwtAttr()| 设置 jwt 的 key 和 value |
| setJwtMap()| 把整个 map的key和value 设置到 jwt |
| getJwtAttr()| 获取 已经设置进去的 jwt 信息 |
| getJwtAttrs()| 获取 所有已经设置进去的 jwt 信息|
| getJwtPara()| 获取客户端传进来的 jwt 信息，若 jwt 超时或者不被信任，那么获取到的内容为null |


**JWT的相关配置**

|配置属性 | 描述 |
| ------------- | -----|
| jboot.web.jwt.httpHeaderName| 配置JWT的http头的key，默认为JWT |
| jboot.web.jwt.secret | 配置JWT的密钥 |
| jboot.web.jwt.validityPeriod | 配置JWT的过期时间，默认不过期 |


## Validate ： 验证器

Jboot 提供了一些列的 validate 注解，方便用户对 Controller 进行数据验证。

- CaptchaValidate 对验证码进行验证
- EmptyValidate  对空内容进行验证
- UrlParaValidate 对URl参数内容进行验证

使用方法：

```java
@RequestMapping("/validate")
public class ValidateController extends Controller {

    public void index(){
        renderText("index");
    }

   //  访问 /validate/test1 不通过，必须是 /validate/test1/data 才会通过
    @UrlParaValidate
    public void test1(){
        renderText("test1");
    }

   //  访问 /validate/test2 不通过，浏览器会显示内容 ：test2 was verification failed
    @UrlParaValidate(renderType = ValidateRenderType.TEXT,message = "test2 was verification failed")
    public void test2(){
        renderText("test2");
    }

   //  访问 /validate/test3 不通过，必须传入 form 数据
    @EmptyValidate(value = @Form(name = "form"),renderType = ValidateRenderType.JSON)
    public void test3(){
        renderText("test3");
    }
}
```