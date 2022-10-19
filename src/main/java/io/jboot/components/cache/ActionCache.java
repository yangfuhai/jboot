package io.jboot.components.cache;


import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.IDataLoader;

import java.util.List;

public class ActionCache {

    private static final Log LOG = Log.getLog(ActionCache.class);

    private static JbootCache actionCache;

    public static JbootCache setThreadCacheNamePrefix(String cacheNamePrefix) {
        return getActionCache().setThreadCacheNamePrefix(cacheNamePrefix);
    }

    public static void clearThreadCacheNamePrefix() {
        getActionCache().clearThreadCacheNamePrefix();
    }

    static JbootCache getActionCache() {
        if (actionCache == null) {
            actionCache = JbootCacheManager.me().getCache(AopCacheConfig.getInstance().getUseCacheName());
        }
        return actionCache;
    }

    public static void setActionCache(JbootCache actionCache) {
        ActionCache.actionCache = actionCache;
    }


    public static void put(String cacheName, Object key, Object value) {
        try {
            getActionCache().put(cacheName, key, value);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }
    }

    public static void put(String cacheName, Object key, Object value, int liveSeconds) {
        try {
            getActionCache().put(cacheName, key, value, liveSeconds);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }
    }

    public static List getKeys(String cacheName) {
        try {
            return getActionCache().getKeys(cacheName);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }
        return null;
    }

    public static void remove(String cacheName, Object key) {
        try {
            getActionCache().remove(cacheName, key);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }
    }

    public static void removeAll(String cacheName) {
        try {
            getActionCache().removeAll(cacheName);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }
    }

    public static <T> T get(String cacheName, Object key) {
        try {
            return getActionCache().get(cacheName, key);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
            remove(cacheName, key);
        }
        return null;
    }


    public static <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        try {
            return getActionCache().get(cacheName, key, dataLoader);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
            remove(cacheName, key);
        }
        return null;
    }


    public static <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
        try {
            return getActionCache().get(cacheName, key, dataLoader, liveSeconds);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
            remove(cacheName, key);
        }
        return null;
    }


    public static Integer getTtl(String cacheName, Object key) {
        try {
            return getActionCache().getTtl(cacheName, key);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }
        return null;
    }


    public static void setTtl(String cacheName, Object key, int seconds) {
        try {
            getActionCache().setTtl(cacheName, key, seconds);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }
    }

    private static final ActionCacheConfig CONFIG = ActionCacheConfig.getInstance();

    public static void putDataToCache(String cacheName, String cacheKey, Object data, int liveSeconds) {
        liveSeconds = liveSeconds > 0 ? liveSeconds : CONFIG.getLiveSeconds();
        if (liveSeconds > 0) {
            put(cacheName, cacheKey, data, liveSeconds);
        } else {
            put(cacheName, cacheKey, data);
        }
    }
}
