
# 目录
- [JBoot核心组件](#jboot核心组件)
- [MVC](#mvc)
	- MVC的概念
	- JbootController
	- @RquestMapping
		- 使用@RquestMapping
		- render
	- session 与 分布式session
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
	- 添加metrics数据
	- metrics与Ganglia
	- metrics与grafana
	- metrics与jmx
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



# JBoot核心组件
Jboot的主要核心组件有以下几个。

* [x] MVC （基于jfinal）
* [x] ORM （基于jfinal）
* [x] AOP （基于guice）
* 安全控制
    * [x] shiro
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
## JbootController
MVC中的C是Controller的简写，在Jboot应用中，所有的控制器Controller都应该继承至JbootController，JbootController扩展了Jfinal中Controller的许多方法，比如多出了如下这些非常常用的方法：

* isMoblieBrowser() //是否是手机浏览器
* isWechatBrowser() //是否是微信浏览器
* isIEBrowser() //是否是IE浏览器，低级的IE浏览器在ajax请求的时候，返回json要做特殊处理
* isAjaxRequest() //是否是ajax请求
* isMultipartRequest() //是否是带有文件上传功能的请求
* getReferer() // 获取来源网址
* getIPAddress() //获取用户的IP地址
* getUserAgent() //获取http头的useragent
* getBaseUrl() //获取当前域名
* getUploadFilesMap() // 获取当前上传的所有文件


同时，JbootController还做了统一的session处理，在分布式应用中，可以配置session的分布式缓存。

## @RquestMapping
RquestMapping是请求映射，也就是通过@RquestMapping注解，可以让某个请求映射到指定的控制器Controller里去。


### 使用@RquestMapping
使用@RquestMapping非常简单。只需要在Controller类添加上@RquestMapping注解即可。

例如：

```java
@RequestMapping("/")
public class HelloController extend JbootController{
   public void index(){
        renderText("hello jboot");
   }
}
```
我们在HelloController控制器上，添加了@RequestMapping("/")配置，也就是让当访问 `http://127.0.0.1/`的时候让HelloController控制的index()这个方法（action）来处理。

**[注意]：** 

* 访问`http://127.0.0.1`等同于`http://127.0.0.1/`。
* `@RquestMapping` 可以使用在任何的 Controller，并 **不需要** 这个Controller继承至JbootController。

## render
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

## session 与 分布式session

使用session非常简单，直接在Controller里调用`getSessionAttr(key)` 或 `setSessionAttr(key,value)` 就可以。

### 分布式session
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

# 安全控制 
## shiro简介

略

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
| shiroNotHasRole  |没没有该角色 |
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


# ORM
## 配置
在使用数据库之前，需要给Jboot应用做一些配置，实际上，在任何的需要到数据库的应用中，都需要给应用程序做一些配置，让应用程序知道去哪里读取数据。

由于Jboot的数据库读取是依赖于JFinal，所以实际上JFinal只是的数据库类型，Jboot都会支持，比如常用的数据库类型有：

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

### 分库
分库建议使用多数据源的方式进行分库

### 分表
在Jboot中，分表是通过sharding-jdbc（ 网址：https://github.com/shardingjdbc/sharding-jdbc） 来实现的，所以，在了解Jboot的分表之前，请先阅读了解sharding-jdbc的配置信息。

阅读Jboot的分表之前，假定你对Sharding-jdbc已经有所了解。

#### 第一步：编写分表策略

例如：

```java
public final class ModuloTableShardingAlgorithm implements SingleKeyTableShardingAlgorithm<Integer> {

    @Override
    public String doEqualSharding(final Collection<String> tableNames, 
    	final ShardingValue<Integer> shardingValue) {
        
    }

    @Override
    public Collection<String> doInSharding(final Collection<String> tableNames, 
    	final ShardingValue<Integer> shardingValue) {
       
    }

    @Override
    public Collection<String> doBetweenSharding(final Collection<String> tableNames, 
    	final ShardingValue<Integer> shardingValue) {
        
    }
}
```

具体实现参考：

https://github.com/shardingjdbc/sharding-jdbc/blob/master/sharding-jdbc-example/sharding-jdbc-example-jdbc/src/main/java/com/dangdang/ddframe/rdb/sharding/example/jdbc/algorithm/ModuloTableShardingAlgorithm.java 

#### 第二步：编写 IShardingRuleFactory 的实现类

```java
public class MyShardingRuleFactory implements IShardingRuleFactory{
	public ShardingRule createShardingRule(DataSource dataSource){
	     // 创建分片规则
	}
}
```

具体可以参 

https://github.com/shardingjdbc/sharding-jdbc/blob/master/sharding-jdbc-example/sharding-jdbc-example-jdbc/src/main/java/com/dangdang/ddframe/rdb/sharding/example/jdbc/Main.java


#### 第三步：给数据源配置上ShardingRuleFactory

```
jboot.datasource.type=
jboot.datasource.url=
jboot.datasource.user=
jboot.datasource.password=
jboot.datasource.shardingRuleFactory=com.yours.MyShardingRuleFactory
```


# AOP

## Google Guice
Jboot 的AOP功能，是使用了Google的Guice框架来完成的，通过AOP，我们可以轻易的在微服务体系中监控api的调用，轻易的使用@Cacheable，@CachePut，@CacheEvict等注解完成对代码的配置。
## @Inject
## @Bean

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
consul -agent dev
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



# metrics数据监控
Jboot的监控机制是通过Metrics来来做监控的，要启用metrics非常简单，通过在jboot.properties文件配置上`jboot.metrics.url`就可以启用metrics。

例如

```xml
jboot.metrics.url = /metrics.html
```
我们就可以通过访问 `http://host:port/metrics.html` 来访问到metrics数据情况。

### 添加metrics数据
默认通过Url访问到的数据是没有具体内容，因为metrics无法得知要显示什么样的数据内容。例如，我们要统计某个action的用户访问量，可以通过在action里编写如下代码。

```java
public void myaction() {

    Jboot.me().getMetric().counter("myaction").inc();

    renderText("my action");
}
```

当我们访问myaction这个地址后，然后再通过浏览器`http://host:port/metrics.html`访问，我们就能查看到如下的json数据。

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

### metrics与Ganglia


### metrics与Grafana

### metrics与jmx
metrics与jmx集成非常简单，只需要在jboot.properties文件添加如下配置：

```xml
jboot.metrics.jmxReporter = true
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
@SwaggerAPIs(name = "测试接口", description = "这个接口集合的描述")
@RequestMapping("/swaggerTest")
public class MySwaggerTestController extends JbootController {


    @SwaggerAPI(description = "测试description描述", summary = "测试summary", operationId = "testOnly",
            params = {@SwaggerParam(name = "name", description = "请输入账号名称")}
    )
    public void index() {
        renderJson(Ret.ok("k1", "v1").set("name", getPara("name")));
    }


    @SwaggerAPI(description = "进行用户登录操作", summary = "用户登录API", method = "post",
            params = {
                    @SwaggerParam(name = "name", description = "请输入账号名称"),
                    @SwaggerParam(name = "pwd", description = "请输入密码", definition = "MySwaggerPeople")
            }
    )
    public void login() {
        renderJson(Ret.ok("k2", "vv").set("name", getPara("name")));
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

## 5个swagger注解


| 指令         |  描述  |
| ------------- | -----|
| SwaggerAPIs  | 在Controller上进行配置，指定Controller api的描述|
| SwaggerAPI | 在Controller上某个action进行注解 |
| SwaggerDefinition  |  |
| SwaggerDefinitionEnum  |  |
| SwaggerParam  |  |
| SwaggerResponse  |  | 

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
@EventConfig(action = {“event1”,"event2"})
public class MyEventListener implements JbootEventListener {
    
    public  void onMessage(JbootEvent event){
        Object data = event.getData();
        System.out.println("get event:"data);
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
要读取这个配置信息，我们需要定义我们的一个model类，并通过@PropertieConfig注解给我们的类配置上类与配置文件的对应关系，如下所示：

```java
@PropertieConfig(prefix="jboot.myconfig")
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

那么，一样的，我们需要编写一个model，并配置上@PropertieConfig注解，与读取jboot.properties文件不同的是，@PropertieConfig 需要添加上file配置，内容如下：

```java
@PropertieConfig(prefix="jboot.myconfig",file="michael.properties")
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
                <repositoryName>libs</repositoryName>
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

		
		
		
		
		
		
		
		
		
		
		
		
		
		

