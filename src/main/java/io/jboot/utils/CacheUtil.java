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
package io.jboot.utils;

import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheManager;

import java.util.List;

/**
 * Usage：
 * 1、CacheUtil.get("cacheName","key")
 * 2、CacheUtil.use("default").get("cacheName","key")
 */
public class CacheUtil {

    public static JbootCache use(String name) {
        return JbootCacheManager.me().getCache(name);
    }

    public static JbootCache setThreadCacheNamePrefix(String cacheNamePrefix) {
        return JbootCacheManager.me().getCache().setThreadCacheNamePrefix(cacheNamePrefix);
    }

    public static void clearThreadCacheNamePrefix() {
        JbootCacheManager.me().getCache().clearThreadCacheNamePrefix();
    }

    public static <T> T get(String cacheName, Object key) {
        return Jboot.getCache().get(cacheName, key);
    }

    public static void put(String cacheName, Object key, Object value) {
        Jboot.getCache().put(cacheName, key, value);
    }

    public static void put(String cacheName, Object key, Object value, int liveSeconds) {
        Jboot.getCache().put(cacheName, key, value, liveSeconds);
    }

    public static List getKeys(String cacheName) {
        return Jboot.getCache().getKeys(cacheName);
    }

    public static void remove(String cacheName, Object key) {
        Jboot.getCache().remove(cacheName, key);
    }

    public static void removeAll(String cacheName) {
        Jboot.getCache().removeAll(cacheName);
    }

    public static <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        return Jboot.getCache().get(cacheName, key, dataLoader);
    }

    public static <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
        return Jboot.getCache().get(cacheName, key, dataLoader, liveSeconds);
    }

    public static Integer getTtl(String cacheName, Object key) {
        return Jboot.getCache().getTtl(cacheName, key);
    }

    public static void setTtl(String cacheName, Object key, int seconds) {
        Jboot.getCache().setTtl(cacheName, key, seconds);
    }
}
