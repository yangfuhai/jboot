# 缓存

## 目录

- 描述
- 配置
- 使用
- EhCache
- Redis
- EhRedis
- J2Cache
- NoneCache

## 描述

Jboot 定位为高性能的微服务框架，然而高性能离不开合理的缓存设计。Jboot 内置了丰富的框架支持，比如：

- ehcache
- redis
- ehredis
- j2cache

## 配置

默认情况下，用户无需做任何配置就可以使用 Jboot 的缓存功能，默认情况下 Jboot 是使用 `Ehcache` 作为 Jboot 的缓存方案。

如果需要修改把 `Ehcahce` 方案修改为使用 `redis` ，则可以添加如下的配置：

```
jboot.cache.type = redis
```

在使用 `redis` 作为默认的缓存方案时，需要配置上 `redis` 的相关信息，例如：

```
jboot.cache.redis.host = 127.0.0.1
jboot.cache.redis.port = 3306
jboot.cache.redis.password
jboot.cache.redis.database
jboot.cache.redis.timeout
jboot.cache.redis.clientName
jboot.cache.redis.testOnCreate
jboot.cache.redis.testOnBorrow
jboot.cache.redis.testOnReturn
jboot.cache.redis.testWhileIdle
jboot.cache.redis.minEvictableIdleTimeMillis
jboot.cache.redis.timeBetweenEvictionRunsMillis
jboot.cache.redis.numTestsPerEvictionRun
jboot.cache.redis.maxAttempts
jboot.cache.redis.maxTotal
jboot.cache.redis.maxIdle
jboot.cache.redis.maxWaitMillis
jboot.cache.redis.serializer
```
当，以上未配置的时候，Jboot 自动会去寻找 `redis` 模块来使用，`redis` 的配置为：

```
jboot.redis.host
jboot.redis.port
jboot.redis.password
jboot.redis.database
jboot.redis.timeout
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
jboot.redis.maxWaitMillis
jboot.redis.serializer
```

以下是 `JbootRedisCacheImpl` 的部分代码：

```java
public class JbootRedisCacheImpl extends JbootCacheBase {


    private JbootRedis redis;

    public JbootRedisCacheImpl() {
        JbootRedisCacheConfig redisConfig = Jboot.config(JbootRedisCacheConfig.class);

        //优先使用 jboot.cache.redis 的配置
        if (redisConfig.isConfigOk()) {
            redis = JbootRedisManager.me().getRedis(redisConfig);
        } 
        // 当 jboot.cache.redis 配置不存在时，
        // 使用 jboot.redis 的配置
        else {
            redis = Jboot.getRedis();
        }

        if (redis == null) {
            throw new JbootIllegalConfigException("can not get redis, please check your jboot.properties , please correct config jboot.cache.redis.host or jboot.redis.host ");
        }
    }

    //....
}    
```

## 使用缓存

#### 显式代码调用

Jboot 提供了一个工具类 CacheUtil，我们可以直接通过 CacheUtil 来操作缓存。

```
# 添加内容到缓存
CacheUtil.put("cacheName","key","value")

# 添加内容到缓存，并设置该缓存的有效期为 10 秒钟
CacheUtil.put("cacheName","key","value",10) 

# 获取缓存内容
String value = CacheUtil.get("cacheName","key");

# 删除缓存
CacheUtl.remove("cacheName","key")

# 重新设置某个缓存的有效期
CacheUtil.setTtl("cacheName","key")

```

当一个系统有多个缓存组件的时候，可能有 redis 或者 ehcache 等，则可以使用如下use("type") 进行操作。
```
CacheUtil.use("redis").put("cacheName","key","value")


CacheUtil.use("ehcache").put("cacheName","key","value") 
```


#### 通过注解使用缓存

- @Cacheable
- @CacheEvict
- @CachesEvict
- @CachePut

在 service 中，Jboot 提供了以上的 4 个组件，方便我们进行缓存操作，而无需使用 CacheUtil 来显示调用。

例如：

```java
@Bean
public class CommentServiceImpl implements CommentService {

    @Override
    public String getCommentById(String id) {
        return "id:" + id + "  data:" + UUID.randomUUID();
    }

    @Override
    @Cacheable(name = "cacheName", key = "#(id)")
    public String getCommentByIdWithCache(String id) {
        return "id:" + id + "  data:" + UUID.randomUUID();
    }


    @Override
    @Cacheable(name = "cacheName", key = "#(id)", liveSeconds = 5)
    public String getCommentByIdWithCacheTime(String id) {
        return "id:" + id + "  data:" + UUID.randomUUID();
    }


    @Override
    @CachePut(name = "cacheName", key = "#(id)")
    public String updateCache(String id) {
        return "id:" + id + "  data:" + UUID.randomUUID();
    }

    @Override
    @CacheEvict(name = "cacheName", key = "#(id)")
    public void delCache(String id) {
    }
}
```

- `getCommentById` 方法没有使用任何注解，每次调用的时候,data后面的都是一个新的随机数。
- `getCommentByIdWithCache` 使用 `@Cacheable` 注解，缓存的 key 是传入进来的 id，因此只要是同一个 id 值，每次返回的随机数都是一样的，因为随机数已经被缓存起来了。
- `getCommentByIdWithCacheTime` 使用 `@Cacheable` 注解，但是添加了 `5秒` 的时间限制，因此，在 5秒钟内，无论调用多少次，返回的随机数都是一样的，5秒之后缓存被删除，再次调用之后会是一个新的随机数，新的随机数会继续缓存 5秒钟。
- `updateCache` 使用了注解 `@CachePut` ，每次调用此方法之后，会更新掉该 id 值的缓存
- `delCache` 使用了 `@CacheEvict` 注解，每次调用会删除该 id 值的缓存
