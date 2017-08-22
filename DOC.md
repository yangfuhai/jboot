
在阅读Jboot文档之前，我假定您已经了解了JFinal，已经有JFinal的基础知识，如果您还没有了解JFinal，请先去JFinal官网 www.jfinal.com 了解学习JFinal，Jboot是基于JFinal进行二次开发，依赖JFinal的基础知识。

或者您也可以去购买我的课程进行学习，课程地址：http://www.yangfuhai.com/post/6.html

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

# 安全控制 
## shiro简介

略

##shiro的配置
在使用Jboot的shiro模块之前，我假定您已经学习并了解shiro的基础知识。在Jboot中使用shiro非常简单，只需要在resources目录下配置上您的shiro.ini文件即可。在shiro.ini文件里，需要在自行扩展realm等信息。


## shiro的使用
Jboot的shiro模块为您提供了以下12个模板指令，方便您使用shiro。

| 指令         |  描述  |
| ------------- | -----|
| ShiroAuthenticated |用户已经身份验证通过，Subject.login登录成功 |
| ShiroGuest  |游客访问时。 但是，当用户登录成功了就不显示了|
| ShiroHasAllPermission  |拥有全部权限 |
| ShiroHasAllRoles  |拥有全部角色 |
| ShiroHasAnyPermission  |拥有任何一个权限 |
| ShiroHasAnyRoles  |拥有任何一个角色 |
| ShiroHasPermission  |有相应权限 |
| ShiroHasRole  |有相应角色 |
| ShiroNoAuthenticated  |未进行身份验证时，即没有调用Subject.login进行登录。 |
| ShiroNotHasPermission  |没有该权限 |
| ShiroNotHasRole  |没没有该角色 |
| ShiroPrincipal  |获取Subject Principal 身份信息 |


### ShiroAuthenticated的使用

```html
#shiroAuthenticated()
  登陆成功：您的用户名是：#(SESSION("username"))
#end

```



### ShiroGuest的使用

```html
#ShiroGuest()
  游客您好
#end

```

### ShiroHasAllPermission的使用

```html
#ShiroHasAllPermission(permissionName1,permissionName2)
  您好，您拥有了权限 permissionName1和permissionName2
#end

```

### ShiroHasAllRoles的使用

```html
#ShiroHasAllRoles(role1, role2)
  您好，您拥有了角色 role1和role2
#end

```
### ShiroHasAnyPermission的使用

```html
#ShiroHasAnyPermission(permissionName1,permissionName2)
  您好，您拥有了权限 permissionName1 或 permissionName2 
#end

```
### ShiroHasAnyRoles的使用

```html
#ShiroHasAllRoles(role1, role2)
  您好，您拥有了角色 role1 或 role2
#end

```
### ShiroHasPermission的使用

```html
#ShiroHasPermission(permissionName1)
  您好，您拥有了权限 permissionName1 
#end

```
### ShiroHasRole的使用

```html
#ShiroHasRole(role1)
  您好，您拥有了角色 role1 
#end

```
### ShiroNoAuthenticated的使用

```html
#ShiroNoAuthenticated()
  您好，您还没有登陆
#end

```
### ShiroNotHasPermission的使用

```html
#ShiroNotHasPermission(permissionName1)
  您好，您没有权限 permissionName1 
#end

```
### ShiroNotHasRole的使用
```html
#ShiroNotHasRole(role1)
  您好，您没有角色role1
#end

```
### ShiroPrincipal的使用
```html
#ShiroPrincipal()
  您好，您的登陆信息是：#(principal)
#end

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
待续


# AOP

## Google Guice
Jboot 的AOP功能，是使用了Google的Guice框架来完成的，通过AOP，我们可以轻易的在微服务体系中监控api的调用，轻易的使用@Cacheable，@CachePut，@CacheEvict等注解完成对代码的配置。
## @Inject
## @Bean

# RPC远程调用
## Motan
## @RpcService

# MQ消息队列
## RedisMQ
## ActiveMQ

# Cache缓存
## ehcache
## redis
## ehredis

# http客户端

# 监控

# 容错与隔离

# 其他

## SPI扩展

## JbootEvnet事件机制

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

## shiro安全控制

## 代码生成器



# maven dependency

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>1.0-beta3</version>
</dependency>

```
# controller example



new a controller

```java
@RequestMapping("/")
public class MyController extend JbootController{
   public void index(){
        renderText("hello jboot");
   }
}
```

start 

```java
public class MyStarter{
   public static void main(String [] args){
       Jboot.run(args);
   }
}
```

visit: http://127.0.0.1:8080


# mq example
config jboot.properties

```java
#type default redis (support: redis,activemq,rabbitmq,hornetq,aliyunmq )
jboot.mq.type = redis
jboot.mq.redis.host = 127.0.0.1
jboot.mq.redis.password =
jboot.mq.redis.database =
```

server a sendMqMessage

```java
 Jboot.me().getMq().publish(yourObject, toChannel);
```

server b message listener

```java
Jboot.me().getMq().addMessageListener(new JbootmqMessageListener(){
        @Override
        public void onMessage(String channel, Object obj) {
           System.out.println(obj);
        }
}, channel);
```

# rpc example
config jboot.properties

```java
#type default motan (support:local,motan,grpc,thrift)
jboot.rpc.type = motan
jboot.rpc.requestTimeOut
jboot.rpc.defaultPort
jboot.rpc.defaultGroup
jboot.rpc.defaultVersion
jboot.rpc.registryType = consul
jboot.rpc.registryName
jboot.rpc.registryAddress = 127.0.0.1:8500
```

define interface

```java
public interface HelloService {
    public String hello(String name);
}
```

server a export serviceImpl

```java
@JbootrpcService
public class myHelloServiceImpl  implements HelloService {
    public String hello(String name){
         System.out.println("hello" + name);
         return "hello ok";
    }
}
```

download consul and start (consul:https://www.consul.io/)

```java
consul -agent dev
```

server b call

```java
 HelloService service = Jboot.me().service(HelloService.class);
 service.hello("michael");
```

or server b controller

```java
public class MyController extends bootController{
    
    @JbootrpcService
    HelloService service ;
    
    public void index(){
        
        renderText("hello " + service.hello());
    }
    
}
```

# cache example
config jboot.properties

```java
#type default ehcache (support:ehcache,redis,ehredis)
jboot.cache.type = redis
jboot.cache.redis.host =
jboot.cache.redis.password =
jboot.cache.redis.database =
```
备注：ehredis 是一个基于ehcache和redis实现的二级缓存框架。

use cache

```java
Jboot.me().getCache().put("cacheName", "key", "value");
```

# database access example
config jboot.properties

```java
#type default mysql (support:mysql,oracle,db2...)
jboot.datasource.type=
jboot.datasource.url=
jboot.datasource.user=
jboot.datasource.password=
jboot.datasource.driverClassName=
jboot.datasource.connectionInitSql=
jboot.datasource.cachePrepStmts=
jboot.datasource.prepStmtCacheSize=
jboot.datasource.prepStmtCacheSqlLimit=
```

define model

```java
@Table(tableName = "user", primaryKey = "id")
public class User extends JbootModel<User> {
	
}
```

dao query

```java
public class UserDao extends JbootDaoBase {
    public static find User DAO = new User();
    
    public User findById(String id){
        return DAO.findById(id);
    }
    
    public List<User> findByNameAndAge(String name,int age){
        
       Columns columns = Columns.create()
                        .like("name","%"+name+"%")
                        .gt("age",age);
        
        return DAO.findListByColums(columns);
    }
}
```

# event example

send event

```java
Jboot.me().sendEvent(actionStr,  dataObj)
```

event listener

```java
@EventConfig(action = {User.ACTION_ADD,User.ACTION_DELETE})
public class MyEventListener implements JbootEventListener {
    
    public  void onMessage(JbootEvent event){
        
        if(event.getAction.equals(User.ACTION_ADD)){
            System.out.println("new user add, user:"+event.getData);
        }else if(event.getAction.equals(User.ACTION_DELETE)){
            System.out.println("user deleted, user:"+event.getData);
        }
        
    }
    
}
```

# read config
config jboot.properties

```java
jboot.myconfig.user = aaa
jboot.myconfig.password = bbb
```
define config model

```java
@PropertieConfig(prefix = "jboot.myconfig")
public class MyConfig {

    private String name;
    private String password;
    
    // getter and setter
}
```

get config model

```java
    MyConfig config = Jboot.me().config(MyConfig.class);
    System.out.println(config.getName());
```

# code generator
```java
  public static void main(String[] args) {
  
        String modelPackage = "io.jboot.test";
        JbootModelGenerator.run(modelPackage);

    }
```

# build

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
                        <program>
                            <mainClass>io.jboot.Jboot</mainClass>
                            <id>jboot</id>
                            <platforms>
                                <platform>windows</platform>
                                <platform>unix</platform>
                            </platforms>
                        </program>
                    </programs>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

maven build

```java
mvn package appassembler:assemble
```

# start app
```java
cd yourProjectPath/target/app/bin
./jboot
```

start app and change config

```java
cd yourProjectPath/target/app/bin
./jboot --jboot.server.port=8080 --jboot.rpc.type=local
```
use your properties replace jboot.properties

```java
cd yourProjectPath/target/app/bin
./jboot --jboot.model=dev --jboot.server.port=8080
```
use jboot-dev.proerties replace jboot.properties and set jboot.server.port=8080


# thanks
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

