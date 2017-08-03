

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


#MVC
## JbootController

## @RquestMapping

#安全控制 
## shiro

#ORM
## Model
## Record
## DAO
## 多数据源
## 分库和分表


#AOP
## Google Guice
## @Inject
## @Bean

#RPC远程调用
## Motan
## @RpcService

#MQ消息队列
## RedisMQ
## ActiveMQ

#Cache缓存
## ehcache
## redis
## ehredis

#http客户端

#监控

#容错与隔离

#其他

##SPI扩展

##JbootEvnet事件机制

##自定义序列化

##配置文件

##分布式session

##shiro安全控制



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

