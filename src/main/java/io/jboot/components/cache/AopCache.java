package io.jboot.components.cache;


import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;

import java.util.List;

public class AopCache {

    private static JbootCache aopCache;

    static JbootCache getAopCache() {
        if (aopCache == null) {
            synchronized (AopCache.class) {
                if (aopCache == null) {
                    aopCache = JbootCacheManager.me().getCache(Jboot.config(JbootCacheConfig.class).getAopCacheType());
                }
            }
        }
        return aopCache;
    }

    public static <T> T get(String cacheName, Object key) {
        return getAopCache().get(cacheName, key);
    }

    public static void put(String cacheName, Object key, Object value) {
        getAopCache().put(cacheName, key, value);
    }

    public static void put(String cacheName, Object key, Object value, int liveSeconds) {
        getAopCache().put(cacheName, key, value, liveSeconds);
    }

    public static List getKeys(String cacheName) {
        return getAopCache().getKeys(cacheName);
    }

    public static void remove(String cacheName, Object key) {
        getAopCache().remove(cacheName, key);
    }

    public static void removeAll(String cacheName) {
        getAopCache().removeAll(cacheName);
    }

    public static <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        return getAopCache().get(cacheName, key, dataLoader);
    }

    public static <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
        return getAopCache().get(cacheName, key, dataLoader, liveSeconds);
    }

    public static Integer getTtl(String cacheName, Object key) {
        return getAopCache().getTtl(cacheName, key);
    }

    public static void setTtl(String cacheName, Object key, int seconds) {
        getAopCache().setTtl(cacheName, key, seconds);
    }
}
