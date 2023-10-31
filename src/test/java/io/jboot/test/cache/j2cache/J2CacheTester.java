package io.jboot.test.cache.j2cache;


import io.jboot.Jboot;
import io.jboot.app.JbootApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class J2CacheTester {

    @Test
    public void testPut() {
        Jboot.getCache().put("cachename", "key", "value");
        String value = Jboot.getCache().get("cachename", "key");
        Assert.assertNotNull(value);
    }

    @Test
    public void testGet() {
        Jboot.getCache().put("cachename", "key", "value");
        String value = Jboot.getCache().get("cachename", "key");
        Assert.assertTrue("value".equals(value));
    }


    @Before
    public void config() {
        JbootApplication.setBootArg("jboot.cache.type", "ehcache");
    }
}
