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
package io.jboot.components.cache;

import io.jboot.Jboot;
import io.jboot.components.cache.caffeine.CaffeineCacheImpl;
import io.jboot.components.cache.caredis.JbootCaredisCacheImpl;
import io.jboot.components.cache.ehcache.JbootEhcacheImpl;
import io.jboot.components.cache.ehredis.JbootEhredisCacheImpl;
import io.jboot.components.cache.j2cache.J2cacheImpl;
import io.jboot.components.cache.none.NoneCacheImpl;
import io.jboot.components.cache.redis.JbootRedisCacheImpl;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ConfigUtil;
import io.jboot.utils.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JbootCacheManager {

    private static JbootCacheManager me = new JbootCacheManager();

    private JbootCacheManager() {
    }

    private Map<String, JbootCache> cacheMap = new ConcurrentHashMap<>();
    private CachePrinter printer = new CachePrinter() {
        @Override
        public void println(String debugInfo) {
            CachePrinter.super.println(debugInfo);
        }
    };

    public static JbootCacheManager me() {
        return me;
    }

    public JbootCache getCache() {
        return getCache("default");
    }

    public JbootCache getCache(String name) {
        if (StrUtil.isBlank(name)) {
            throw new IllegalArgumentException("Cache name must not be null or blank.");
        }

        JbootCache cache = cacheMap.get(name);

        if (cache == null) {
            Map<String, JbootCacheConfig> configModels = ConfigUtil.getConfigModels(JbootCacheConfig.class);
            JbootCacheConfig.TYPES.forEach(configModels::remove);

            configModels.putIfAbsent("default", Jboot.config(JbootCacheConfig.class));

            if (!configModels.containsKey(name)) {
                throw new JbootIllegalConfigException("Please config \"jboot.cache." + name + ".type\" in your jboot.properties.");
            }

            JbootCacheConfig cacheConfig = configModels.get(name);
            JbootCache newCache = buildCache(cacheConfig);
            if (newCache != null) {
                cacheMap.putIfAbsent(name, newCache);
            }

            cache = cacheMap.get(name);
        }

        return cache;
    }


    private synchronized JbootCache buildCache(JbootCacheConfig config) {

        switch (config.getType()) {
            case JbootCacheConfig.TYPE_EHCACHE:
                return new JbootEhcacheImpl(config);
            case JbootCacheConfig.TYPE_REDIS:
                return new JbootRedisCacheImpl(config);
            case JbootCacheConfig.TYPE_EHREDIS:
                return new JbootEhredisCacheImpl(config);
            case JbootCacheConfig.TYPE_J2CACHE:
                return new J2cacheImpl(config);
            case JbootCacheConfig.TYPE_CAFFEINE:
                return new CaffeineCacheImpl(config);
            case JbootCacheConfig.TYPE_CAREDIS:
                return new JbootCaredisCacheImpl(config);
            case JbootCacheConfig.TYPE_NONE:
                return new NoneCacheImpl(config);
            default:
                return JbootSpiLoader.load(JbootCache.class, config.getType(), config);
        }
    }

    public CachePrinter getPrinter() {
        return printer;
    }

    public void setPrinter(CachePrinter printer) {
        this.printer = printer;
    }
}
