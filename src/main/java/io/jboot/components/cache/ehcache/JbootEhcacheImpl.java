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
package io.jboot.components.cache.ehcache;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCacheBase;
import io.jboot.components.cache.JbootCacheConfig;
import io.jboot.utils.StrUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import java.util.Arrays;
import java.util.List;


public class JbootEhcacheImpl extends JbootCacheBase {

    private CacheManager cacheManager;
    private static Object locker = new Object();

    private CacheEventListener cacheEventListener;

    public JbootEhcacheImpl(JbootCacheConfig config) {
        super(config);
        JbootEhCacheConfig ehconfig = Jboot.config(JbootEhCacheConfig.class);
        if (StrUtil.isBlank(ehconfig.getConfigFileName())) {
            cacheManager = CacheManager.create();
        } else {
            String configPath = ehconfig.getConfigFileName();
            if (!configPath.startsWith("/")) {
                configPath = PathKit.getRootClassPath() + "/" + configPath;
            }
            cacheManager = CacheManager.create(configPath);
        }
    }


    public CacheEventListener getCacheEventListener() {
        return cacheEventListener;
    }

    public void setCacheEventListener(CacheEventListener cacheEventListener) {
        this.cacheEventListener = cacheEventListener;
    }

    public Cache getOrAddCache(String cacheName) {
        cacheName = buildCacheName(cacheName);
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            synchronized (locker) {
                cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    cacheManager.addCacheIfAbsent(cacheName);
                    cache = cacheManager.getCache(cacheName);
                    if (cacheEventListener != null) {
                        cache.getCacheEventNotificationService().registerListener(cacheEventListener);
                    }
                }
            }
        }
        return cache;
    }


    @Override
    public <T> T get(String cacheName, Object key) {
        Element element = getOrAddCache(cacheName).get(key);
        if (element == null) {
            return null;
        }

        Object objectValue = element.getObjectValue();
        if (config.isDevMode()) {
            println("Ehcache GET: cacheName[" + buildCacheName(cacheName) + "] cacheKey[" + key + "] value:" + objectValue);
        }
        return (T) objectValue;

    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        getOrAddCache(cacheName).put(new Element(key, value));

        if (config.isDevMode()) {
            println("Ehcache PUT: cacheName[" +buildCacheName(cacheName)+ "] cacheKey["+key+"] value:" + value);
        }
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        if (liveSeconds <= 0) {
            put(cacheName, key, value);
            return;
        }
        Element element = new Element(key, value);
        element.setTimeToLive(liveSeconds);
        getOrAddCache(cacheName).put(element);

        if (config.isDevMode()) {
            println("Ehcache PUT: cacheName[" +buildCacheName(cacheName)+ "] cacheKey["+key+"] value:" + value);
        }
    }

    @Override
    public void remove(String cacheName, Object key) {
        getOrAddCache(cacheName).remove(key);

        if (config.isDevMode()) {
            println("Ehcache REMOVE: cacheName[" +buildCacheName(cacheName)+ "] cacheKey["+key+"]");
        }
    }

    @Override
    public void removeAll(String cacheName) {
        getOrAddCache(cacheName).removeAll();
        cacheManager.removeCache(cacheName);

        if (config.isDevMode()) {
            println("Ehcache REMOVEALL: cacheName[" +buildCacheName(cacheName)+ "]");
        }
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        Object data = get(cacheName, key);
        if (data == null) {
            data = dataLoader.load();
            put(cacheName, key, data);
        }

        if (config.isDevMode()) {
            println("Ehcache GET: cacheName[" +buildCacheName(cacheName)+ "] cacheKey["+key+"] value:" + data);
        }
        return (T) data;
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
        if (liveSeconds <= 0) {
            return get(cacheName, key, dataLoader);
        }
        Object data = get(cacheName, key);
        if (data == null) {
            data = dataLoader.load();
            put(cacheName, key, data, liveSeconds);
        }

        if (config.isDevMode()) {
            println("Ehcache GET: cacheName[" +buildCacheName(cacheName)+ "] cacheKey["+key+"] value:" + data);
        }

        return (T) data;
    }

    @Override
    public Integer getTtl(String cacheName, Object key) {
        Element element = getOrAddCache(cacheName).get(key);
        return element != null ? element.getTimeToLive() : null;
    }


    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        Element element = getOrAddCache(cacheName).get(key);
        if (element == null) {
            return;
        }

        element.setTimeToLive(seconds);
        getOrAddCache(cacheName).put(element);

        if (config.isDevMode()) {
            println("Ehcache SETTTL: cacheName[" +buildCacheName(cacheName)+ "] cacheKey["+key+"] seconds:" + seconds);
        }
    }

    @Override
    public List getNames() {
        return Arrays.asList(cacheManager.getCacheNames());
    }

    @Override
    public List getKeys(String cacheName) {
        return getOrAddCache(cacheName).getKeys();
    }


    public CacheManager getCacheManager() {
        return cacheManager;
    }

}
