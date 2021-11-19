package io.jboot.test.ehcache;


import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheConfig;
import io.jboot.components.cache.ehcache.JbootEhcacheImpl;

public class EhcacheTester {

    private static final String CACHE_NAME = "test";

    public static void main(String[] args) throws Exception {
        JbootCache cache = new JbootEhcacheImpl(new JbootCacheConfig());

        cache.put(CACHE_NAME, "key1", "value1");
        cache.put(CACHE_NAME, "key2", "value2", 2);
        cache.put(CACHE_NAME, "key3", "value3", 3);
        cache.put(CACHE_NAME, "key4", "value4", 4);
        cache.put(CACHE_NAME, "key5", "value5", 8);

        for (int i = 0; i < 10; i++) {
            System.out.println("key1 : " + cache.get(CACHE_NAME, "key1"));
            System.out.println("key2 : " + cache.get(CACHE_NAME, "key2"));
            System.out.println("key3 : " + cache.get(CACHE_NAME, "key3"));
            System.out.println("key4 : " + cache.get(CACHE_NAME, "key4"));
            System.out.println("key5 : " + cache.get(CACHE_NAME, "key5"));
            System.out.println("---------------------------");
            Thread.sleep(1000);
        }
    }
}
