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

#### 通过注解使用缓存
