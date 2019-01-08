/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.cache;

import io.jboot.Jboot;
import io.jboot.components.cache.ehcache.JbootEhcacheImpl;
import io.jboot.components.cache.ehredis.JbootEhredisCacheImpl;
import io.jboot.components.cache.j2cache.J2cacheImpl;
import io.jboot.components.cache.none.NoneCacheImpl;
import io.jboot.components.cache.redis.JbootRedisCacheImpl;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.utils.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JbootCacheManager {

    private static JbootCacheManager me = new JbootCacheManager();

    private JbootCacheManager() {
    }

    private Map<String, JbootCache> cacheMap = new ConcurrentHashMap<>();
    private JbootCacheConfig config = Jboot.config(JbootCacheConfig.class);

    public static JbootCacheManager me() {
        return me;
    }

    public JbootCache getCache() {
        return getCache(config.getType());
    }

    public JbootCache getCache(String type) {
        if (StrUtil.isBlank(type)) {
            throw new IllegalArgumentException("type must not be null or blank.");
        }

        JbootCache cache = cacheMap.get(type);
        if (cache != null) {
            return cache;
        }

        synchronized (type) {
            if (cache == null) {
                JbootCacheConfig cacheConfig = new JbootCacheConfig();
                cacheConfig.setType(type);
                cache = buildCache(cacheConfig);
                cacheMap.put(type, cache);
            }
        }

        return cache;
    }


    private JbootCache buildCache(JbootCacheConfig config) {

        switch (config.getType()) {
            case JbootCacheConfig.TYPE_EHCACHE:
                return new JbootEhcacheImpl();
            case JbootCacheConfig.TYPE_REDIS:
                return new JbootRedisCacheImpl();
            case JbootCacheConfig.TYPE_EHREDIS:
                return new JbootEhredisCacheImpl();
            case JbootCacheConfig.TYPE_J2CACHE:
                return new J2cacheImpl();
            case JbootCacheConfig.TYPE_NONE:
                return new NoneCacheImpl();
            default:
                return JbootSpiLoader.load(JbootCache.class, config.getType());
        }
    }
}
