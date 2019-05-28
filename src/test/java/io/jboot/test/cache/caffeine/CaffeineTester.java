package io.jboot.test.cache.caffeine;


import io.jboot.Jboot;
import io.jboot.app.JbootApplication;
import io.jboot.components.cache.JbootCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CaffeineTester {

    private static final String cacheName = "cachename";

    @Test
    public void testPut() {
        Jboot.getCache().put(cacheName, "key", "value");
        String value = Jboot.getCache().get(cacheName, "key");
        Assert.assertNotNull(value);
    }

    @Test
    public void testGet() {
        JbootCache cache = Jboot.getCache();
        cache.put(cacheName, "key", "value~~~~~~~");
        String value = cache.get(cacheName, "key");
        System.out.println("value:"+value);
        Assert.assertTrue("value~~~~~~~".equals(value));
    }

    @Test
    public void testTtl() throws InterruptedException {
        JbootCache cache = Jboot.getCache();
        cache.put(cacheName, "key", "value~~~~~~~",10);
        for (int i = 0;i<10;i++){
            System.out.println(cache.getTtl(cacheName,"key"));
            Thread.sleep(1000);
        }
    }


    @Test
    public void testDue() throws InterruptedException {
        JbootCache cache = Jboot.getCache();
        cache.put(cacheName, "key", "value~~~~~~~",10);
        for (int i = 0;i<15;i++){
            System.out.println((String) cache.get(cacheName,"key"));
            Thread.sleep(1000);
        }
    }


    @Before
    public void config() {
        JbootApplication.setBootArg("jboot.cache.type", "caffeine");
    }
}
