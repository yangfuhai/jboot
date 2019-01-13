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

Jboot 的数据库是依赖 JFinal 的 ORM 做基本的数据库操作，同时依赖 apahce-sphere 来做分库、分表和分布式事务。

因此，在使用 Jboot 操作数据库的时候，建议对 JFinal 的 ORM 功能和 Apache Sharding-Sphere 有所了解。

- JFinal的数据库操作相关文档：https://www.jfinal.com/doc/5-1
- Apache Sharding-Sphere 文档：http://shardingsphere.io/document/current/cn/overview/


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