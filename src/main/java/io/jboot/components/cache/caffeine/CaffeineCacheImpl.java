/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.components.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.components.cache.JbootCacheBase;
import io.jboot.components.cache.JbootCacheConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CaffeineCacheImpl extends JbootCacheBase {

    private Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    public CaffeineCacheImpl(JbootCacheConfig config) {
        super(config);
    }

    protected Cache getCacheOnly(String cacheName) {
        cacheName = buildCacheName(cacheName);
        return cacheMap.get(cacheName);
    }

    protected Cache getCache(String cacheName) {
        cacheName = buildCacheName(cacheName);
        Cache cache = cacheMap.get(cacheName);
        if (cache == null) {
            synchronized (CaffeineCacheImpl.class) {
                cache = cacheMap.get(cacheName);
                if (cache == null) {
                    cache = createCacheBuilder().build(cacheName);
                    cacheMap.put(cacheName,cache);
                }
            }
        }
        return cache;
    }

    protected CaffeineCacheBuilder createCacheBuilder() {
        return new DefaultCaffeineCacheBuilder();
    }


    @Override
    public <T> T get(String cacheName, Object key) {
        Cache cache = getCache(cacheName);
        CaffeineCacheObject data = (CaffeineCacheObject) cache.getIfPresent(key);
        if (data == null) {
            return null;
        }

        if (data.isDue()) {
            cache.invalidate(key);
            return null;
        }

        T cacheData = (T) data.getValue();
        if (config.isDevMode()) {
            println("CaffeineCache GET: cacheName[" + buildCacheName(cacheName) + "] cacheKey[" + key + "] value:" + cacheData);
        }
        return cacheData;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        putData(getCache(cacheName), key, new CaffeineCacheObject(value));
        if (config.isDevMode()) {
            println("CaffeineCache PUT: cacheName[" + buildCacheName(cacheName) + "] cacheKey[" + key + "] value:" + value);
        }
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        putData(getCache(cacheName), key, new CaffeineCacheObject(value, liveSeconds));
        if (config.isDevMode()) {
            println("CaffeineCache PUT: cacheName[" + buildCacheName(cacheName) + "] cacheKey[" + key + "] value:" + value);
        }
    }



    @Override
    public void remove(String cacheName, Object key) {
        Cache cache = getCacheOnly(cacheName);
        if (cache != null) {
            cache.invalidate(key);
        }
        if (config.isDevMode()) {
            println("CaffeineCache REMOVE: cacheName[" + buildCacheName(cacheName) + "] cacheKey[" + key + "]");
        }
    }

    @Override
    public void removeAll(String cacheName) {
        Cache cache = getCacheOnly(cacheName);
        if (cache != null) {
            cache.invalidateAll();
        }
        cacheMap.remove(buildCacheName(cacheName));
        if (config.isDevMode()) {
            println("CaffeineCache REMOVEALL: cacheName[" + buildCacheName(cacheName) + "]");
        }
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        Cache cache = getCache(cacheName);
        CaffeineCacheObject data = (CaffeineCacheObject) cache.getIfPresent(key);
        if (data == null || data.isDue()) {
            Object newValue = dataLoader.load();
            if (newValue != null) {
                data = new CaffeineCacheObject(newValue);
                putData(cache, key, data);
                if (config.isDevMode()) {
                    println("CaffeineCache PUT: cacheName[" + buildCacheName(cacheName) + "] cacheKey[" + key + "] value:" + newValue);
                }
            }
            return (T) newValue;
        } else {
            Object cacheData = data.getValue();
            if (config.isDevMode()) {
                println("CaffeineCache GET: cacheName[" + buildCacheName(cacheName) + "] cacheKey[" + key + "] value:" + cacheData);
            }
            return (T) cacheData;
        }
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
        Cache cache = getCache(cacheName);
        CaffeineCacheObject data = (CaffeineCacheObject) cache.getIfPresent(key);
        if (data == null || data.isDue()) {
            Object newValue = dataLoader.load();
            if (newValue != null) {
                data = new CaffeineCacheObject(newValue, liveSeconds);
                putData(cache, key, data);
                if (config.isDevMode()) {
                    println("CaffeineCache PUT: cacheName[" +buildCacheName(cacheName)+ "] cacheKey["+key+"] value:" + newValue);
                }
            }
            return (T) newValue;
        } else {
            Object cacheData = data.getValue();
            if (config.isDevMode()) {
                println("CaffeineCache GET: cacheName[" +buildCacheName(cacheName)+ "] cacheKey["+key+"] value:" + cacheData);
            }
            return (T) cacheData;
        }
    }

    @Override
    public Integer getTtl(String cacheName, Object key) {
        Cache cache = getCacheOnly(cacheName);
        if (cache == null) {
            return null;
        }

        CaffeineCacheObject data = (CaffeineCacheObject) cache.getIfPresent(key);
        if (data == null) {
            return null;
        }

        return data.getTtl();
    }

    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        Cache cache = getCacheOnly(cacheName);
        if (cache == null) {
            return;
        }

        CaffeineCacheObject data = (CaffeineCacheObject) cache.getIfPresent(key);
        if (data == null) {
            return;
        }

        data.setLiveSeconds(seconds);
        putData(cache, key, data);

        if (config.isDevMode()) {
            println("CaffeineCache SETTTL: cacheName[" +buildCacheName(cacheName)+ "] cacheKey["+key+"] seconds:" + seconds);
        }
    }


    @Override
    public List getNames() {
        return new ArrayList(cacheMap.keySet());
    }

    @Override
    public List getKeys(String cacheName) {
        Cache cache = getCacheOnly(cacheName);
        return cache == null ? null : new ArrayList(cache.asMap().keySet());
    }


    protected void putData(Cache cache, Object key, CaffeineCacheObject value) {
        value.setCachetime(System.currentTimeMillis());
        cache.put(key, value);
    }
}
