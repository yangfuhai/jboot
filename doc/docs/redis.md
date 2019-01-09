# Redis

## 目录
- Redis简介
- Redis的配置
- Redis的使用
- Redis操作系列方法
- Redis扩展
- Redis集群

## Redis简介

Redis 是完全开源免费的，遵守BSD协议，是一个高性能的key-value数据库。

Redis 与其他 key - value 缓存产品有以下三个特点：

- Redis支持数据的持久化，可以将内存中的数据保存在磁盘中，重启的时候可以再次加载进行使用。
- Redis不仅仅支持简单的key-value类型的数据，同时还提供list，set，zset，hash等数据结构的存储。
- Redis支持数据的备份，即master-slave模式的数据备份。

Redis 优势:

- 性能极高 – Redis能读的速度是110000次/s,写的速度是81000次/s 。
- 丰富的数据类型 – Redis支持二进制案例的 Strings, Lists, Hashes, Sets 及 Ordered Sets 数据类型操作。
- 原子 – Redis的所有操作都是原子性的，意思就是要么成功执行要么失败完全不执行。单个操作是原子性的。多个操作也支持事务，即原子性，通过MULTI和EXEC指令包起来。
- 丰富的特性 – Redis还支持 publish/subscribe, 通知, key 过期等等特性。


## Redis的配置
在使用 Redis 之前，先进行 Redis 配置，配置内容如下：

```
jboot.redis.host=127.0.0.1
jboot.redis.password=xxxx
```

Redis 还支持如下的更多功能的配置：

```
jboot.redis.port = 6379
jboot.redis.timeout = 2000
jboot.redis.database
jboot.redis.clientName
jboot.redis.testOnCreate
jboot.redis.testOnBorrow
jboot.redis.testOnReturn
jboot.redis.testWhileIdle
jboot.redis.minEvictableIdleTimeMillis
jboot.redis.timeBetweenEvictionRunsMillis
jboot.redis.numTestsPerEvictionRun
jboot.redis.maxAttempts
jboot.redis.maxTotal
jboot.redis.maxIdle
jboot.redis.minIdle
jboot.redis.maxWaitMillis

# 自定义序列化方案
jboot.redis.serializer 
```

## Redis的使用

配置后，就可以通过如下代码获取 JbootRedis 对redis进行操作：

```
JbootRedis redis = Jboot.getRedis();
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

```
jboot.redis.host=127.0.0.1
jboot.redis.password=xxxx
```

在集群模式下，只需要在 jboot.redis.host 配置为多个主机即可，例如：


```
## 多个IP用英文逗号隔开，端口号用英文冒号（:）。
Jboot.redis.host=192.168.1.33,192.168.1.34:3307,192.168.1.35
jboot.redis.password=xxxx
```
