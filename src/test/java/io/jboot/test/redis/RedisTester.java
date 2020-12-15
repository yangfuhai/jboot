package io.jboot.test.redis;

import io.jboot.app.JbootApplication;
import io.jboot.support.redis.JbootRedis;
import io.jboot.support.redis.JbootRedisManager;
import io.jboot.support.redis.RedisScanResult;

import java.util.ArrayList;
import java.util.List;

public class RedisTester {

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
