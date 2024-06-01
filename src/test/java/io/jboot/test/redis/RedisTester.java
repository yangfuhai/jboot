package io.jboot.test.redis;

import io.jboot.app.JbootApplication;
import io.jboot.components.limiter.redis.RedisRateLimitUtil;
import io.jboot.support.redis.JbootRedis;
import io.jboot.support.redis.JbootRedisManager;
import io.jboot.support.redis.RedisScanResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RedisTester {

    @Before
    public void config() {
        JbootApplication.setBootArg("jboot.redis.host", "127.0.0.1");
        JbootApplication.setBootArg("jboot.redis.password", "123456");
        JbootApplication.setBootArg("jboot.redis.port", "6379");
    }

    @Test
    public void testGetAndSet() {
        JbootRedis redis = JbootRedisManager.me().getRedis();
        String key = "JbootRedisValue";
        Assert.assertEquals("OK", redis.set(key, "10"));
        Assert.assertEquals("10", redis.get(key));
        System.out.println(redis.ttl(key));
        redis.del(key);
    }

    @Test
    public void testEval() {
        JbootRedis redis = JbootRedisManager.me().getRedis();
        String response = (String) redis.eval("return KEYS[1]", 1, "key1");
        Assert.assertEquals("key1", response);
    }

    @Test
    public void testRateLimit() {
        String resource = "limited-resource";
        Assert.assertTrue(RedisRateLimitUtil.tryAcquire(resource, 2, 1));
        Assert.assertTrue(RedisRateLimitUtil.tryAcquire(resource, 2, 1));
        Assert.assertFalse(RedisRateLimitUtil.tryAcquire(resource, 2, 1));
    }

    public static void main(String[] args) {
        JbootApplication.setBootArg("jboot.redis.host", "127.0.0.1");
        JbootApplication.setBootArg("jboot.redis.database", "3");

//        for (int i=0;i<2350;i++){
//            redis.set("testkey:"+i,i);
//        }
//        System.out.println("set ok");
//
//        System.out.println(getKeys("testkey").size());
//
//
//        JbootRedisCacheImpl redisCache = new JbootRedisCacheImpl();
//        for (int i = 0; i < 23; i++) {
//            redisCache.put("myName", "myKey" + i, i);
//        }
//        System.out.println(redisCache.getKeys("myName"));
//        System.out.println(redisCache.getNames());
//        redisCache.removeAll("myName");
//        System.out.println(redisCache.getKeys("myName"));
//        System.out.println(redisCache.getNames());

    }

    public static List getKeys(String cacheName) {
        JbootRedis redis = JbootRedisManager.me().getRedis();
        ;
        List<String> keys = new ArrayList<>();
        String cursor = "0";
        int scanCount = 1000;
        List<String> scanResult = null;
        do {
            RedisScanResult redisScanResult = redis.scan(cacheName + ":*", cursor, scanCount);
            if (redisScanResult != null) {
                scanResult = redisScanResult.getResults();
                cursor = redisScanResult.getCursor();
                if (scanResult != null && scanResult.size() > 0) {
                    keys.addAll(scanResult);
                }
                if (redisScanResult.isCompleteIteration()) {
                    //终止循环
                    scanResult = null;
                }
            }
        } while (scanResult != null && scanResult.size() != 0);

        return keys;
    }
}
