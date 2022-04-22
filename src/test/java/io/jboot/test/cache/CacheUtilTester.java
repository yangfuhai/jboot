package io.jboot.test.cache;

import io.jboot.app.config.JbootConfigManager;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.utils.CacheUtil;

import java.util.List;

public class CacheUtilTester {

    public static void main(String[] args) {
        JbootConfigManager.setBootArg("jboot.cache.redis.globalKeyPrefix","globalKeyPrefix");
        JbootConfigManager.setBootArg("jboot.cache.type","redis");
        JbootConfigManager.setBootArg("jboot.redis.host","127.0.0.1");

        List names = JbootCacheManager.me().getCache().getNames();
        System.out.println(names);


        String cacheName = "tester";
        CacheUtil.put(cacheName,"key1","value1");
        CacheUtil.put(cacheName,"key2","value2");

        System.out.println(CacheUtil.get(cacheName,"key1").toString());
        System.out.println(CacheUtil.get(cacheName,"key2").toString());


        List keys = CacheUtil.getKeys(cacheName);
        System.out.println(keys);


        CacheUtil.removeAll(cacheName);

         keys = CacheUtil.getKeys(cacheName);
        System.out.println(keys);
    }
}
