# jboot
jboot is a similar springboot project base on jfinal and undertow,we have using in product environment.

# jboot 中文描述
jboot是一个基于jfinal、undertow开发的一个类似springboot的开源框架，
我们已经在正式的商业上线项目中使用。她集成了代码生成，微服务，MQ，RPC，监控等功能，
开发者使用及其简单。

# maven dependency

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>1.0-alphpa1</version>
</dependency>

```
# controller example



new a controller

```java
@UrlMapping(url="/")
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

visit: http://127.0.0.1:8088


# mq example
config jboot.properties
```java
#type default redis (support: redis,activemq,rabbitmq,hornetq,aliyunmq )
jboot.mq.type = redis
jboot.mq.redis.address = 127.0.0.1
jboot.mq.redis.password =
jboot.mq.redis.database =
```

server a sendMqMessage
```java
 Jboot.getMq().publish(yourObject, toChannel);
```

server b message listener
```java
Jboot.getMq().addMessageListener(new JbootmqMessageListener(){
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
@JbootrpcService(export = HelloService.class)
public class myHelloServiceImpl extends JbootService implements HelloService {
    public String hello(String name){
         System.out.println("hello" + name);
         return "hello ok";
    }
}
```

server b call
```java
 HelloService service = Jboot.service(HelloService.class);
 service.hello("michael");
```

# cache example
config jboot.properties
```java
#type default ehcache (support:ehcache,redis,ehredis (ehredis:tow level cache,ehcache level one and redis level tow))
jboot.cache.type = redis
jboot.cache.redis.address =
jboot.cache.redis.password =
jboot.cache.redis.database =
```

use cache
```java
Jboot.getCache().put("cacheName", "key", "value");
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
public class UserDao {
    public static find User DAO = new User();
    
    public User findById(String id){
        return DAO.findById(id);
    }
    
    public List<User> findByNameAndAge(String name,int age){
        return DAO.findListByColums(Columns.create().like("name","%"+name+"%").gt("age",age));
    }
}
```

# event example

send event
```java
Jboot.sendEvent(actionStr,  dataObj)
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
    MyConfig config = Jboot.config(MyConfig.class);
    System.out.println(config.getName());
```

# code generator
```java
  public static void main(String[] args) {

        String modelPackage = "io.jboot.test";

        String dbHost = "127.0.0.1";
        String dbName = "yourDbName";
        String dbUser = "root";
        String dbPassword = "";

        JbootModelGenerator.run(modelPackage, dbHost, dbName, dbUser, dbPassword);

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
                            <jvmSettings>
                                <extraArguments>
                                    <extraArgument>-server</extraArgument>
                                    <extraArgument>-Xmx2G</extraArgument>
                                    <extraArgument>-Xms2G</extraArgument>
                                </extraArguments>
                            </jvmSettings>
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
* jfinal
* undertow

# author
* name:yangfuhai
* qq:1506615067
* email:fuhai999@gmail.com

# donate
![](https://camo.githubusercontent.com/6e46a604bca1a81c26d4f7972e3ab45ca50b9405/687474703a2f2f3778763978702e636f6d312e7a302e676c622e636c6f7564646e2e636f6d2f7a7a2e6a7067)