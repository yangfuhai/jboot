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

## Jboot 增强的 Controller 功能

#### 增强功能1：缓存功能

例如：
```java
@Cacheable(name = "aaa", liveSeconds = 10)
public void json() {
    
    System.out.println("json() invoked!!!!!!!!!");
    
    Map<String, Object> data = new HashMap<>();
    data.put("age", 1);
    data.put("name", "张三");
    data.put("sex", 1);
    
    renderJson(data);
}
```
以上的代码中，在 10 秒钟内多次访问的时候，只有第一次访问到了 json() 方法，其余的都直接使用第一次的结果输出给浏览器。其中，缓存的名称为
"aaa"，key 为 `class.method()` 格式。我们可以通过 `@Cacheable(name = "aaa", key="xxx")` 来指定缓存的key值。

其中 key 里的内容还可以使用 `#(方法参数名称)` 和 `@para('http参数名称')` 的方式来定义 key。

例如：
```java
@Cacheable(name = "aaa", key="#para('mykey')", liveSeconds = 10)
public void json() {
    
    System.out.println("json() invoked!!!!!!!!!");
    
    Map<String, Object> data = new HashMap<>();
    data.put("age", 1);
    data.put("name", "张三");
    data.put("sex", 1);
    
    renderJson(data);
}
```
当访问 `http://127.0.0.1:8080/json?mykey=thekey` 的时候，该缓存的 key 的内容为："thekey"。

但是在某些极端场景下，我们可能是不需要缓存的，配置如下：

```java
   @Cacheable(name = "aaa", liveSeconds = 10, unless = "para('type')==1")
   public void json2() {
        System.out.println("json2() invoked!!!!!!!!!");
        Map<String, Object> data = new HashMap<>();
        data.put("age", 1);
        data.put("name", "张三");
        data.put("sex", 1);
        renderJson(data);
    }
```

当访问 `http://127.0.0.1:8080/json2?type=1` 的时候，永远不会命中缓存。

#### 增强功能2：返回值自动渲染功能

例如：

```java
@RequestMapping("/")
public MyController extends Controller{

    //自动使用 html 来渲染
    public String test1(){
        return "test1.html";
     }

     //自动使用文本来渲染
    public String test2(){
        return "test2...";
    }

    //渲染 404 错误
    public String test3(){
        return "error: 404";
    }

    //渲染 500 错误
    public String test4(){
        return "error: 500";
    }

    //自动 redirect 跳转
    public String test5(){
        return "redirect: /to/your/path";
    }

    //自动 forward action 重定向
    public String test6(){
        return "forward: /to/your/path";
    }

    //自动进行文件下载
    public File test7(){
        return new File('/file/path');
    }

    //渲染 json 内容
    public Object test8(){
        Map<String,Object> map = new HashMap<>();
        map.put("key","value....");
        return map;
    }

    //使用自动的 render 渲染
    public Object test9(){
        Render render = new TextRender("some text...");
        return render;
    }
        
}
```

#### 增强功能3：XSS 功能防护功能

在 jboot 中，只需要我们在 jboot.properties 添加如下配置，并能开启全局 xss 防护功能：

```properties
jboot.web.escapeParasEnable = true
```
所以的 http 请求都会自动进行 escape 编码，防止 xss 攻击。若想在某些极端场景下获取原始内容，只需要我们在 Controller 里通过
getOriginalPara('key'); 既可以获得原始的内容，此时，需要自己进行 XSS 内容过滤或者对内容进行安全编码。

#### 增强功能4：更加强大的验证器功能

详情：http://www.jboot.com.cn/docs/validator.html

#### 更多的增强功能

Jboot 还对 web 模块做了许多其他的增强，比如 1、分布式 session 的支持； 2、json 增强，前端传入 json 内容可以直接注入到 model 或者 bean；
3、更多的模板指令，比如前端的分页指令等。4、@EnableCORS 对跨域的支持。  5、更加方便的枚举类在 模板 里的使用。6、提供 @GetRequest、@PostRequest
对 Controller 方法的限制 等等等等。


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



