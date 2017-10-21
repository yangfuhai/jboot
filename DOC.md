
# 目录
- JBoot核心组件
- MVC
	- MVC的概念
	- JbootController
	- @RquestMapping
		- 使用@RquestMapping
		- render
	- session 与 分布式session
- 安全控制
	- shiro简介
	- shiro的配置
	- shiro的使用
		- 12个模板指令（用在html上）
		- 5个Requires注解功能（用在Controller上）
- ORM
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
- AOP
	- Google Guice
	- @Inject
	- @Bean
- RPC远程调用
	- 使用步骤
	- 其他注意
- MQ消息队列
	- 使用步骤
	- RedisMQ
	- ActiveMQ
	- RabbitMq
	- 阿里云商业MQ
- Cache缓存
	- 使用步骤
	- 注意事项
	- ehcache
	- redis
	- ehredis
- http客户端
- 监控
- 容错与隔离
- 其他
	- SPI扩展
	- JbootEvnet事件机制
	- 自定义序列化
	- 配置文件
	- 代码生成器
- 项目构建
- 鸣谢
- [联系作者](#联系作者)
- [常见问题](#常见问题)
	- 使用Jboot后还能自定义JfinalConfig等配置文件吗？





# JBoot核心组件
Jboot的主要核心组件有以下几个。

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
同JFinal render。

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
model是MVC设计模式中的M，但同时每个model也会对应一个数据库表，更多参考JFinal文档。

### @Table注解
@Table注解是给Model使用的，表示让Model映射到哪个数据库表，使用代码如下：

```java
@Table(tableName = "company", primaryKey = "cid")
public class Company extends BaseCompany<Company> {
	
}
```
值得注意的是：

在Jboot应用中，我们几乎感受不到@Table这个注解的存在，因为这部分完全是代码生成器生成的，关于代码生成器，请查看 代码生成器章节。

## Record
参考JFinal

## DAO
参考JFinal

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
    public String doEqualSharding(final Collection<String> tableNames, final ShardingValue<Integer> shardingValue) {
        
    }

    @Override
    public Collection<String> doInSharding(final Collection<String> tableNames, final ShardingValue<Integer> shardingValue) {
       
    }

    @Override
    public Collection<String> doBetweenSharding(final Collection<String> tableNames, final ShardingValue<Integer> shardingValue) {
        
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

#### 其他注意
因为在配置文件中，配置的服务发现类型为consul，所以需要提前安装好consul。

##### 下载consul
https://www.consul.io 

##### 启动consul

```java
consul -agent dev
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

# 监控

# 容错与隔离

# 其他

## SPI扩展

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

		
		
		
		
		
		
		
		
		
		
		
		
		
		

