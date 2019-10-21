# 数据库

Jboot 数据库功能基于 JFinal 的 ActiveRecordPlugin 插件和 Apache sharding-sphere 框架，提供了方便快捷的数据库操作能力。

## 目录

- 描述
- 基本增删改查
- 关联查询
- 分页查询
- 批量插入
- 事务操作
- 读写分离
- 分库分表
- 分布式事务

## 描述

Jboot 的数据库是依赖 JFinal 的 ORM 做基本的数据库操作，同时依赖 apahce-sphere 来做分库、分表和依赖 Seata 来做分布式事务。因此，在使用 Jboot 操作数据库的时候，建议对 JFinal 的 ORM 功能和 Apache Sharding-Sphere 有所了解。

- JFinal的数据库操作相关文档：https://www.jfinal.com/doc/5-1
- Apache Sharding-Sphere 文档：http://shardingsphere.io/document/current/cn/overview/
- Seata 的帮助文档：https://github.com/seata/seata/wiki/Home_Chinese


## 基本增删改查

JFinal 操作数据库，提供了两种方式对数据库进行操作，他们分别是：
- Db + Record 方式
- Model 映射方式

### Db + Record 方式

Db 可以理解为一个工具类，而 Record 是一个类似 Map 的数据结构（其实内部就是通过 Map 来实现的），Db 查询的返回的数据是一个 `Record` 或者是 `List<Record>` , Db 提供了如下操作数据库的系列方法：


| 方法         |  描述  |
| ------------- | -----|
|query(String, Object...)|...|
|query(String)|...|
|queryFirst(String, Object...)|...|
|queryFirst(String)|...|
|queryColumn(String, Object...)|...|
|queryColumn(String)|...|
|queryStr(String, Object...)|...|
|queryStr(String)|...|
|queryInt(String, Object...)|...|
|queryInt(String)|...|
|queryLong(String, Object...)|...|
|queryLong(String)|...|
|queryDouble(String, Object...)
|queryDouble(String)|...|
|queryFloat(String, Object...)|...|
|queryFloat(String)|...|
|queryBigDecimal(String, Object...)|...|
|queryBigDecimal(String)|...|
|queryBytes(String, Object...)|...|
|queryBytes(String)|...|
|queryDate(String, Object...)|...|
|queryDate(String)|...|
|queryTime(String, Object...)|...|
|queryTime(String)|...|
|queryTimestamp(String, Object...)|...|
|queryTimestamp(String)|...|
|queryBoolean(String, Object...)|...|
|queryBoolean(String)|...|
|queryShort(String, Object...)|...|
|queryShort(String)|...|
|queryByte(String, Object...)|...|
|queryByte(String)|...|
|queryNumber(String, Object...)|...|
|queryNumber(String)|...|
|update(String, Object...)|...|
|update(String)|...|
|find(String, Object...)|...|
|find(String)|...|
|findFirst(String, Object...)|...|
|findFirst(String)|...|
|findById(String, Object)|...|
|findById(String, String, Object...)|...|
|deleteById(String, Object)|...|
|deleteById(String, String, Object...)|...|
|delete(String, String, Record)|...|
|delete(String, Record)|...|
|delete(String, Object...)|...|
|delete(String)|...|
|paginate(int, int, String, String, Object...)|...|
|paginate(int, int, boolean, String, String, Object...)|...|
|paginate(int, int, String, String)|...|
|paginateByFullSql(int, int, String, String, Object...)|...|
|paginateByFullSql(int, int, boolean, String, String, Object...)|...|
|save(Config, java.sql.Connection, String, String, Record)|...|
|save(String, String, Record)|...|
|save(String, Record)|...|
|update(Config, java.sql.Connection, String, String, Record)|...|
|update(String, String, Record)|...|
|update(String, Record)|...|
|execute(ICallback)|...|
|execute(Config, ICallback)|...|
|tx(Config, int, IAtom)|...|
|tx(int, IAtom)|...|
|tx(IAtom)|...|
|findByCache(String, Object, String, Object...)|...|
|findByCache(String, Object, String)|...|
|findFirstByCache(String, Object, String, Object...)|...|
|findFirstByCache(String, Object, String)|...|
|paginateByCache(String, Object, int, int, String, String, Object...)|...|
|paginateByCache(String, Object, int, int, boolean, String, String, Object...)|...|
|paginateByCache(String, Object, int, int, String, String)|...|
|batch(String, Object[][], int)|...|
|batch(String, String, List, int)|...|
|batch(List<String>, int)|...|
|batchSave(List<? extends Model>, int)|...|
|batchSave(String, List<Record>, int)|...|
|batchUpdate(List<? extends Model>, int)|...|
|batchUpdate(String, String, List<Record>, int)|...|
|batchUpdate(String, List<Record>, int)|...|
|getSql|...|
|getSqlPara(String, Record)|...|
|getSqlPara(String, Model)|...|
|getSqlPara(String, Map)|...|
|getSqlPara(String, Object...)|...|
|find(SqlPara)|...|
|findFirst(SqlPara)|...|
|update(SqlPara)|...|
|paginate(int, int, SqlPara)|...|

以下是 Db + Record 模式的一些示例：

```java
// 创建name属性为James,age属性为25的record对象并添加到数据库
Record user = new Record()
.set("name", "James")
.set("age", 25);

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
List<Record> users = Db.find("select * from user where age > 18");
 
// 分页查询年龄大于18的user,当前页号为1,每页10个user
Page<Record> userPage = Db.paginate(1, 10, "select *", "from user where age > ?", 18);

```

在单数据库下，以下是 Db 工具进行的事务操作：

```java
boolean succeed = Db.tx(() -> {
    int count = Db.update("update account set cash = cash - ? where id = ?", 100, 123);
    int count2 = Db.update("update account set cash = cash + ? where id = ?", 100, 456);
    return count == 1 && count2 == 1;
});
```

  以上两次数据库更新操作在一个事务中执行，如果执行过程中发生异常或者return false，则自动回滚事务。


### Model 映射方式

Model是 MVC 模式中的 M 部分。以下是 Model 定义示例代码：

```java
@Table(tableName = "user", primaryKey = "id")
public class User extends BaseUser<User> {

}   
```

BaseUser：

```java
public abstract class BaseUser<M extends BaseUser<M>> extends JbootModel<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	// other getter setter ...

}
```

需要注意的是：
- 以上的 `User` 和 `BaseUser` 都是通过代码生成器自动生成的，无需手写。
- 多次执行代码生成器，`User` 代码不会被覆盖，但是 `BaseUser` 会被重新覆盖，因此，请不要在 `BaseUser` 手写任何代码。

一般情况下，在正式的项目里，代码分层还需要 `Service` 层来对业务逻辑进行处理。

UserService 代码如下:

```java
public class UserService extends JbootServiceBase<User>  {

    // 不需要做任何的实现
    
}  
```

以上的 `UserService` , 只需要继承 `JbootServiceBase<User>` ,我们不需要编写任何实现代码，就可以拥有基本的增删改查的功能。

以下是示例代码：

```java
//创建 UserService
UserService userService = new UserService();

// 创建name属性为James,age属性为25的User对象并添加到数据库
User user = new User().set("name", "James").set("age", 25);
userService.save(user); 

// 删除id值为25的User
userService.deleteById(25);
 
// 查询id值为25的User将其name属性改为James并更新到数据库
User user = userService.findById(25);
user.set("name", "James");
userService.update(user);
 
// 分页查询user,当前页号为1,每页10个user
Page<User> userPage = userService.paginate(1, 10);
```

## 读写分离

在 Jboot 应用中，读写分离建议使用两个数据源，分别是读的数据源和写的数据源，写的数据源必须支持可读可写。

在应用中，在某些场景下我们需要从只读数据源读取数据的时候，通过 `DAO.use('只读数据源的名称').find(...)` 就可以。

## 分库分表

Jboot 的分库分表功能使用了 Sharding-jdbc 实现的，若在 Jboot 应用在需要用到分库分表功能，需要添加 `jboot.datasource.shardingConfigYaml = xxx.yaml ` 的配置，其中 `xxx.yaml` 配置需要放在 classpath 目录下，配置内容参考：https://shardingsphere.apache.org/document/current/cn/manual/sharding-jdbc/configuration/config-yaml/

**注意：** 当在 `jboot.properties` 文件配置 `jboot.datasource.shardingConfigYaml = xxx.yaml`之后，不再需要在 `jboot.properties` 配置 `jboot.datasource.url` 、 `jboot.datasource.user` 和 `jboot.datasource.password` 等，这些配置都转义到 `xxx.yaml` 里进行配置了。

## 分布式事务

Jboot 的分布式事务依赖 Seata 来进行实现，在开始分布式事务之前，请先做好 Seata 的相关配置。

- 创建 Seata 数据库
- 启动 Seata

参考：https://github.com/seata/seata/wiki/Quick-Start

正常启动 Seata 之后，需要在 jboot.properties 配置文件添加如下配置

```
jboot.rpc.filter = seata
jboot.rpc.type = dubbo
jboot.seata.enable = true
jboot.seata.failureHandler = com.alibaba.io.seata.tm.api.DefaultFailureHandlerImpl
jboot.seata.applicationId = Dubbo_Seata_Account_Service
jboot.seata.txServiceGroup = dubbo_seata_tx_group
```
同时，在 resource 目录下添加 `registry.conf` 文件，用于对 seata 进行 registry 配置，内容如下：

```
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  type = "file"

  nacos {
    serverAddr = "localhost"
    namespace = ""
    cluster = "default"
  }
  eureka {
    serviceUrl = "http://localhost:8761/eureka"
    application = "default"
    weight = "1"
  }
  redis {
    serverAddr = "localhost:6379"
    db = "0"
  }
  zk {
    cluster = "default"
    serverAddr = "127.0.0.1:2181"
    session.timeout = 6000
    connect.timeout = 2000
  }
  consul {
    cluster = "default"
    serverAddr = "127.0.0.1:8500"
  }
  etcd3 {
    cluster = "default"
    serverAddr = "http://localhost:2379"
  }
  sofa {
    serverAddr = "127.0.0.1:9603"
    application = "default"
    region = "DEFAULT_ZONE"
    datacenter = "DefaultDataCenter"
    cluster = "default"
    group = "SEATA_GROUP"
    addressWaitTime = "3000"
  }
  file {
    name = "file.conf"
  }
}

config {
  # file、nacos 、apollo、zk、consul、etcd3
  type = "file"

  nacos {
    serverAddr = "localhost"
    namespace = ""
  }
  consul {
    serverAddr = "127.0.0.1:8500"
  }
  apollo {
    app.id = "seata-server"
    apollo.meta = "http://192.168.1.204:8801"
  }
  zk {
    serverAddr = "127.0.0.1:2181"
    session.timeout = 6000
    connect.timeout = 2000
  }
  etcd3 {
    serverAddr = "http://localhost:2379"
  }
  file {
    name = "file.conf"
  }
}
```

> PS：如果不想叫 registry.conf ,请在环境变量里配置 `seata.config.name = yourname` ，那么可以使用 yourname.conf 代替 registry.conf。


同时，在 resource 目录下添加 file.conf 文件，内容如下：

```
transport {
  # tcp udt unix-domain-socket
  type = "TCP"
  #NIO NATIVE
  server = "NIO"
  #enable heartbeat
  heartbeat = true
  #thread factory for netty
  thread-factory {
    boss-thread-prefix = "NettyBoss"
    worker-thread-prefix = "NettyServerNIOWorker"
    server-executor-thread-prefix = "NettyServerBizHandler"
    share-boss-worker = false
    client-selector-thread-prefix = "NettyClientSelector"
    client-selector-thread-size = 1
    client-worker-thread-prefix = "NettyClientWorkerThread"
    # netty boss thread size,will not be used for UDT
    boss-thread-size = 1
    #auto default pin or 8
    worker-thread-size = 8
  }
  shutdown {
    # when destroy server, wait seconds
    wait = 3
  }
  serialization = "seata"
  compressor = "none"
}
service {
  #vgroup->rgroup
  vgroup_mapping.dubbo_seata_tx_group = "default"
  #only support single node
  default.grouplist = "127.0.0.1:8091"
  #degrade current not support
  enableDegrade = false
  #disable
  disable = false
  #unit ms,s,m,h,d represents milliseconds, seconds, minutes, hours, days, default permanent
  max.commit.retry.timeout = "-1"
  max.rollback.retry.timeout = "-1"
}

client {
  async.commit.buffer.limit = 10000
  lock {
    retry.internal = 10
    retry.times = 30
  }
  report.retry.count = 5
  tm.commit.retry.count = 1
  tm.rollback.retry.count = 1
}

## transaction log store
store {
  ## store mode: file、db
  mode = "file"

  ## file store
  file {
    dir = "sessionStore"

    # branch session size , if exceeded first try compress lockkey, still exceeded throws exceptions
    max-branch-session-size = 16384
    # globe session size , if exceeded throws exceptions
    max-global-session-size = 512
    # file buffer size , if exceeded allocate new buffer
    file-write-buffer-cache-size = 16384
    # when recover batch read size
    session.reload.read_size = 100
    # async, sync
    flush-disk-mode = async
  }

  ## database store
  db {
    ## the implement of javax.sql.DataSource, such as DruidDataSource(druid)/BasicDataSource(dbcp) etc.
    datasource = "dbcp"
    ## mysql/oracle/h2/oceanbase etc.
    db-type = "mysql"
    driver-class-name = "com.mysql.jdbc.Driver"
    url = "jdbc:mysql://127.0.0.1:3306/seata"
    user = "mysql"
    password = "mysql"
    min-conn = 1
    max-conn = 3
    global.table = "global_table"
    branch.table = "branch_table"
    lock-table = "lock_table"
    query-limit = 100
  }
}
lock {
  ## the lock store mode: local、remote
  mode = "remote"

  local {
    ## store locks in user's database
  }

  remote {
    ## store locks in the seata's server
  }
}
recovery {
  #schedule committing retry period in milliseconds
  committing-retry-period = 1000
  #schedule asyn committing retry period in milliseconds
  asyn-committing-retry-period = 1000
  #schedule rollbacking retry period in milliseconds
  rollbacking-retry-period = 1000
  #schedule timeout retry period in milliseconds
  timeout-retry-period = 1000
}

transaction {
  undo.data.validation = true
  undo.log.serialization = "jackson"
  undo.log.save.days = 7
  #schedule delete expired undo_log in milliseconds
  undo.log.delete.period = 86400000
  undo.log.table = "undo_log"
}

## metrics settings
metrics {
  enabled = false
  registry-type = "compact"
  # multi exporters use comma divided
  exporter-list = "prometheus"
  exporter-prometheus-port = 9898
}

support {
  ## spring
  spring {
    # auto proxy the DataSource bean
    datasource.autoproxy = false
  }
}
```

> 注意：
> 1、jboot.seata.txServiceGroup 配置的值要注意和 file.conf 里的 vgroup_mapping.xxx 保持一致
> 2、jboot.rpc.filter=seata ##seata在Dubbo中的事务传播过滤器

以上配置完毕后如何使用呢？点击 [这里](../../src/test/java/io/jboot/test/seata) 查看代码实例。