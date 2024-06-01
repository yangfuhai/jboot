package io.jboot.test.cache.redis;


import io.jboot.Jboot;
import io.jboot.app.JbootApplication;
import io.jboot.components.cache.redis.JbootRedisCacheImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RedisCacheTester {

    @Before
    public void config() {
        JbootApplication.setBootArg("jboot.cache.type", "redis");
        JbootApplication.setBootArg("jboot.cache.redis.host", "127.0.0.1");
        JbootApplication.setBootArg("jboot.cache.redis.password", "123456");
        JbootApplication.setBootArg("jboot.cache.redis.port", "6379");
        JbootApplication.setBootArg("jboot.cache.redis.globalKeyPrefix", "myapp");
    }

    @Test
    public void testCacheType() {
        Assert.assertTrue(Jboot.getCache().getClass() == JbootRedisCacheImpl.class);
    }


    @Test
    public void testPut() {
        Jboot.getCache().put("cachename", "key", "value");
        String value = Jboot.getCache().get("cachename", "key");
        System.out.println(Jboot.getCache().getTtl("cachename", "key"));
        Assert.assertNotNull(value);
    }

    @Test
    public void testGet() {
        Jboot.getCache().put("cachename", "key", "value1111");
        String value = Jboot.getCache().get("cachename", "key");
        System.out.println("testGet:" + value);
        Assert.assertTrue("value1111".equals(value));
    }



}
