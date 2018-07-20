
# 目录
- [快速上手](#快速上手)
- [JBoot核心组件](#jboot核心组件)
- [MVC](#mvc)
	- MVC的概念
	- JbootController
	- @RquestMapping
		- 使用@RquestMapping
		- render
	- session 与 分布式session
	- websocket
- [安全控制](#安全控制)
	- shiro简介
	- shiro的配置
	- shiro的使用
		- 12个模板指令（用在html上）
		- 5个Requires注解功能（用在Controller上）
- [ORM](#orm)
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
- [AOP](#aop)
	- Google Guice
	- @Inject
	- @Bean
- [RPC远程调用](#rpc远程调用)
	- 使用步骤
	- 其他注意
- [Redis操作](#redis操作)
	- Redis简介
	- Redis的使用
	- Redis操作系列方法
	- Redis扩展
	- Redis集群
- [MQ消息队列](#mq消息队列)
	- 使用步骤
	- RedisMQ
	- ActiveMQ
	- RabbitMq
	- 阿里云商业MQ
- [Cache缓存](#cache缓存)
	- 使用步骤
	- 注意事项
	- ehcache
	- redis
	- ehredis
- [http客户端](#http客户端)
	- Get请求
	- Post 请求
	- 文件上传
	- 文件下载
- [metrics数据监控](#metrics数据监控)
	- 添加metric数据
	- metric与Ganglia
	- metric与grafana
	- metric与jmx
- [容错与隔离](#容错与隔离)
	- hystrix配置
	- Hystrix Dashboard 部署
	- 通过 Hystrix Dashboard 查看数据
	
- [Opentracing数据追踪](#opentracing数据追踪)
	- [Opentracing简介](#opentracing简介)
	- [Opentracing在Jboot上的配置](#opentracing在jboot上的配置)
	- [Zipkin](#zipkin)
		- [Zipkin快速启动](#zipkin快速启动)
		- [使用zipkin](#使用zipkin)
	- SkyWalking
		- [SkyWalking快速启动](#skywalking快速启动)
		- [使用SkyWalking](#使用skywalking)
	- 其他
- [统一配置中心](#统一配置中心)
	- [部署统一配置中心服务器](#部署统一配置中心服务器)
	- [连接统一配置中心](#连接统一配置中心)


- [Swagger api自动生成](#swagger-api自动生成)
	- [swagger简介](#swagger简介)
	- [swagger使用](#swagger使用)
	- [5个swagger注解](#swagger使用)

	
- 其他
	- [SPI扩展](#spi扩展)
	- [JbootEvnet事件机制](#jbootEvnet事件机制)
	- 自定义序列化
	- 配置文件
	- 代码生成器
- [项目构建](#项目构建)
- 鸣谢
- [联系作者](#联系作者)
- [常见问题](#常见问题)
	- 使用Jboot后还能自定义JfinalConfig等配置文件吗？


# 快速上手

#### 创建项目
略
#### 添加Jboot依赖

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>1.6.3</version>
</dependency>
```
#### 编写helloworld

```java
@RequestMapping("/")
public class MyController extends JbootController{
   public void index(){
        renderText("hello jboot");
   }
   
   public static void main(String [] args){
       Jboot.run(args);
   }
}
```
#### 运行并浏览器查看
运行main方法后，在浏览器输入网址：http://127.0.0.1:8088 查看，此时，浏览器显示：hello jboot


# JBoot核心组件
Jboot的主要核心组件有以下几个。

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
* MQ消息队列 
    * [x] rabbitmq
    * [x] redismq
    * [x] 阿里云商业MQ
    * [ ] activemq
* 缓存
    * [x] ehcache
    * [x] redis
    * [x] 分布式二级缓存ehredis
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


# MVC
## MVC的概念
略

## Controller
Controller是JFinal核心类之一，该类作为MVC模式中的控制器。基于JFinal的Web应用的控制器需要继承该类。Controller是定义Action方法的地点，是组织Action的一种方式，一个Controller可以包含多个Action。Controller是线程安全的。
### JbootController
JbootController是扩展了JFinal Controller，在Jboot应用中，所有的控制器都应该继承至JbootController。

**JbootController新增的普通方法：**

|方法调用 | 描述 |
| ------------- | -----|
|isMoblieBrowser()| 是否是手机浏览器|
|isWechatBrowser()| 是否是微信浏览器|
|isIEBrowser()| 是否是IE浏览器，低级的IE浏览器在ajax请求的时候，返回json要做特殊处理|
| isAjaxRequest()| 是否是ajax请求 |
| isMultipartRequest()| 是否是带有文件上传功能的请求|
| getReferer()| 获取来源网址器|
| getIPAddress()| 获取用户的IP地址，这个决定于浏览器，同时做nginx等转发的时候要做好配置|
| getUserAgent()| 获取http头的useragent|
| getBaseUrl()| 获取当前域名|
| getUploadFilesMap()| 获取当前上传的所有文件|

**新增关于FlashMessage的方法：**

|方法调用 | 描述 |
| ------------- | -----|
| setFlashAttr()| 设置 FlashMessage 的 key 和 value |
| setFlashMap()| 把整个 map的key和value 设置到 FlashMessage|
| getFlashAttr()| 获取 已经设置进去的FlashMessage 信息 |
| getFlashAttrs()| 获取 所有已经设置进去的 FlashMessage 信息|

FlashMessage 是一种特殊的 attribute，用法和 setAttr 一样，唯一不同的是 setAttr 是用于当前页面渲染，而
setFlashAttr 是用于对 redirect 之后的页面进行渲染。


**新增关于JWT的方法：**

|方法调用 | 描述 |
| ------------- | -----|
| setJwtAttr()| 设置 jwt 的 key 和 value |
| setJwtMap()| 把整个 map的key和value 设置到 jwt |
| getJwtAttr()| 获取 已经设置进去的 jwt 信息 |
| getJwtAttrs()| 获取 所有已经设置进去的 jwt 信息|
| getJwtPara()| 获取客户端传进来的 jwt 信息，若 jwt 超时或者不被信任，那么获取到的内容为null |

**JWT简介：**  Json web token (JWT), 是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准（[RFC 7519](https://tools.ietf.org/html/rfc7519)).该token被设计为紧凑且安全的，特别适用于分布式站点的单点登录（SSO）场景。JWT的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，该token也可直接被用于认证，也可被加密。

*JWT的相关配置*

|配置属性 | 描述 |
| ------------- | -----|
| jboot.web.jwt.httpHeaderName| 配置JWT的http头的key，默认为JWT |
| jboot.web.jwt.secret | 配置JWT的密钥 |
| jboot.web.jwt.validityPeriod | 配置JWT的过期时间，默认不过期 |


### @RquestMapping
RquestMapping是请求映射，也就是通过@RquestMapping注解，可以让某个请求映射到指定的控制器Controller里去。


**使用@RquestMapping**

使用@RquestMapping非常简单。只需要在Controller类添加上@RquestMapping注解即可。

例如：

```java
@RequestMapping("/")
public class HelloController extends JbootController{
   public void index(){
        renderText("hello jboot");
   }
}
```
我们在HelloController控制器上，添加了@RequestMapping("/")配置，也就是让当访问 `http://127.0.0.1/`的时候让HelloController控制的index()这个方法（action）来处理。

**[注意]：** 

* 访问`http://127.0.0.1`等同于`http://127.0.0.1/`。
* `@RquestMapping` 可以使用在任何的 Controller，并 **不需要** 这个Controller继承至JbootController。

### Action
在 Controller 之中定义的 public 方法称为 Action。Action 是请求的最小单位。Action 方法 必须在 Controller 中定义，且必须是 public 可见性。

每个Action对应一个URL地址的映射：


```java
public class HelloController extends Controller { 

	public void index() {
		renderText("此方法是一个action"); 
	}

	public String test() { 
		return "index.html";
	} 
	
	public String save(User user) { 
		user.save();
		render("index.html");
	} 
}
```
以上代码中定义了三个 Action，分别是 `HelloController.index()`、 `HelloController.test()` 和 `HelloController.save(User user)`。

Action 可以有返回值，返回值可在拦截器中通过 invocation.getReturnValue() 获取到，以便进行 render 控制。

Action 可以带参数，可以代替 getPara、getBean、getModel 系列方法获取参数，使用 UploadFile 参数时可以代替 getFile 方法实现文件上传。这种传参方式还有一个好处是便于与 swagger 这类 第三方无缝集成，生成 API 文档。

**注意：** 带参数的Action必须在pom.xml文件里添加如下配置：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <encoding>UTF-8</encoding>
        <!--必须添加compilerArgument配置，才能使用JController方法带参数的功能-->
        <compilerArgument>-parameters</compilerArgument>
    </configuration>
</plugin>
```

如果 action 形参是一个 model 或者 bean，原先通过 getBean(User.class, “”) 获取 时第二个参数为空字符串或 null，那么与之等价的形参注入只需要用一下@Para(“”)注解即可:

```java
public void save(@Para(“”)User user) { 
	user.save();
	render("index.html");
}
```

### getPara 系列方法
Controller  供了 getPara 系列方法用来从请求中获取参数。getPara 系列方法分为两种类型。 第一种类型为第一个形参为 String 的 getPara 系列方法。该系列方法是对 HttpServletRequest.getParameter(String name) 的 封 装 ， 这 类 方 法 都 是 转 调 了 HttpServletRequest.getParameter(String name)。第二种类型为第一个形参为 int 或无形参的 getPara 系列方法。该系列方法是去获取 urlPara 中所带的参数值。getParaMap 与 getParaNames 分别对应 HttpServletRequest 的 getParameterMap 与 getParameterNames。

|方法调用 | 返回值 |
| ------------- | -----|
|getPara(”title”)| 返回页面表单域名为“title”参数值|
|getParaToInt(”age”) |返回页面表单域名为“age”的参数值并转为 int 型 |
|getPara(0)|返回 url 请求中的 urlPara 参数的第一个值，如 http://localhost/controllerKey/method/v0-v1-v2 这个请求将 返回”v0”|
|getParaToInt(1)|返回 url 请求中的 urlPara 参数的第二个值并转换成 int 型，如 http://localhost/controllerKey/method/2-5-9 这个请求将返回 5|
|getParaToInt(2)|如http://localhost/controllerKey/method/2-5-N8 这个 请求将返回 -8。注意:约定字母 N 与 n 可以表示负 号，这对 urlParaSeparator 为 “-” 时非常有用。|
|getPara()|返回 url 请求中的 urlPara 参数的整体值，如 http://localhost/controllerKey/method/v0-v1-v2 这个 请求将返回”v0-v1-v2”

### getBean 与 getModel 方法
getModel 用来接收页面表单域传递过来的 model 对象，表单域名称以”modelName.attrName”方式命名，getModel 使用的 attrName 必须与数据表字段名完全一样。getBean 方法用于支持传统 Java Bean，包括支持使用 jfnal 生成器生成了 getter、setter 方法的 Model，页面表单传参时使用与 setter 方法相一致的 attrName，而非数据表字段名。 getModel 与 getBean 区别在于前者使用数表字段名而后者使用与 setter 方法一致的属性名进行数据注入。建议优先使用 getBean 方法。 

以下是一个简单的示例:

```java
// 定义Model，在此为Blog
public class Blog extends JbootModel<Blog> {
	
}

// 在页面表单中采用modelName.attrName形式为作为表单域的name 
<form action="/blog/save" method="post">
	<input name="blog.title" type="text"> 
	<input name="blog.content" type="text"> 
	<input value=" 交" type="submit">
</form>

@RequestMapping("/blog")
public class BlogController extends JbootController { 

	public void save() {
		// 页面的modelName正好是Blog类名的首字母小写 
		Blog blog = getModel(Blog.class);
		
		//如果表单域的名称为 "otherName.title"可加上一个参数来获取
		Blog blog = getModel(Blog.class, "otherName");
		
		//如果表单域的名称为 "title" 和 "content" 
		Blog blog = getModel(Blog.class, "");
	}
	
	// 或者 也可以写如下代码,但是注意，只能写一个save方法
	public void save(Blog blog) {
		// do your something
	}
	
}
```

上面代码中，表单域采用了”blog.title”、”blog.content”作为表单域的 name 属性，”blog”是类 文件名称”Blog”的首字母变小写，”title”是 blog 数据库表的 title 字段，如果希望表单域使用任 意的 modelName ，只需要在 getModel 时多添加一个参数来指定，例如: getModel(Blog.class, ”otherName”)。

### render
渲染器，负责把内容输出到浏览器，在Controller中，提供了如下一些列render方法。

| 指令         |  描述  |
| ------------- | -----|
| render(”test.html”)  |渲染名为 test.html 的视图，该视图的全路径为”/path/test.html” |
| render(”/other_path/test.html”)   |渲染名为 test.html 的视图，该视图的全路径为”/other_path/test.html”，即当参数以”/”开头时将采用绝对路径。|
| renderTemplate(”test.html”)  |渲染名为 test.html 的视图，且视图类型为 JFinalTemplate。|
| renderFreeMarker(”test.html”)  |渲 染 名 为 test.html 的视图 ， 且 视图类型为FreeMarker。 |
| renderJsp(”test.jsp”)  |渲染名为 test.jsp 的视图，且视图类型为 Jsp。 |
| renderVelocity(“test.html”)   |渲染名为 test.html 的视图，且视图类型为 Velocity。 |
| renderJson()  |将所有通过 Controller.setAttr(String, Object)设置的变量转换成 json 数据并渲染。|
| renderJson(“users”, userList)   |以”users”为根，仅将 userList 中的数据转换成 json数据并渲染。 |
| renderJson(user)  |将 user 对象转换成 json 数据并渲染。 |
| renderJson(“{\”age\”:18}” )   |直接渲染 json 字符串。 |
| renderJson(new String[]{“user”, “blog”})  |仅将 setAttr(“user”, user)与 setAttr(“blog”, blog)设置的属性转换成 json 并渲染。使用 setAttr 设置的其它属性并不转换为 json。 |
| renderFile(“test.zip”);  |渲染名为 test.zip 的文件，一般用于文件下载 |
| renderText(“Hello Jboot”)   |渲染纯文本内容”Hello Jboot”。 |
| renderHtml(“Hello Html”)   |渲染 Html 内容”Hello Html”。 |
| renderError (404 , “test.html”)  |渲染名为 test.html 的文件，且状态为 404。 |
| renderError (500 , “test.html”)   |渲染名为 test.html 的文件，且状态为 500。 |
| renderNull() |不渲染，即不向客户端返回数据。|
| render(new MyRender()) |使用自定义渲染器 MyRender 来渲染。 |

### session 与 分布式session

使用session非常简单，直接在Controller里调用`getSessionAttr(key)` 或 `setSessionAttr(key,value)` 就可以。

#### 分布式session
在Jboot的设计中，分布式的session是依赖分布式缓存的，jboot中，分布式缓存提供了3种方式：

1. ehcache
2. redis
3. ehredis： 基于ehcache和redis实现的二级缓存框架。

所以，在使用jboot的分布式session之前，需要在jboot.properties配置上jboot分布式的缓存。

例如：

```html
jboot.cache.type=redis
jboot.cache.redis.host = 127.0.0.1
jboot.cache.redis.password = 123456
jboot.cache.redis.database = 1
```
配置好缓存后，直接在Controller里调用`getSessionAttr(key)` 或 `setSessionAttr(key,value)` 即可。

*注意：* session都是走缓存，如果jboot配置的缓存是ehcache（或者 ehredis）,请注意在ehcache.xml上添加名为 `SESSION` 的缓存节点。

### 限流和流量控制
在Jboot中，默认提供了4个注解进行流量管控。4个注解代表着四个不同的流量管控方案，他们分别是：

| 指令         |  描述  |
| ------------- | -----|
| EnableConcurrencyLimit | 限制当前Action的并发量 |
| EnablePerIpLimit  |限制每个IP的每秒访问量|
| EnablePerUserLimit  |限制每个用户的访问量|
| EnableRequestLimit  |限制总体每秒钟可以通过的访问量|

例如：

```java
@RequestMapping("/limitation")
public class LimitationDemo extends JbootController {


    public static void main(String[] args) {
        Jboot.setBootArg("jboot.limitation.webPath","/limitation/view");
        Jboot.run(args);
    }


    public void index() {
        renderText("render ok");
    }

    /**
     * 所有的请求，每1秒钟只能访问一次
     */
    @EnableRequestLimit(rate = 1)
    public void request() {
        renderText("request() render ok");
    }

    /**
     * 所有的请求，并发量为1个
     */
    @EnableConcurrencyLimit(rate = 1)
    public void con() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        renderText("con() render ok");
    }

    /**
     * 所有的请求，每1秒钟只能访问一次
     * 被限制的请求，自动跳转到 /limitation/request2
     */
    @EnableRequestLimit(rate = 1, renderType = LimitRenderType.REDIRECT, renderContent = "/limitation/request2")
    public void request1() {
        renderText("request1() render ok");
    }


    public void request2() {
        renderText("request2() render ok");
    }


    /**
     * 每个用户，每5秒钟只能访问一次
     */
    @EnablePerUserLimit(rate = 0.2)
    public void user() {
        renderText("user() render ok");
    }

    /**
     * 每个用户，每5秒钟只能访问一次
     * 被限制的请求，渲染文本内容 "被限制啦"
     */
    @EnablePerUserLimit(rate = 0.2, renderType = LimitRenderType.TEXT, renderContent = "被限制啦")
    public void user1() {
        renderText("user1() render ok");
    }


    /**
     * 每个IP地址，每5秒钟只能访问一次
     */
    @EnablePerIpLimit(rate = 0.2)
    public void ip() {
        renderText("ip() render ok");
    }
}
```

以上代码和注释已经很清楚的描述了每个注解的意义，但是，针对已经上线的项目，使用@EnableXXXLimit进行流量控制并一定是有效的，很多时候我们需要针对突发流量进行限制和管控，因此，除了以上注解意外，Jboot提供了在线流量管理功能。

使用Jboot在线流量管理，首先配置上流量管理的URL地址，例如：

```
jboot.limitation.webPath = /jboot/limitation
```

配置好，启动项目，访问 `http://127.0.0.1:8080/jboot/limitation` 我们可以看到如下内容：

```json
{
"ipRates": {
	"/limitation/ip": {
		"enable": true,
		"rate": 0.2,
		"renderContent": "",
		"renderType": "",
		"type": "ip"
	}
},
"userRates": {
	"/limitation/user": {
		"enable": true,
		"rate": 0.2,
		"renderContent": "",
		"renderType": "",
		"type": "user"
	},
	"/limitation/user1": {
		"enable": true,
		"rate": 0.2,
		"renderContent": "被限制啦",
		"renderType": "text",
		"type": "user"
	}
},
"concurrencyRates": {
	"/limitation/con": {
		"enable": true,
		"rate": 1,
		"renderContent": "",
		"renderType": "",
		"type": "concurrency"
	}
},
"requestRates": {
	"/limitation/request1": {
		"enable": true,
		"rate": 1,
		"renderContent": "/limitation/request2",
		"renderType": "redirect",
		"type": "request"
	},
	"/limitation/request": {
		"enable": true,
		"rate": 1,
		"renderContent": "",
		"renderType": "",
		"type": "request"
	}
}
}
```

#### 限流API

1. 限流设置
	* 接口：`/jboot/limitation/set`
	* 参数：
	
		| 参数         |  描述  |
		| ------------- | -----|
		| type | 限流类型：支持有 `ip`,`user`,`request`,`concurrency`，分别代表：单个IP每秒钟限流、单个用户每秒钟限流、每秒钟允许请求的数量，总体并发量设置 |
		| path  |要对那个路径进行设置，例如 `/user/aabb`|
		| rate  |设置的数值是多少|
	
1. 关闭限流管控
	* 接口：`/jboot/limitation/close`
	* 参数：
	
		| 参数         |  描述  |
		| ------------- | -----|
		| type | 限流类型：支持有 `ip`,`user`,`request`,`concurrency`，分别代表：单个IP每秒钟限流、单个用户每秒钟限流、每秒钟允许请求的数量，总体并发量设置 |
		| path  |要对那个路径进行设置，例如 `/user/aabb`|
		
1. 开启限流管控
	* 接口：`/jboot/limitation/enable`
	* 参数：
	
		| 参数         |  描述  |
		| ------------- | -----|
		| type | 限流类型：支持有 `ip`,`user`,`request`,`concurrency`，分别代表：单个IP每秒钟限流、单个用户每秒钟限流、每秒钟允许请求的数量，总体并发量设置 |
		| path  |要对那个路径进行设置，例如 `/user/aabb`|


**注意：** 

1. 通过限流API进行限流，所有的设置都会保存在内存里，因此如果重启服务器后，通过限流API进行限流的所有设置将会失效。
2. 接口的前缀 `/jboot/limitation`是通过jboot.properties的`jboot.limitation.webPath = /jboot/limitation`进行设置的。

**限流API安全设置**

由于限流功能对系统至关重要，为了防止恶意用户猜出限流API对系统进行恶意操作，因此Jboot提供了限流API的权限设置功能，需要通过 通过jboot.properties的`jboot.limitation.webAuthorizer = com.xxx.MyAuthorizer`进行设置，其中`MyAuthorizer`需要实现`io.jboot.web.limitation.web.Authorizer`接口。

例如：

```java
public class MyAuthorizer implements Authorizer {
    @Override
    public boolean onAuthorize(Controller controller) {
        return true;
    }
}
```

当限流API被请求的时候，会通过 `MyAuthorizer` 进行权限认证，只有`MyAuthorizer`通过(onAuthorize返回`true`)的时候，请求API才会生效。

## websocket
在使用websocket之前，需要在jboot.properties文件上配置启动websocket，例如：

```java
jboot.web.websocketEnable = true
jboot.web.websocketBufferPoolSize = 100  
```

`jboot.web.websocketBufferPoolSize` 在没有配置的情况下，默认值是`100`。

当做好以上配置后，就可以开始编写websocket的相关代码了。

html代码：

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
服务器返回的信息：
<input type="text" id="show"/>

浏览器发送的信息：
<input type="text" id="msg"/>
<input type="button" value="send" id="send" onclick="send()"/>


<script>
    var ws = null ;
    var target="ws://localhost:8080/websocket/test";
    if ('WebSocket' in window) {
        ws = new WebSocket(target);
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(target);
    } else {
        alert('WebSocket is not supported by this browser.');
    }

    ws.onopen = function(obj){
        console.info('open') ;
        console.info(obj) ;
    } ;
    
    ws.onclose = function (obj) {
        console.info('close') ;
        console.info(obj) ;
    } ;
    ws.onmessage = function(obj){
        console.info(obj) ;
        document.getElementById('show').value=obj.data;
    } ;
    function send(){
    	ws.send(document.getElementById('msg').value);
    }
</script>
</body>
</html>
```

java代码：

```java
@ServerEndpoint("/websocket/test")
public class Test {
	@OnOpen
	public void onOpen(){
		System.out.println("onOpen");
	}
	@OnClose
	public void onClose(){
		System.out.println("onClose");
	}
	@OnMessage
	public void onMessage(Session session,String msg){
		System.out.println("receive message : "+msg);
		if(session.isOpen()){
			try {
				//发送消息的html页面
				session.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
```


# 安全控制 
## shiro简介
Apache Shiro是一个强大且易用的Java安全框架,执行身份验证、授权、密码学和会话管理。使用Shiro的易于理解的API,您可以快速、轻松地获得任何应用程序,从最小的移动应用程序到最大的网络和企业应用程序。

## shiro的配置
在使用Jboot的shiro模块之前，我假定您已经学习并了解shiro的基础知识。在Jboot中使用shiro非常简单，只需要在resources目录下配置上您的shiro.ini文件即可。在shiro.ini文件里，需要在自行扩展realm等信息。


## shiro的使用
Jboot的shiro模块为您提供了以下12个模板指令，同时支持shiro的5个Requires注解功能。方便您使用shiro。

### 12个模板指令（用在html上）

| 指令         |  描述  |
| ------------- | -----|
| shiroAuthenticated |用户已经身份验证通过，Subject.login登录成功 |
| shiroGuest  |游客访问时。 但是，当用户登录成功了就不显示了|
| shiroHasAllPermission  |拥有全部权限 |
| shiroHasAllRoles  |拥有全部角色 |
| shiroHasAnyPermission  |拥有任何一个权限 |
| shiroHasAnyRoles  |拥有任何一个角色 |
| shiroHasPermission  |有相应权限 |
| shiroHasRole  |有相应角色 |
| shiroNoAuthenticated  |未进行身份验证时，即没有调用Subject.login进行登录。 |
| shiroNotHasPermission  |没有该权限 |
| shiroNotHasRole  |没有该角色 |
| shiroPrincipal  |获取Subject Principal 身份信息 |





#### shiroAuthenticated的使用

```html
#shiroAuthenticated()
  登陆成功：您的用户名是：#(SESSION("username"))
#end

```



#### shiroGuest的使用

```html
#shiroGuest()
  游客您好
#end

```

#### shiroHasAllPermission的使用

```html
#shiroHasAllPermission(permissionName1,permissionName2)
  您好，您拥有了权限 permissionName1和permissionName2
#end

```

#### shiroHasAllRoles的使用

```html
#shiroHasAllRoles(role1, role2)
  您好，您拥有了角色 role1和role2
#end

```
#### shiroHasAnyPermission的使用

```html
#shiroHasAnyPermission(permissionName1,permissionName2)
  您好，您拥有了权限 permissionName1 或 permissionName2 
#end

```
#### shiroHasAnyRoles的使用

```html
#shiroHasAllRoles(role1, role2)
  您好，您拥有了角色 role1 或 role2
#end

```
#### shiroHasPermission的使用

```html
#shiroHasPermission(permissionName1)
  您好，您拥有了权限 permissionName1 
#end

```
#### shiroHasRole的使用

```html
#shiroHasRole(role1)
  您好，您拥有了角色 role1 
#end

```
#### shiroNoAuthenticated的使用

```html
#shiroNoAuthenticated()
  您好，您还没有登陆
#end

```
#### shiroNotHasPermission的使用

```html
#shiroNotHasPermission(permissionName1)
  您好，您没有权限 permissionName1 
#end

```
#### shiroNotHasRole的使用
```html
#shiroNotHasRole(role1)
  您好，您没有角色role1
#end

```
#### shiroPrincipal的使用
```html
#shiroPrincipal()
  您好，您的登陆信息是：#(principal)
#end

```


### 5个Requires注解功能（用在Controller上）

| 指令         |  描述  |
| ------------- | -----|
| RequiresPermissions | 需要权限才能访问这个action |
| RequiresRoles  | 需要角色才能访问这个action|
| RequiresAuthentication  | 需要授权才能访问这个action，即：`SecurityUtils.getSubject().isAuthenticated()` |
| RequiresUser  | 获取到用户信息才能访问这个action，即：`SecurityUtils.getSubject().getPrincipal() != null ` |
| RequiresGuest  | 和RequiresUser相反 |


#### RequiresPermissions的使用

```java
public class MyController extends JbootController{

      @RequiresPermissions("permission1")
      public void index(){

	  }
	  
	  @RequiresPermissions(value={"permission1","permission2"},logical=Logincal.AND)
      public void index1(){

	  }
}
```

#### RequiresRoles的使用

```java
public class MyController extends JbootController{

      @RequiresRoles("role1")
      public void index(){

	  }
	  
	  @RequiresRoles(value = {"role1","role2"},logical=Logincal.AND)
      public void userctener(){

	  }
}
```

#### RequiresUser、RequiresGuest、RequiresAuthentication的使用

```java
public class MyController extends JbootController{

      @RequiresUser
      public void userCenter(){

	  }
	  
	  @RequiresGuest
      public void login(){

	  }
	  
	  @RequiresAuthentication
	  public void my(){
	  
	  }
}
```

## JWT

### JWT简介
Json web token (JWT), 是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准（[RFC 7519](https://tools.ietf.org/html/rfc7519)).该token被设计为紧凑且安全的，特别适用于分布式站点的单点登录（SSO）场景。JWT的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，该token也可直接被用于认证，也可被加密。

### JWT的使用

#### 在server段使用JWT

在Server端使用JWT非常简单，代码如下：

```java
public class JwtController extends JbootController {

    public void index() {
        setJwtAttr("key1", "test1");
        setJwtAttr("key2", "test2");
        
        //do your sth
    }

    public void show() {
        String value = getJwtPara("key1");
        // value : test1
    }
}
```

**注意：** 在Server端使用JWT，必须在jboot.properties配置文件中配置上 jwt 的秘钥，代码如下：

```java
jboot.web.jwt.secret = your_secret
```

**关于JWT的方法：**

|方法调用 | 描述 |
| ------------- | -----|
| setJwtAttr()| 设置 jwt 的 key 和 value |
| setJwtMap()| 把整个 map的key和value 设置到 jwt |
| getJwtAttr()| 获取 已经设置进去的 jwt 信息 |
| getJwtAttrs()| 获取 所有已经设置进去的 jwt 信息|
| getJwtPara()| 获取客户端传进来的 jwt 信息，若 jwt 超时或者不被信任，那么获取到的内容为null |

#### 在客户端使用JWT

在客户端使用JWT的场景一般是用于非浏览器的第三方进行认证，例如：APP客户端，前后端分离的AJAX请求等。

例如，在登录后，服务器Server会通过 `setJwtAttr()` 设置上用户数据，客户端可以去获取 HTTP 响应头中的 Jwt，就可以获取 服务器渲染的 Jwt 信息，此时，应该把 Jwt 的信息保存下来，比如保存到 cookie 或 保存在storage等，
在客户每次请求服务器 API 的时候，应该把 Jwt 设置在请求的 http 头中的 Jwt（注意，第一个字母大写），服务器就可以获取到具体是哪个 “用户” 进行请求了。

## shiro的其他使用

### 自定义shiro错误处理
编写一个类实现 实现接口 io.jboot.component.shiro.JbootShiroInvokeListener，例如：


```java
  public class MyshiroListener implements  JbootShiroInvokeListener {


        private JbootShiroConfig config = Jboot.config(JbootShiroConfig.class);


        @Override
        public void onInvokeBefore(FixedInvocation inv) {
            //do nothing
        }

        @Override
        public void onInvokeAfter(FixedInvocation inv, AuthorizeResult result) {
            if (result == null || result.isOk()) {
                inv.invoke();
                return;
            }

            int errorCode = result.getErrorCode();
            switch (errorCode) {
                case AuthorizeResult.ERROR_CODE_UNAUTHENTICATED:
                    doProcessUnauthenticated(inv.getController());
                    break;
                case AuthorizeResult.ERROR_CODE_UNAUTHORIZATION:
                    doProcessuUnauthorization(inv.getController());
                    break;
                default:
                    inv.getController().renderError(404);
            }
        }


        public void doProcessUnauthenticated(Controller controller) {
            // 处理认证失败
        }

        public void doProcessuUnauthorization(Controller controller) {
            // 处理授权失败
        }

    };
```

其次在jboot.properties中配置即可

```xml
jboot.shiro.invokeListener=com.xxx.MyshiroListener
```

### shiro 和 jwt 整合
和自定义shiro错误处理一样。 编写一个类实现 实现接口 io.jboot.component.shiro.JbootShiroInvokeListener，例如：

```java
  public class MyshiroListener implements  JbootShiroInvokeListener {

        @Override
        public void onInvokeBefore(FixedInvocation inv) {
            String userId = String.valueOf(inv.getController.getJwtPara(USER_ID));

            JwtAuthenticationToken token = new JwtAuthenticationToken();
            token.setUserId(userId);
            token.setToken(userId);
    
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
    
            return subject;
        }

        @Override
        public void onInvokeAfter(FixedInvocation inv, AuthorizeResult result) {
            // ....
        }

    };
```
同时在jboot.properties中配置即可

```xml
jboot.shiro.invokeListener=com.xxx.MyshiroListener
```


自定义JwtAuthenticationToken

```java
public class JwtAuthenticationToken implements AuthenticationToken {
    /** 用户id */
    private String userId;
    /** token */
    private String token;
    
    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
    
    ... getter setter
}
```



实现shiro realm JwtAuthorizingRealm

```java
public class JwtAuthorizingRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtAuthenticationToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) token;
        String uid = (String) jwtToken.getPrincipal();

        // 此处判断 uid 是否存在，可以访问等操作
       
        return new SimpleAuthenticationInfo(uid, jwtToken.getCredentials(), this.getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 此处获取 uid 角色权限
        return null;
    }
}
```

实现jwt 无状态化，JwtSubjectFactory

```java
public class JwtSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        if (context.getAuthenticationToken() instanceof JwtAuthenticationToken) {
            // jwt 不创建 session
            context.setSessionCreationEnabled(false);
        }

        return super.createSubject(context);
    }
}
```

jboot.properties中配置

```xml
#---------------------------------------------------------------------------------#
jboot.web.jwt.httpHeaderName=Jwt
jboot.web.jwt.secret=xxxxxxxxx
jboot.web.jwt.validityPeriod=1800000
#---------------------------------------------------------------------------------#```
```

shiro.ini中配置

```xml

    [main]
    #cache Manager
    shiroCacheManager = io.jboot.component.shiro.cache.JbootShiroCacheManager
    securityManager.cacheManager = $shiroCacheManager

    #realm
    dbRealm=xxx.JwtAuthorizingRealm
    dbRealm.authorizationCacheName=shiro-authorizationCache
    
    securityManager.realm=$dbRealm

    #session manager
    sessionManager=org.apache.shiro.session.mgt.DefaultSessionManager
    sessionManager.sessionValidationSchedulerEnabled=false

    #use jwt
    subjectFactory=xxx.JwtSubjectFactory
    securityManager.subjectFactory=$subjectFactory
    securityManager.sessionManager=$sessionManager

    #session storage false
    securityManager.subjectDAO.sessionStorageEvaluator.sessionStorageEnabled=false
    
```

#### 认证服务端配置
服务端主要作用为对用户名密码做认证，通过后构建jwt，与正常认证无太大区别，所以下面只给出认证后构建jwt的demo

```java
@RequestMapping("/")
public class MainController extends BaseController {

    /**
     * 登录 基于 jwt
     */
    public void postLogin(String loginName, String pwd) {
        // 此处判断用户名密码是否正确
        
        String userId = "userId"; //返回用户ID
        setJwtAttr("userId", userId); //构建jwt
        renderJson(); //返回成功
    }
}
```

### shiro 和 sso 整合
和上面介绍的 jwt 的桥接器类似，主要作用是接收 sso 请求，完成客户端应用的局部认证与授权。

以下是一个基于jboot 实现 sso服务端 与 sso客户端的 demo

#### SSO客户端配置
自定义 SSOAuthenticationToken

```java
public class SSOAuthenticationToken implements AuthenticationToken {

    /** 用户id */
    private String userId;

    /** 全局会话 code */
    private String ssoCode;

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return ssoCode;
    }
    ... getter setter
```

实现 JbootShiroInvokeListener 接口：

```java
 public class MyshiroListener implements  JbootShiroInvokeListener {

        @Override
        public void onInvokeBefore(FixedInvocation inv) {
        String ssoCode = inv.getController().getPara("ssoCode");
        String userId = inv.getController().getPara("userId");

        if (StringUtils.isBlank(ssoCode) || StringUtils.isBlank(userId)) {
            return;
        }

        SSOAuthenticationToken token = new SSOAuthenticationToken();
        token.setUserId(userId);
        token.setSsoCode(ssoCode);

        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
```

实现shiro realm SSOAuthorizingRealm

```java
public class SSOAuthorizingRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof SSOAuthenticationToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        SSOAuthenticationToken ssoToken = (SSOAuthenticationToken) token;
        String uid = (String) ssoToken.getPrincipal();
        String ssoCode = token.getCredentials().toString();

        //判断ssoCode是否为 sso 系统颁发

        // 此处判断 uid 是否存在，可以访问等操作
       
        return new SimpleAuthenticationInfo(uid, ssoToken.getCredentials(), this.getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 此处获取 uid 角色权限
        return null;
    }
}
```

实现 shiro 无认证请求重定向到 sso系统，SSOShiroErrorProcess

```java
public class MyshiroListener implements  JbootShiroInvokeListener {

        @Override
        public void onInvokeAfter(FixedInvocation inv, AuthorizeResult result) {
        if (result.isOk()) {
                inv.invoke();
                return;
            }
        int errorCode = result.getErrorCode();
            
        switch (errorCode) {
            case AuthorizeResult.ERROR_CODE_UNAUTHENTICATED:
                doProcessUnauthenticated(inv.getController());
                break;
            case AuthorizeResult.ERROR_CODE_UNAUTHORIZATION:
                doProcessuUnauthorization(inv.getController());
                break;
            default:
                inv.getController().renderError(404);
        }
    }


    public void doProcessUnauthenticated(Controller controller) {
        UpmsConfig upmsConfig = Jboot.config(UpmsConfig.class);

        StringBuilder ssoServerUrl = new StringBuilder(upmsConfig.getServerUrl());
        ssoServerUrl.append("/sso/index").append("?").append("appid").append("=").append(upmsConfig.getAppId()).append("sysid").append("=").append(upmsConfig.getSystemId());

        // 回跳地址
        StringBuffer backurl = controller.getRequest().getRequestURL();
        String queryString = controller.getRequest().getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            backurl.append("?").append(queryString);
        }
        ssoServerUrl.append("&").append("backurl").append("=").append(StringUtils.urlEncode(backurl.toString()));

        controller.redirect(ssoServerUrl.toString());
    }

    public void doProcessuUnauthorization(Controller controller) {
        controller.renderError(403);
    }
}
```


shiro.ini中配置

```xml
[main]

#cache Manager
shiroCacheManager = io.jboot.component.shiro.cache.JbootShiroCacheManager
securityManager.cacheManager = $shiroCacheManager

#realm
dbRealm=xxx.SSOAuthorizingRealm
dbRealm.authorizationCacheName=shiro-authorizationCache

securityManager.realm=$dbRealm

#session 基于缓存sessionDao，如果缓存已经实现共享，那么session也同样实现共享
sessionDAO=xxx.SessionDAO
sessionDAO.activeSessionsCacheName=shiro-active-session

#设置sessionCookie
sessionIdCookie=org.apache.shiro.web.servlet.SimpleCookie
sessionIdCookie.name=ssotestaid
#sessionIdCookie.domain=demo.com
#sessionIdCookie.path=
#cookie最大有效期，单位秒，默认30天
sessionIdCookie.maxAge=1800
sessionIdCookie.httpOnly=true

#设置session会话管理
sessionManager=org.apache.shiro.web.session.mgt.DefaultWebSessionManager
sessionManager.sessionDAO=$sessionDAO
sessionManager.sessionIdCookie=$sessionIdCookie
sessionManager.sessionIdCookieEnabled=true
sessionManager.sessionIdUrlRewritingEnabled=false
securityManager.sessionManager=$sessionManager
#session过期时间，单位毫秒，默认两天
securityManager.sessionManager.globalSessionTimeout=1800000

```

#### SSO服务端配置
SSO服务端，主要包括登录认证、全局code认证、退出等操作。

```java
@RequestMapping("/sso")
@EnableCORS
public class SSOController extends BaseController {

    public void index(String appid, String backurl) {
        // 判断 appid 是否正确，backurl 是否正确

        redirect("/sso/login?backurl=" + StringUtils.urlEncode(backurl));
    }

    @Before(GET.class)
    public void login() {
        Subject subject = SecurityUtils.getSubject();
        String backurl = getPara("backurl");

        if (subject.isAuthenticated()) {
            String loginName = (String) subject.getPrincipal();

            // 判断用户id

            String code = (String) subject.getSession(false).getId().toString();

            if (StringUtils.isBlank(backurl)) {
                renderJson(JsonResult.buildSuccess(code));
            } else {
                if (backurl.contains("?")) {
                    backurl += "&ssoCode=" + code + "&userId=" + upmsUser.getId();
                } else {
                    backurl += "?ssoCode=" + code + "&userId=" + upmsUser.getId();
                }
            }

            redirect(backurl);
        } else {
            setAttr("backurl", backurl);
            render("login.html");
        }
    }

    @Before(POST.class)
    @EmptyValidate(value = {
            @Form(name = "loginName", message = "用户名不能为空"),
            @Form(name = "password", message = "密码不能为空"),
    }, renderType = ValidateRenderType.JSON)
    public void postLogin(String loginName, String password) {
        Subject subject = SecurityUtils.getSubject();

        String backUrl = getPara("backUrl", "");
        Ret ret = JsonResult.buildSuccess("登录成功", backUrl);

        if (!subject.isAuthenticated()) {
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(loginName, password);
            subject.login(usernamePasswordToken);

            // 获取用户 id
            
            Session session = subject.getSession(true);
            String code = session.getId().toString();

            String backurl = getPara("backurl");
            if (StringUtils.isBlank(backurl)) {
                renderJson(JsonResult.buildSuccess(code));
            } else {
                if (backurl.contains("?")) {
                    backurl += "&ssoCode=" + code + "&userId=" + upmsUser.getId();
                } else {
                    backurl += "?ssoCode=" + code + "&userId=" + upmsUser.getId();
                }
            }

            redirect(backurl);
            return;

        }

        renderJson(ret);
    }

    @Before(POST.class)
    @EmptyValidate(value = {
            @Form(name = "code", message = "参数错误"),
    }, renderType = ValidateRenderType.JSON)
    public void code(String code) {
        Object codeCache = null; // 获取缓存全局code

        if (codeCache == null) {
            renderJson(JsonResult.buildError("invalid"));
        } else {
            renderJson(JsonResult.buildSuccess("success"));
        }
    }


    public void logout() {
        // shiro退出登录
        SecurityUtils.getSubject().logout();
        // 跳回原地址
        String redirectUrl = getRequest().getHeader("Referer");
        if (null == redirectUrl) {
            redirectUrl = "/";
        }

        redirect(redirectUrl);
    }
}
```

# ORM
## 配置
在使用数据库之前，需要给Jboot应用做一些配置，实际上，在任何需要用到数据库的应用中，都需要给应用程序做一些配置，让应用程序知道去哪里读取数据。

由于Jboot的数据库读取是依赖于JFinal，所以实际上只要是JFinal支持的数据库类型，Jboot都会支持，比如常用的数据库类型有：

* Mysql
* Oracle
* SqlServer
* postgresql
* sqlite
* 其他标准的数据库

在Jboot应用连接数据库之前，我们需要在resources目录下创建一个jboot.properties配置文件，并在jboot.properties编写内容如下：

```xml
jboot.datasource.type=mysql
jboot.datasource.url=jdbc:mysql://127.0.0.1:3306/jbootdemo
jboot.datasource.user=root
jboot.datasource.password=your_password
```

其中：

* jboot.datasource.type 是配置数据库类型
* jboot.datasource.url 是数据库请求URL地址
* jboot.datasource.user 是数据库需要的账号
* jboot.datasource.password 是数据库需要的密码

### 高级配置
除了 `type`，`url`，`user`，`password`四个配置以外，jbootdatasource 还支持以下配置：

* jboot.datasource.name 数据源的名称
* jboot.datasource.driverClassName 驱动类名
* jboot.datasource.connectionInitSql 连接初始化Sql
* jboot.datasource.poolName 线程池名称
* jboot.datasource.cachePrepStmt 缓存启用
* jboot.datasource.prepStmtCacheSize 缓存大小
* jboot.datasource.prepStmtCacheSqlLimit 缓存限制
* jboot.datasource.maximumPoolSize 线程池大小
* jboot.datasource.sqlTemplatePath sql文件路径
* jboot.datasource.sqlTemplate sql文件，多个用英文逗号隔开
* jboot.datasource.table 该数据源对应的表名，多个表用英文逗号隔开

更多的具体使用，特别是name、table等在分库分表章节会讲到。


## Model
model是MVC设计模式中的M，但同时每个model也会对应一个数据库表，它充当 MVC 模式中的 Model 部分。以下是Model 定义示例代码：

```java
public class User extends JbootModel<User> {
	public static final User dao = new User().dao();
}
```

以上代码中的 User 通过继承 Model，便立即拥有的众多方便的操作数据库的方法。在 User中声明的 dao 静态对象是为了方便查询操作而定义的，该对象并不是必须的。同时，model无需定义 getter、setter 方法，无需 XML 配置，极大降低了代码量。

以下是model常见的用法：

```java
// 创建name属性为James,age属性为25的User对象并添加到数据库
new User().set("name", "James").set("age", 25).save();
// 删除id值为25的User
User.dao.deleteById(25);
// 查询id值为25的User将其name属性改为James并更新到数据库
User.dao.findById(25).set("name", "James").update();
// 查询id值为25的user, 且仅仅取name与age两个字段的值
User user = User.dao.findByIdLoadColumns(25, "name, age");
// 获取user的name属性
String userName = user.getStr("name");
// 获取user的age属性
Integer userAge = user.getInt("age");
// 查询所有年龄大于18岁的user
List<User> users = User.dao.find("select * from user where age>18");
// 分页查询年龄大于18的user,当前页号为1,每页10个user
Page<User> userPage = User.dao.paginate(1, 10, "select *", "from user
where age > ?", 18);
```

**注意：**User 中定义的 public static final User dao 对象是全局共享的，**只能** 用于数据库查询，**不能** 用于数据承载对象。数据承载需要使用 new User().set(…)来实现。

### @Table注解
@Table注解是给Model使用的，表示让Model映射到哪个数据库表，使用代码如下：

```java
@Table(tableName = "user", primaryKey = "id")
public class User extends JbootModel <Company> {
	
}
```
值得注意的是：

在Jboot应用中，我们几乎感受不到@Table这个注解的存在，因为这部分完全是代码生成器生成的，关于代码生成器，请查看 代码生成器章节。

## Db + Record 模式
Db 类及其配套的 Record 类，提供了在 Model 类之外更为丰富的数据库操作功能。使用Db 与 Record 类时，无需对数据库表进行映射，Record 相当于一个通用的 Model。以下为 Db +Record 模式的一些常见用法：

```java
// 创建name属性为James,age属性为25的record对象并添加到数据库
Record user = new Record().set("name", "James").set("age", 25);
Db.save("user", user);
// 删除id值为25的user表中的记录
Db.deleteById("user", 25);
// 查询id值为25的Record将其name属性改为James并更新到数据库
user = Db.findById("user", 25).set("name", "James");
Db.update("user", user);
// 获取user的name属性
String userName = user.getStr("name");
// 获取user的age属性
Integer userAge = user.getInt("age");
// 查询所有年龄大于18岁的user
Page<Record> userPage = Db.paginate(1, 10, "select *", "from user where
age > ?", 18);
```

或者，事务操作：

```java
boolean succeed = Db.tx(new IAtom(){
		public boolean run() throws SQLException {
		int count = Db.update("update account set cash = cash - ? where
		id = ?", 100, 123);
		int count2 = Db.update("update account set cash = cash + ? where
		id = ?", 100, 456);
		return count == 1 && count2 == 1;
	}
});
```
以上两次数据库更新操作在一个事务中执行，如果执行过程中发生异常或者 run()方法返回 false，则自动回滚事务。

## 更多
请参考JFinal的文档：http://download.jfinal.com/download/3.2/jfinal-3.2-manual.pdf 

## 多数据源
在Jboot中，使用多数据源非常简单。

在以上章节里，我们知道，要连接数据库需要做如下配置：

```xml
jboot.datasource.type=mysql
jboot.datasource.url=jdbc:mysql://127.0.0.1:3306/jbootdemo
jboot.datasource.user=root
jboot.datasource.password=your_password
```

假设我们再增加两个数据源，只需要在jboot.properties文件在添加如下配置即可：

```xml
jboot.datasource.a1.type=mysql
jboot.datasource.a1.turl=jdbc:mysql://127.0.0.1:3306/jboot1
jboot.datasource.a1.tuser=root
jboot.datasource.a1.tpassword=your_password

jboot.datasource.a2.type=mysql
jboot.datasource.a2.turl=jdbc:mysql://127.0.0.1:3306/jboot2
jboot.datasource.a2.tuser=root
jboot.datasource.a2.tpassword=your_password
```

这表示，我们又增加了数据源`a1`和数据源`a2`，在使用的时候，我们只需要做一下使用：

```java
Company company = new Company();
company.setCid("1");
company.setName("name");

company.use("a1").save();
```
`company.use("a1").save();`表示使用数据源`a1`进行保存。

**值得注意的是：**

在多数据源应用中，很多时候，我们一个Model只有对应一个数据源，而不是一个Model对应多个数据源。假设Company只有在`a1`数据源中存在，在其他数据源并不存在，我们需要把`a1`数据源的配置修改如下：

```xml
jboot.datasource.a1.type=mysql
jboot.datasource.a1.url=jdbc:mysql://127.0.0.1:3306/jboot1
jboot.datasource.a1.user=root
jboot.datasource.a1.password=your_password
jboot.datasource.a1.table=company

jboot.datasource.a2.type=mysql
jboot.datasource.a2.url=jdbc:mysql://127.0.0.1:3306/jboot2
jboot.datasource.a2.user=root
jboot.datasource.a2.password=your_password
jboot.datasource.a1.table=user,xxx(其他非company表)
```
这样，company在`a1`数据源中存在，Jboot在初始化的时候，并不会去检查company在其他数据源中是否存在，同时，代码操作company的时候，不再需要use，代码如下：

```java
Company company = new Company();
company.setCid("1");
company.setName("name");

//company.use("a1").save();
company.save();
```
代码中不再需要 `use("a1")` 指定数据源，因为company只有一个数据源。


## 分库和分表
在Jboot中，分表是通过sharding-jdbc（ 网址：https://github.com/shardingjdbc/sharding-jdbc） 来实现的，所以，在了解Jboot的分表之前，请先阅读了解sharding-jdbc的配置信息。

分库分表相关demo: [点击这里](./src/test/java/sharding)

### 分库
分库意味你有多个数据库，每个数据库会对应一个数据源。

例如，我们的应用有三个数据库，分别是 db1,db2,db3，那么需要我们在 jboot.properties 配置文件里配置上三个数据，配置如下：

```
jboot.datasource.db1.url = jdbc:mysql://127.0.0.1:3306/db1
jboot.datasource.db1.user = root
jboot.datasource.db1.password = 

jboot.datasource.db2.url = jdbc:mysql://127.0.0.1:3306/db2
jboot.datasource.db2.user = root
jboot.datasource.db2.password = 

jboot.datasource.db3.url = jdbc:mysql://127.0.0.1:3306/db3
jboot.datasource.db3.user = root
jboot.datasource.db3.password = 

```

我们希望在分库的时候，通过Model的主键ID进行hashcode进行取模，决定分库。因此需要编写分库策略，代码如下：

```java
public class UserDatabaseShardingStrategyConfig implements ShardingStrategyConfiguration {

    @Override
    public ShardingStrategy build() {
        return shardingStrategy;
    }


    private ShardingStrategy shardingStrategy = new ShardingStrategy() {

        @Override
        public Collection<String> getShardingColumns() {
            //根据id进行分库
            return Sets.newHashSet("id");
        }

        @Override
        public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue> shardingValues) {
            ListShardingValue shardingValue = (ListShardingValue) shardingValues.stream().findFirst().get();

            String dbName = "db" + Math.abs(shardingValue.getValues().iterator().next().toString().hashCode()) % 3;

            System.out.println("插入数据到库：" + dbName);

            //返回通过计算得到的表
            return Sets.newHashSet(dbName);

        }
    };

}
```

编写好分库策略后，需要给Model配置上分库策略：

```java
@Table(tableName = "tb_user",
        primaryKey = "id",
         // 具体的表tb_user${0..2} 表示有三张表 tb_user0,tb_user1,tb_user2,
         // main 是默认数据源的名称
        actualDataNodes = "main.tb_user${0..2}",
        //分表策略
        databaseShardingStrategyConfig = UserDatabaseShardingStrategyConfig.class 
)
public class UserModel extends JbootModel<UserModel> {


   //geter setter
}

```


### 分表




#### demos

例如：有一个userModel，我们希望能进行分为三张表，通过id的hashcode进行取模，代码如下：


```java

@Table(tableName = "tb_user",
        primaryKey = "id",
         // 具体的表tb_user${0..2} 表示有三张表 tb_user0,tb_user1,tb_user2,
         // main 是默认数据源的名称
        actualDataNodes = "main.tb_user${0..2}",
        //分表策略
        tableShardingStrategyConfig = UserTableShardingStrategyConfig.class 
)
public class UserModel extends JbootModel<UserModel> {


    //geter setter
}

```

编写UserModel的分表策略  UserTableShardingStrategyConfig，代码如下：

```java
public class UserTableShardingStrategyConfig implements ShardingStrategyConfiguration {

    @Override
    public ShardingStrategy build() {
        return shardingStrategy;
    }


    private ShardingStrategy shardingStrategy = new ShardingStrategy() {

        @Override
        public Collection<String> getShardingColumns() {
            //根据id进行分表
            return Sets.newHashSet("id");
        }

        @Override
        public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue> shardingValues) {
            ListShardingValue shardingValue = (ListShardingValue) shardingValues.stream().findFirst().get();

            String tableName = "tb_user" + Math.abs(shardingValue.getValues().iterator().next().toString().hashCode()) % 3;

            System.out.println("插入数据到表：" + tableName);

            //返回通过计算得到的表
            return Sets.newHashSet(tableName);

        }
    };

}
```

编写配置文件：

```
jboot.datasource.type=mysql
jboot.datasource.url=jdbc:mysql://127.0.0.1:3306/jbootsharding
jboot.datasource.user=root
jboot.datasource.password=
jboot.datasource.shardingEnable=true
```

进行UserModel保存到数据库

```java
@RequestMapping("/sharding")
public class ShardingController extends JbootController {

    public void index() {
        UserModel user = new UserModel();
        user.setId(StringUtils.uuid());
        user.setName("Michael yang");

        user.save();

        renderText("插入数据成功，请查看数据库...");

    }

    public static void main(String[] args) {
        Jboot.run(args);
    }
}
```

具体demo请参考：

https://gitee.com/fuhai/jboot/tree/master/src/test/java/sharding


# AOP

## Google Guice
Jboot 的AOP功能，是使用了Google的Guice框架来完成的，通过AOP，我们可以轻易的在微服务体系中监控api的调用，轻易的使用@Cacheable，@CachePut，@CacheEvict等注解完成对代码的配置。

## @Inject 和 @Bean
和Spring一样，Jboot是通过注解 `@Inject` 来对变量进行赋值注入的，例如：

```java
public class AopDemo extends JbootController {

    @Inject
    CategoryService service;

    public void index() {
        renderHtml("service:" + service.hello(""));
    }
      
    public static void main(String[] args) {
        Jboot.run(args);
    }
}
```
但是，必须强调的是：`CategoryService`接口能够被注入，其实必须有实现类，同时实现类必须通过 `@Bean` 进行配置，例如：


接口代码：

```java
public interface CategoryService {
    public String hello(String text);
}
```

实现类代码：

```java
@Bean //必须通过 @Bean 进行配置，让CategoryServiceImpl处于自动暴露状态
public class CategoryServiceImpl implements CategoryService {
    @Override
    public String hello(String text) {
        return "CategoryServiceImpl say hello " + text;
    }

}
```

但是，当`@Inject`注入的不是一个接口类，而是一个普通类，那么无需 `@Bean` 的配合。例如：

```java
public class AopDemo extends JbootController {

    @Inject
    MyServiceImpl myService;

    public void index() {
        renderHtml("service:" + myService);
    }
      
    public static void main(String[] args) {
        Jboot.run(args);
    }
}
```
在以上代码中，由于 `MyServiceImpl` 不是已经接口，而是一个类，此时无需在 `MyServiceImpl` 这个类上配置
`@Bean` 注解。



当一个接口有多个实现类的时候，可以通过配合`@Named`配合进行注入，例如：

```java
public class AopDemo extends JbootController {

    @Inject
    CategoryService service;

    @Inject
    @Named("myCategory") //通过@Named指定使用哪个实现类
    CategoryService nameservice;

    public void index() {
        renderHtml("service:" + service.hello("") 
        + "<br /> nameservice:" + nameservice.hello(""));
    }
      
    public static void main(String[] args) {
        Jboot.run(args);
    }
}
```

以下是实现类的代码：

```java
@Bean
public class CategoryServiceImpl implements CategoryService {
    @Override
    public String hello(String text) {
        return "CategoryServiceImpl say hello " + text;
    }
}
```

```java
@Bean(name = "myCategory")
public class NamedCategoryServiceImpl implements CategoryService {
    @Override
    public String hello(String text) {
        return "NamedCategoryServiceImpl say hello " + text;
    }
}
```

两个类都实现了`CategoryService`接口，不同的是 `NamedCategoryServiceImpl` 实现类在配置 `@Bean` 的时候传了参数 `name = "myCategory"`，这样，注入的时候就可以配合 `@Named` 进行对实现类的选择。

## @RpcService
通过以上 `@Inject` 和 `@Bean` 的配合，我们很方便的在项目中自由的对代码进行注入，但是，如果注入的是一个RPC的服务，那么需要通过 `@RpcService` 进行注入。更多关于RPC部分，请查看RPC章节。





# RPC远程调用
在Jboot中，RPC远程调用是通过新浪的motan、或阿里的dubbo来完成的。计划会支持 grpc和thrift等。


### 使用步骤：
#### 第一步：配置Jboot.properties文件，内容如下：

```java
#默认类型为 motan (支持:dubbo,计划支持 grpc 和 thrift)
jboot.rpc.type = motan
#发现服务类型为 consul ，支持zookeeper。
jboot.rpc.registryType = consul
jboot.rpc.registryAddress = 127.0.0.1:8500
```

#### 第二步：定义接口

```java
public interface HelloService {
    public String hello(String name);
}
```

#### 第三步：通过@JbootrpcService注解暴露服务到注册中心

```java
@JbootrpcService
public class myHelloServiceImpl  implements HelloService {
    public String hello(String name){
         System.out.println("hello" + name);
         return "hello ok";
    }
}
```

#### 第四步：客户调用

```java
 HelloService service = Jboot.me().service(HelloService.class);
 service.hello("michael");
```
如果是在Controller中，也可以通过 @JbootrpcService 注解来获取服务，代码如下：

```java
public class MyController extends JbootController{
    
    @JbootrpcService
    HelloService service ;
    
    public void index(){
        String text = service.hello();
        renderText(text);
    }
    
}
```

### 配置中心

##### 下载consul
https://www.consul.io 

##### 启动consul

```java
consul agent -dev
```

允许其他机器访问consul:

```java
consul agent -dev -client=本机局域网IP
```

#### zookeeper
##### 下载zookeeper
http://zookeeper.apache.org/releases.html

##### 启动zookeeper
下载zookeeper后，进入zookeeper目录下，找到 conf/zoo_example.cfg，重命名为 zoo.cfg。

zoo.cfg 内容如下：

```
tickTime=2000
dataDir=/var/lib/zookeeper
clientPort=2181
```

在终端模式下，进入 zookeeper的更目录，执行：

```java
bin/zkServer.sh start
```
关于zookeeper更多的内容，请查看 http://zookeeper.apache.org 和 http://zookeeper.apache.org/doc/trunk/zookeeperStarted.html

# Redis操作
## Redis简介
Redis 是完全开源免费的，遵守BSD协议，是一个高性能的key-value数据库。

Redis 与其他 key - value 缓存产品有以下三个特点：

* Redis支持数据的持久化，可以将内存中的数据保存在磁盘中，重启的时候可以再次加载进行使用。
* Redis不仅仅支持简单的key-value类型的数据，同时还提供list，set，zset，hash等数据结构的存储。
* Redis支持数据的备份，即master-slave模式的数据备份。
	
Redis 优势:

* 性能极高 – Redis能读的速度是110000次/s,写的速度是81000次/s 。
* 丰富的数据类型 – Redis支持二进制案例的 Strings, Lists, Hashes, Sets 及 Ordered Sets 数据类型操作。
* 原子 – Redis的所有操作都是原子性的，意思就是要么成功执行要么失败完全不执行。单个操作是原子性的。多个操作也支持事务，即原子性，通过MULTI和EXEC指令包起来。
* 丰富的特性 – Redis还支持 publish/subscribe, 通知, key 过期等等特性。


## Redis的使用
在使用Reids之前，先进行Redis配置，配置内容如下：

```java
jboot.redis.host=127.0.0.1
jboot.redis.password=xxxx
```

配置后，就可以通过如下代码获取 JbootRedis 对redis进行操作：

```java
JbootRedis redis = Jboot.me().getReids();
redis.set("key1","value1");

String value = redis.get("key1");

System.out.println(value); // 输出 value1
```

## Redis操作系列方法

| 指令（方法）         |  描述  |
| ------------- | -----|
| set(Object key, Object value);| 存放 key value 对到 redis，对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。如果 key 已经持有其他值， SET 就覆写旧值，无视类型。 |
| setnx  | 当且仅当 key 不存在能成功设置|
| setWithoutSerialize  | 存放 key value 对到 redis，不对value进行序列化，经常用在设置某些 数字或字符串类型的数据 |
| setex(Object key, int seconds, Object value)  |存放 key value 对到 redis，并将 key 的生存时间设为 seconds (以秒为单位) |
| get  |  返回 key 所关联的 value 值 |
| getWithoutSerialize  |  返回 key 所关联的 value 值，不对value近反序列化 |
| del(Object key)  | 删除给定的一个 key |
| del(Object... keys)  | 删除给定的多个 key |
| keys  | 查找所有符合给定模式 pattern 的 key，例如：KEYS h?llo 匹配 hello ， hallo 和 hxllo 等 |
| mset  | 同时设置一个或多个 key-value 对，例如：mset("k1", "v1", "k2", "v2") |
| mget  | 返回所有(一个或多个)给定 key 的值 |
| decr  | 将 key 中储存的数字值减一 |
| decrBy(Object key, long longValue)  | 将 key 所储存的值减去减量 value |
| incr  | 将 key 中储存的数字值增一 |
| incrBy(Object key, long value)  | 将 key 所储存的值加上增量 value |
| exists  | 检查给定 key 是否存在 |
| randomKey  | 从当前数据库中随机返回(不删除)一个 key |
| rename  | 将 key 改名为 newkey，当 newkey 已经存在时， RENAME 命令将覆盖旧值 |
| move  | 将当前数据库的 key 移动到给定的数据库 db 当中 |
| migrate  | 将 key 原子性地从当前实例传送到目标实例的指定数据库上 |
| select  | 切换到指定的数据库，数据库索引号 index 用数字值指定，以 0 作为起始索引值 |
| expire  | 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除 |
| expireAt  | expireAt 的作用和 expire 类似，都用于为 key 设置生存时间。不同在于 expireAt 命令接受的时间参数是 UNIX 时间戳(unix timestamp) |
| pexpire  | 这个命令和 expire 命令的作用类似，但是它以毫秒为单位设置 key 的生存时间 |
| pexpireAt  | 这个命令和 expireAt 命令类似，但它以毫秒为单位设置 key 的过期 unix 时间戳 |
| getSet  | 将给定 key 的值设为 value ，并返回 key 的旧值(old value) |
| persist  | 移除给定 key 的生存时间 |
| type  | 返回 key 所储存的值的类型 |
| ttl  | 以秒为单位，返回给定 key 的剩余生存时间 |
| pttl  | 这个命令类似于 TTL 命令，但它以毫秒为单位返回 key 的剩余生存时间 |
| objectRefcount  | 对象被引用的数量 |
| objectIdletime  | 对象没有被访问的空闲时间 |
| hset(Object key, Object field, Object value)  | 将哈希表 key 中的域 field 的值设为 value |
| hmset(Object key, Map<Object, Object> hash)  | 同时将多个 field-value (域-值)对设置到哈希表 key 中 |
| hget(Object key, Object field)  | 返回哈希表 key 中给定域 field 的值 |
| hmget(Object key, Object... fields)  | 返回哈希表 key 中，一个或多个给定域的值 |
| hdel  |  删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略 |
| hexists  | 查看哈希表 key 中，给定域 field 是否存在 |
| hgetAll  | 返回哈希表 key 中，所有的域和值 |
| hvals  | 返回哈希表 key 中所有域的值 |
| hkeys  | 返回哈希表 key 中的所有域 |
| hlen  | 返回哈希表 key 中域的数量 |
| hincrBy(Object key, Object field, long value)  | 为哈希表 key 中的域 field 的值加上增量 value |
| hincrByFloat  | 为哈希表 key 中的域 field 加上浮点数增量 value |
| lindex  | 返回列表 key 中，下标为 index 的元素 |
| getCounter  | 获取记数器的值 |
| llen  | 返回列表 key 的长度 |
| lpop  | 移除并返回列表 key 的头元素 |
| lpush  | 将一个或多个值 value 插入到列表 key 的表头 |
| lset  | 将列表 key 下标为 index 的元素的值设置为 value |
| lrem  | 根据参数 count 的值，移除列表中与参数 value 相等的元素 |
| lrange(Object key, long start, long end)  | 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定 |
| ltrim  | 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除 |
| rpop  | 移除并返回列表 key 的尾元素 |
| rpoplpush  | 命令 rpoplpush 在一个原子时间内，执行以下两个动作：1：将列表中的最后一个元素(尾元素)弹出，并返回给客户端。2：将弹出的元素插入到列表 ，作为列表的的头元素 |
| rpush  | 将一个或多个值 value 插入到列表 key 的表尾(最右边) |
| blpop(Object... keys)  | blpop 是列表的阻塞式(blocking)弹出原语 |
| blpop(Integer timeout, Object... keys)  | blpop 是列表的阻塞式(blocking)弹出原语 |
| brpop(Object... keys)   | 列表的阻塞式(blocking)弹出原语 |
| brpop(Integer timeout, Object... keys)  | 列表的阻塞式(blocking)弹出原语 |
| ping  | 使用客户端向服务器发送一个 PING ，如果服务器运作正常的话，会返回一个 PONG  |
| sadd  | 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略 |
| scard  | 返回集合 key 的基数(集合中元素的数量) |
| spop  | 移除并返回集合中的一个随机元素 |
| smembers  | 返回集合 key 中的所有成员|
| sismember  | 判断 member 元素是否集合 key 的成员 |
| sinter  | 返回多个集合的交集，多个集合由 keys 指定 |
| srandmember  | 返回集合中的一个随机元素 |
| srandmember  | 返回集合中的 count 个随机元素 |
| srem  |  移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略 |
| sunion  | 返回多个集合的并集，多个集合由 keys 指定 |
| sdiff  | 返回一个集合的全部成员，该集合是所有给定集合之间的差集 |
| zadd(Object key, double score, Object member)  |  将一个或多个 member 元素及其 score 值加入到有序集 key 当中 |
| zadd(Object key, Map<Object, Double> scoreMembers)  | 同上|
| zcard  | 返回有序集 key 的基数 |
| zcount  | 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量 |
| zincrby  | 为有序集 key 的成员 member 的 score 值加上增量 increment  |
| zrange  | 返回有序集 key 中，指定区间内的成员 |
| zrevrange  |  返回有序集 key 中，指定区间内的成员 |
| zrangeByScore  | 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员 |
| zrank  | 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列 |
| zrevrank  | 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序 |
| zrem  |  移除有序集 key 中的一个或多个成员，不存在的成员将被忽略 |
| zscore  | 返回有序集 key 中，成员 member 的 score 值 |
| publish(String channel, String message)  | 发布一条消息 |
| publish(byte[] channel, byte[] message)  | 发布一条消息 |
| subscribe(JedisPubSub listener, final String... channels)  | 订阅消息 |
| subscribe(BinaryJedisPubSub binaryListener, final byte[]... channels)  | 订阅消息 |


## Redis扩展

JbootRedis 是通过 `jedis` 或者 `JedisCluster` 进行操作的，如果想扩展自己的方法。可以直接获取 `jedis` （或`JedisCluster`) 对 Redis 进行操作，获取  `jedis`（或`JedisCluster`) 的代码如下：

```java
JbootRedis redis = Jboot.me().getReids();

//单机模式下
JbootRedisImpl redisImpl = (JbootRedisImpl)redis;
Jedis jedis = redisImpl.getJedis();

//集群模式下
JbootClusterRedisImpl redisImpl = (JbootClusterRedisImpl)redis;
JedisCluster jedis = redisImpl.getJedisCluster();
```
## Redis集群
在单机模式下，配置文件如下：

```java
jboot.redis.host=127.0.0.1
jboot.redis.password=xxxx
```

在集群模式下，只需要在 jboot.redis.host 配置为多个主机即可，例如：


```java
## 多个IP用英文逗号隔开
Jboot.redis.host=192.168.1.33,192.168.1.34
jboot.redis.password=xxxx
```

# MQ消息队列
Jboot 内置整个了MQ消息队列，使用MQ非常简单

#### 第一步：配置jboot.properties文件，内容如下：
```java
#默认为redis (支持: redis,activemq,rabbitmq,hornetq,aliyunmq等 )
jboot.mq.type = redis
jboot.mq.redis.host = 127.0.0.1
jboot.mq.redis.password =
jboot.mq.redis.database =
```

#### 第二步：在服务器A中添加一个MQ消息监听器

```java
Jboot.me().getMq().addMessageListener(new JbootmqMessageListener(){
        @Override
        public void onMessage(String channel, Object obj) {
           System.out.println(obj);
        }
}, channel);
```

#### 第三步：服务器B发送一个消息

```java
 Jboot.me().getMq().publish(yourObject, toChannel);
```

#### 注意：服务器A和服务器B在jboot.properties上应配置相同的内容。

## RedisMQ
## ActiveMQ

# Cache缓存
Jboot中内置支持了ehcache、redis和 一个基于ehcache、redis研发的二级缓存ehredis，在使用Jboot缓存之前，先配置完成缓存的配置。

### 使用步骤
#### 第一步：配置jboot.properties文件，内容如下：

```java
#默认类型为ehcache ehcache (支持:ehcache,redis,ehredis)
jboot.cache.type = redis
jboot.cache.redis.host = 127.0.0.1
jboot.cache.redis.password =
jboot.cache.redis.database =
```
备注：ehredis 是一个基于ehcache和redis实现的二级缓存框架。

#### 第二步：使用缓存

```java
Jboot.me().getCache().put("cacheName", "key", "value");
```

### 注意事项
Jboot的分布式session是通过缓存实现的，所以如果要启用Jboot的分布式session，请在缓存中配置类型为redis或者ehredis。


## ehcache
## redis
## ehredis

# http客户端
Jboot内置了一个轻量级的http客户端，可以通过这个客户端方便的对其他第三方http服务器进行数据请求和下载等功能。

### Get请求

```java
@Test
public void testHttpGet(){
    String html = Jboot.httpGet("https://www.baidu.com");
    System.out.println(html);
}
```

或者

```java
@Test
public void testHttpPost(){
    Map<String, Object> params  = new HashMap<>();
    params.put("key1","value1");
    params.put("key2","value2");


    String html = Jboot.httpGet("http://www.oschina.net/",params);
    System.out.println(html);
}
```

### Post请求

```java
@Test
public void testHttpPost(){
    String html = Jboot.httpPost("http://www.xxx.com");
    System.out.println(html);
}
```

或者

```java
@Test
public void testHttpPost(){
    Map<String, Object> params  = new HashMap<>();
    params.put("key1","value1");
    params.put("key2","value2");


    String html = Jboot.httpPost("http://www.oschina.net/",params);
    System.out.println(html);
}
```

### 文件上传

```java
@Test
public void testHttpUploadFile(){
    Map<String, Object> params  = new HashMap<>();
    params.put("file1",file1);
    params.put("file2",file2);


    String html = Jboot.httpPost("http://www.oschina.net/",params);
    System.out.println(html);
}
```
备注：文件上传其实和post提交是一样的，只是params中的参数是文件。

### 文件下载

```java
@Test
public void testHttpDownload() {

    String url = "http://www.xxx.com/abc.zip";
    File downloadToFile = new File("/xxx/abc.zip");

    JbootHttpRequest request = JbootHttpRequest.create(url, null, JbootHttpRequest.METHOD_GET);
    request.setDownloadFile(downloadToFile);

    JbootHttpResponse response = Jboot.me().getHttp().handle(request);

    if (response.isError()){
        downloadToFile.delete();
    }

    System.out.println(downloadToFile.length());
}
```



# metric数据监控
Jboot的监控机制是通过metric来来做监控的，要启用metric非常简单，通过在jboot.properties文件配置上`jboot.metric.url`就可以启用metric。

例如

```xml
jboot.metric.url = /metric.html
```
我们就可以通过访问 `http://host:port/metric.html` 来访问到metric数据情况。

### 添加metric数据
默认通过Url访问到的数据是没有具体内容，因为metric无法得知要显示什么样的数据内容。例如，我们要统计某个action的用户访问量，可以通过在action里编写如下代码。

```java
public void myaction() {

    Jboot.me().getmetric().counter("myaction").inc();

    renderText("my action");
}
```

当我们访问myaction这个地址后，然后再通过浏览器`http://host:port/metric.html`访问，我们就能查看到如下的json数据。

```js
{
	"version": "3.1.3",
	"gauges": {},
	"counters": {
		"myaction": {
				"count": 1
			}
	},
	"histograms": {},
	"meters": {},
	"timers": {}
}
```
当再次访问`myaction`后，count里面的值就变成2了。

### metric与Ganglia


### metric与Grafana

### metric与jmx
metric与jmx集成非常简单，只需要在jboot.properties文件添加如下配置：

```xml
jboot.metric.jmxReporter = true
```
然后，我们就可以通过`JConsole`或者`VisualVM`进行查看了。


# 容错与隔离

### hystrix配置
Jboot的容错、隔离和降级服务、都是通过`Hystrix`来实现的。在RPC远程调用中，Jboot已经默认开启了Hystrix的监控机制，对数默认错误率达到50%的service则立即返回，不走网络。


### Hystrix Dashboard 部署
要查看hystrix的数据，我们需要部署`Hystrix Dashboard`。然后通过`Hystrix Dashboard`来查看。

通过Gradle来编译：

```
$ git clone https://github.com/Netflix/Hystrix.git
$ cd Hystrix/hystrix-dashboard
$ ../gradlew appRun
> Building > :hystrix-dashboard:appRun > Running at http://localhost:7979/hystrix-dashboard
```

或者通过docker来运行hystrix-dashboard:

```java
docker run --rm -ti -p 7979:7979 kennedyoliveira/hystrix-dashboard
```

运行`hystrix-dashboard`成功后，通过浏览器输入`http://localhost:7979/hystrix-dashboard`就可以看到如下图显示：


 ![](https://github.com/Netflix/Hystrix/wiki/images/dashboard-home.png)


### 通过 Hystrix Dashboard 查看数据
接下来，我们需要配置jboot应用的hystrix监控地址，配置如下：

```
jboot.hystrix.url = /hystrix.stream
```
然后在上面图片中，填写url地址为：`http://host:port/hystrix.stream`,并点击`monitor stream`按钮,就可以看到如下图显示，所以的远程调用方法都统计到了。
 
 
 **注意：** 如果是通过docker启动的`hystrix-dashboard`，`http://host:port/hystrix.stream`中的host一定是本机的真实IP地址。

 
 ![](https://github.com/Netflix/Hystrix/wiki/images/hystrix-dashboard-netflix-api-example-iPad.png)

### 自定义监控隔离

# Opentracing数据追踪
Jboot在分布式下，对数据的追踪是通过opentracing来实现的，opentracing官方地址（http://opentracing.io ）

### Opentracing简介
OpenTracing（http://opentracing.io ）通过提供平台无关、厂商无关的API，使得开发人员能够方便的添加（或更换）追踪系统的实现。OpenTracing正在为全球的分布式追踪，提供统一的概念和数据标准。

目前，已经有了诸如 UBER，LightStep，Apple，yelp，workiva等公司在跟进，以及开源团队：ZIPKIN，appdash，TRACER，JAEGER，GRPC等的支持。

已经支持 opentracing-api的开源库有：Zipkin，Jaeger（Uber公司的），Appdash，LightStep，Hawkular，Instana，sky-walking，inspectIT，stagemonitor等。具体地址请查看：http://opentracing.io/documentation/pages/supported-tracers.html

### Opentracing在Jboot上的配置
在jboot中启用opentracing非常简单，只需要做如下配置：

```java
jboot.tracing.type=zipkin
jboot.tracing.serviceName=service1
jboot.tracing.url=http://127.0.0.1:9411/api/v2/spans
```
同步简单几个配置，就可以启动opentracing对数据的追踪，并把数据传输到对应的服务器上，例如使用的是zipkin，那么就会传输到zipkin的server上。

### Zipkin
zipkin官网： http://zipkin.io/ 

#### zipkin快速启动

```java
wget -O zipkin.jar 'https://search.maven.org/remote_content?g=io.zipkin.java&a=zipkin-server&v=LATEST&c=exec'
java -jar zipkin.jar
```

或者通过docker来运行：

```java
docker run -d -p 9411:9411 openzipkin/zipkin
```

或者 自己编译zipkin源代码，然后通过以下方式执行：

```java
# Build the server and also make its dependencies
$ ./mvnw -DskipTests --also-make -pl zipkin-server clean install
# Run the server
$ java -jar ./zipkin-server/target/zipkin-server-*exec.jar
```

#### 使用zipkin
通过以上步骤，把zipkin启动后，只需要在 jboot.properties 文件把 jboot.tracing.url 的属性修改为zipkin的地址即可：

```
jboot.tracing.url = http://127.0.0.1:9411/api/v2/spans
```

配置之后，我们就可以通过zipkin来查看jboot追踪的数据了。
![](http://zipkin.io/public/img/web-screenshot.png)

### SkyWalking
SkyWalking官网：http://skywalking.org ，Skywalking为国人开发，据说目前 **华为开发云**、**当当网** 等已经 加入 Skywalking 生态系统，具体查看：https://www.oschina.net/news/89756/devcloud-dangdang-join-skywalking 

#### SkyWalking快速启动
#### 使用SkyWalking

### 其他


# 统一配置中心
在jboot中，已经内置了统一配置中心，当中心配置文件修改后，分布式服务下的所有有用的额配置都会被修改。在某些情况下，如果统一配置中心出现宕机等情况，微服务将会使用本地配置文件当做当前配置信息。

## 部署统一配置中心服务器
部署统一配置服务器非常简单，不需要写一行代码，把jboot.proerties的配置信息修改如下，并启动jboot，此时的jboot就已经是一个统一配置中心了。

```
jboot.config.serverEnable=true
jboot.config.path=/Users/michael/Desktop/test
```
在以上配置中，我们可以把所有的配置文件(.properties文件)放到目录 `/Users/michael/Desktop/test` 目录下，当该目录下新增配置文件、修改配置文件、删除配置文件都会通过http暴露出去。

当启动 jboot 后，我们可以通过浏览器输入 `http://127.0.0.1:8080/jboot/config`来查看配置情况，微服务客户端也是定时访问这个url地址来读取配置信息。


## 连接统一配置中心

要启用远程配置也非常简单，只需要在微服务添加下配置即可。

```
jboot.config.remoteEnable=true
jboot.config.remoteUrl=http://127.0.0.1:8080/jboot/config
```
当启用远程配置后，服务会优先使用远程配置，在远程配置未配置 或 宕机的情况下使用本地配置。

# Swagger api自动生成

## swagger简介

## swagger使用


### 第一步：配置并启用swagger
在 jboot.properties上添加如下配置：

```java
jboot.swagger.path=/swaggerui
jboot.swagger.title=Jboot API 测试
jboot.swagger.description=这真的真的真的只是一个测试而已，不要当真。
jboot.swagger.version=1.0
jboot.swagger.termsOfService=http://jboot.io
jboot.swagger.contact=email:fuhai999@gmail.com;qq:123456
jboot.swagger.host=127.0.0.1:8080 
```

### 第二步：下载swagger ui放到resource目录下
注意，这里一定要放在resource的 `swaggerui` 目录，因为以上的配置中是`jboot.swagger.path=/swaggerui`,当然可以通过这个配置来修改这个存放目录。

另：swagger ui 的下载地址是：https://github.com/swagger-api/swagger-ui，下载其 `dist` 目录即可，只需要这个目录里的文件。

### 第三步：通过注解配置Controller的api

代码如下：

```java
@RequestMapping("/swaggerTest")
@Api(description = "用户相关接口文档", basePath = "/swaggerTest", tags = "abc")
public class MySwaggerTestController extends JbootController {

    @ApiOperation(value = "用户列表", httpMethod = "GET", notes = "user list")
    public void index() {
        renderJson(Ret.ok("k1", "v1").set("name", getPara("name")));
    }


    @ApiOperation(value = "添加用户", httpMethod = "POST", notes = "add user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "form", dataType = "string", required = true),
            @ApiImplicitParam(name = "k1", value = "k1", paramType = "form", dataType = "string", required = true),
    })
    public void add(String username) {
        renderJson(Ret.ok("k1", "v1").set("username", username));
    }


}
```

### 第四步：浏览器访问swagger生成api文档
在第一步的配置中，因为`jboot.swagger.path=/swaggerui`，所以我们访问如下地址：`http://127.0.0.1:8080/swaggerui` 效果如下图所示。

![](http://oss.yangfuhai.com/markdown/jboot/swagger/01.png)
图片1

![](http://oss.yangfuhai.com/markdown/jboot/swagger/02.png)
图片2

在图片2中，我们可以输入参数，并点击 `Execute` 按钮进行测试。



# 其他

## SPI扩展
SPI的全名为Service Provider Interface。

### SPI具体约定
当服务的提供者，提供了服务接口的一种实现之后，在jar包的META-INF/services/目录里同时创建一个以服务接口命名的文件。该文件里就是实现该服务接口的具体实现类。而jboot装配这个模块的时候，就能通过该jar包META-INF/services/里的配置文件找到具体的实现类名，并装载实例化，完成模块的注入。

### Jboot SPI模块
在jboot中，一下模块已经实现了SPI机制。

- Jbootrpc
- JbootHttp
- JbootCache
- Jbootmq
- JbootSerializer

例如，在JbootCache中，内置了三种实现方案：ehcache、redis、ehredis。在配置文件中，我看可以通过 `jboot.cache.type = ehcache` 的方式来指定在Jboot应用中使用了什么样的缓存方案。

但是，在Jboot中，通过SPI机制，我们一样可以扩展出第4、第5甚至更多的缓存方案出来。

扩展步骤如下：

- 第一步：编写JbootCache的子类
- 第二步：通过@JbootSpi注解给刚刚编写的类设置上一个名字，例如：mycache
- 第三步：通过在jboot.properties文件中配置上类型为 mycache，配置代码如下：

```xml
jboot.cache.type = mycache
```

通过以上三步，我们就可以完成了对JbootCache模块的扩展，其他模块类似。

## JbootEvnet事件机制
为了解耦，Jboot内置了一个简单易用的事件系统，使用事件系统非常简单。

#### 第一步，注册事件的监听器。

```java
@EventConfig(action = {"event1","event2"})
public class MyEventListener implements JbootEventListener {
    
    public  void onEvent(JbootEvent event){
        Object data = event.getData();
        System.out.println("get data:"+data);
    }
}
```
通过 @EventConfig 配置 让MyEventListener监听上 event1和event2两个事件。

#### 第二步，在项目任何地方发生事件

```java
Jboot.sendEvent("event1",  object)
```



## 自定义序列化
自定义序列化是通过Jboot的SPI机制来实现的，请参考 [SPI扩展](#SPI扩展)。

## 配置文件

### 读取jboot.properties的配置信息
要读取jboot.properties的配置信息非常简单，例如我们配置内容如下：

```xml
jboot.myconfig.name=aaa
jboot.myconfig.passowrd=bbb
jboot.myconfig.age=10
```
要读取这个配置信息，我们需要定义我们的一个model类，并通过@PropertyConfig注解给我们的类配置上类与配置文件的对应关系，如下所示：

```java
@PropertyConfig(prefix="jboot.myconfig")
public class MyConfigModel{
    private String name;
    private String password;
    private int age;

    //getter setter 略
}
```

*注意：* 类名MyConfigModel随便取

编写好配置类MyConfigModel后，我们就可以通过如下代码来读取到配置信息：

```java
MyConfigModel config = Jboot.config(MyConfigModel.class);
```

### 读取自定义配置文件的配置信息

在以上章节中，我们已经知道了如何来读取jboot.properties的配置文件，在某些场景下，可能需要我们把我们的配置信息编写到一个独立的properties配置文件里面去，例如：在我们的项目中有一个叫 michael.properties 文件，文件的内容如下：

```xml
jboot.myconfig.name=aaa
jboot.myconfig.passowrd=bbb
jboot.myconfig.age=10
```

那么，一样的，我们需要编写一个model，并配置上@PropertyConfig注解，与读取jboot.properties文件不同的是，@PropertyConfig 需要添加上file配置，内容如下：

```java
@PropertyConfig(prefix="jboot.myconfig",file="michael.properties")
public class MyConfigModel{
    private String name;
    private String password;
    private int age;

    //getter setter 略
}
```

然后，和读取jboot.properties一样。


```java
MyConfigModel config = Jboot.config(MyConfigModel.class);
```

## 分布式session


## 代码生成器
Jboot内置了一个简易的代码生成器，可以用来生成model层和Service层的基础代码，在生成代码之前，请先配置jboot.properties关于数据库相关的配置信息。

### 使用步骤

#### 第一步：配置数据源
```xml
jboot.datasource.type=mysql
jboot.datasource.url=jdbc:mysql://127.0.0.1:3306/jbootdemo
jboot.datasource.user=root
jboot.datasource.password=your_password
```

#### 第二步：通过JbootModelGenerator生成model代码
```java
  public static void main(String[] args) {
  
  		//model 的包名
        String modelPackage = "io.jboot.test";
        
        JbootModelGenerator.run(modelPackage);

    }
```

#### 第三步：通过JbootServiceGenerator生成Service代码
```java
  public static void main(String[] args) {
  
  		//生成service 的包名
        String basePackage = "io.jboot.testservice";
        //依赖model的包名
        String modelPackage = "io.jboot.test";
        
        JbootServiceGenerator.run(basePackage, modelPackage);

    }
```

#### 其他
当没在jboot.properties文件配置数据源的时候，可以通过如下代码来使用：

```java
 public static void main(String[] args) {

        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        Jboot.setBootArg("jboot.datasource.user", "root");

        String basePackage = "io.jboot.codegen.service.test";
        String modelPackage = "io.jboot.codegen.test.model";
        JbootServiceGenerator.run(basePackage, modelPackage);

    }

```


# 项目构建
在Jboot中已经内置了高性能服务器undertow，undertow的性能比tomcat高出很多（具体自行搜索：undertow vs tomcat），所以jboot构建和部署等不再需要tomcat。在Jboot构建的时候，在linux平台下，会生成jboot.sh 在windows平台下会生成jboot.bat脚本，直接执行该脚本即可。

生成jboot.sh或者jboot.bat，依赖maven的appassembler插件，因此，你的maven配置文件pom.xml需要添加如下配置：

config pom.xml

```xml

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
                <!--必须添加compilerArgument配置，才能使用JFinal的Controller方法带参数的功能-->
                <compilerArgument>-parameters</compilerArgument>
            </configuration>
        </plugin>


        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>appassembler-maven-plugin</artifactId>
            <version>1.10</version>
            <configuration>
            
               <assembleDirectory>${project.build.directory}/app</assembleDirectory>
                <repositoryName>lib</repositoryName>
                <binFolder>bin</binFolder>
                <configurationDirectory>webRoot</configurationDirectory>
                <copyConfigurationDirectory>true</copyConfigurationDirectory>
                <configurationSourceDirectory>src/main/resources</configurationSourceDirectory>
                <repositoryLayout>flat</repositoryLayout>
                <encoding>UTF-8</encoding>
                <logsDirectory>logs</logsDirectory>
                <tempDirectory>tmp</tempDirectory>

                <programs>
                    <!--程序打包 mvn package appassembler:assemble -->
                    <program>
                        <mainClass>io.jboot.Jboot</mainClass>
                        <id>jboot</id>
                        <platforms>
                            <platform>windows</platform>
                            <platform>unix</platform>
                        </platforms>
                    </program>
                </programs>

                <daemons>
                    <!-- 后台程序打包：mvn clean package appassembler:generate-daemons -->
                    <daemon>
                        <mainClass>io.jboot.Jboot</mainClass>
                        <id>jboot</id>
                        <platforms>
                            <platform>jsw</platform>
                        </platforms>
                        <generatorConfigurations>
                            <generatorConfiguration>
                                <generator>jsw</generator>
                                <includes>
                                    <include>linux-x86-32</include>
                                    <include>linux-x86-64</include>
                                    <include>macosx-universal-32</include>
                                    <include>macosx-universal-64</include>
                                    <include>windows-x86-32</include>
                                    <include>windows-x86-64</include>
                                </includes>
                                <configuration>
                                    <property>
                                        <name>configuration.directory.in.classpath.first</name>
                                        <value>webRoot</value>
                                    </property>
                                    <property>
                                        <name>wrapper.ping.timeout</name>
                                        <value>120</value>
                                    </property>
                                    <property>
                                        <name>set.default.REPO_DIR</name>
                                        <value>lib</value>
                                    </property>
                                    <property>
                                        <name>wrapper.logfile</name>
                                        <value>logs/wrapper.log</value>
                                    </property>
                                </configuration>
                            </generatorConfiguration>
                        </generatorConfigurations>
                    </daemon>
                </daemons>
            </configuration>
        </plugin>
    </plugins>
</build>
```

#### 进行maven构建

```java
mvn package appassembler:assemble
```
构建完毕后，会在target目录下生成一个app文件夹，在app文件的bin目录下会有一个jboot脚本（或者jboot.bat）。

#### 启动应用
```java
cd yourProjectPath/target/app/bin
./jboot
```

##### 在启动的时候添加上自己的配置信息

```java
cd yourProjectPath/target/app/bin
./jboot --jboot.server.port=8080 --jboot.rpc.type=local
```
##### 使用你自己的配置文件来代替 jboot.properties

```java
cd yourProjectPath/target/app/bin
./jboot --jboot.model=dev --jboot.server.port=8080
```
上面的命令启动后，会使用 `jboot-dev.proerties` 文件来替代 `jboot.properties` 同时设置 jboot.server.port=8080（服务器端口号为8080）


#### 后台程序

在以上文档中，如果通过如下代码进行构建的。

```java
mvn package appassembler:assemble
```
构建会生成 app目录，及对应的jboot脚本，但是jboot在执行的时候是前台执行的，也就是必须打开一个窗口，当关闭这个窗口后，jboot内置的服务器undertow也会随之关闭了，在正式的环境里，我们是希望它能够以服务的方式在后台运行。

那么，如果构建一个后台运行的程序呢？步骤如下：

##### 第一步：执行如下maven编译

```java
mvn clean package appassembler:generate-daemons
```
maven命令执行完毕后，会在target下生成如下文件夹 `/generated-resources/appassembler/jsw/jboot` , 文件中我们会找到bin目录，生成的后台脚本jboot（或jboot.bat）会存放在bin目录里。

##### 第二步：启动应用
```java
cd yourProjectPath/target/generated-resources/appassembler/jsw/jboot/bin
./jboot
```
此时，启动的应用为后台程序了。


## Jboot部署到tomcat
首先，需要配置的自己的pom文件的packaging为war，并配置上maven编译插件：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-war-plugin</artifactId>
    <configuration>
        <attachClasses>true</attachClasses>
        <packagingExcludes>WEB-INF/web.xml</packagingExcludes>
    </configuration>
</plugin>

```
这个过程和普通的java web工程没什么区别。

最最重要的是配置web.xml，在WEB-INF下创建 web.xml，起内容如下：

```xml
<filter>
    <filter-name>jfinal</filter-name>
    <filter-class>com.jfinal.core.JFinalFilter</filter-class>
    <init-param>
        <param-name>configClass</param-name>
        <param-value>io.jboot.web.JbootAppConfig</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>jfinal</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
这里注意：param-value一定使用io.jboot.web.JbootAppConfig，或者是其子类。

 

如果用到shiro，再配置上：

```xml
<filter>
    <filter-name>shiro</filter-name>
    <filter-class>org.apache.shiro.web.servlet.ShiroFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>shiro</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
<listener>
    <listener-class>org.apache.shiro.web.env.EnvironmentLoaderListener</listener-class>
</listener>
```

一般情况下，shiro的配置内容要放到jfinal的配置之上。

 

如果项目还用到hystrix，需要添加如下配置：

```xml
<servlet>
    <servlet-name>hystrix</servlet-name>
    <servlet-class>com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>hystrix</servlet-name>
    <url-pattern>/hystrix</url-pattern>
</servlet-mapping>
 ```

如果还用到Metrics，添加如下配置：

```xml
<servlet>
    <servlet-name>metrics</servlet-name>
    <servlet-class>com.codahale.metrics.servlets.AdminServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>metrics</servlet-name>
    <url-pattern>/metrics</url-pattern>
</servlet-mapping>
<listener>
    <listener-class>io.jboot.component.metric.JbootMetricServletContextListener</listener-class>
</listener>
<listener>
    <listener-class>io.jboot.component.metric.JbootHealthCheckServletContextListener</listener-class>
</listener>
 ```
 
 注意：因为项目的html文件都放在resource下，如果war解压后只有 META-INF 和 WEB-INF 这两个文件夹，html文件被打包在 WEB-INF/clasess 下，是不会被正确渲染的。所以需要通过pom文件配置插件，把resource下的html文件copy到war的根目录下，才能被正常渲染。
 
 假设你的html文件都放在 resource下的htmls目录，配置内容如下：
 
 ```xml
  <plugin>
    <artifactId>maven-antrun-plugin</artifactId>
    <executions>
        <execution>
            <phase>compile</phase>
            <goals>
                <goal>run</goal>
            </goals>
            <configuration>
                <tasks>
                    <move file="${project.build.directory}/classes/htmls"  tofile="${project.build.directory}/${project.build.finalName}/htmls"/>
                </tasks>
            </configuration>
        </execution>
    </executions>
</plugin>
 ```


# 鸣谢
rpc framework: 

* motan(https://github.com/weibocom/motan)
* grpc(http://grpc.io)
* thrift(https://github.com/apache/thrift)

mq framework:

* activemq
* rabbitmq
* redis mq
* hornetq
* aliyun mq

cache framework

* ehcache
* redis

core framework:

* jfinal (https://github.com/jfinal/jfinal)
* undertow (https://github.com/undertow-io/undertow)
* guice (https://github.com/google/guice)
* metrics (https://github.com/dropwizard/metrics)
* hystrix (https://github.com/Netflix/Hystrix)
* shiro （https://github.com/apache/shiro）

# 联系作者
* qq:1506615067
* wechat：wx198819880
* email:fuhai999#gmail.com

# 常见问题

- 使用Jboot后还能自定义Jfinal的配置文件吗？
	- 答：可以使用，目前提供两种方案。
		- 方案1（推荐）：编写一个类，随便起个名字，继承 JbootAppListenerBase ,然后复写里面的方法。
		- 方案2（不推荐）：编写自己的JfinalConfig，继承 JbootAppConfig ，然后在 jboot.properties 的 jboot.jfinalConfig 配置上自己的类名。注意，在自己的config中，请优先调用super方法。例如在configConstant中，请先调用super.configConstant(constants)。

		
		
		
		
	

