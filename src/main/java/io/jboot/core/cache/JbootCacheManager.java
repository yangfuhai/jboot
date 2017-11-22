/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.core.cache;

import io.jboot.Jboot;
import io.jboot.core.cache.ehcache.JbootEhcacheImpl;
import io.jboot.core.cache.ehredis.JbootEhredisCacheImpl;
import io.jboot.core.cache.redis.JbootRedisCacheImpl;
import io.jboot.core.spi.JbootSpiLoader;


public class JbootCacheManager {

    private static JbootCacheManager me = new JbootCacheManager();

    private JbootCacheManager() {
    }

    private JbootCache jbootCache;

    public static JbootCacheManager me() {
        return me;
    }

    public JbootCache getCache() {
        if (jbootCache == null) {
            jbootCache = buildCache();
        }
        return jbootCache;
    }

    private JbootCache buildCache() {
        JbootCacheConfig config = Jboot.config(JbootCacheConfig.class);
        switch (config.getType()) {
            case JbootCacheConfig.TYPE_EHCACHE:
                return new JbootEhcacheImpl();
            case JbootCacheConfig.TYPE_REDIS:
                return new JbootRedisCacheImpl();
            case JbootCacheConfig.TYPE_EHREDIS:
                return new JbootEhredisCacheImpl();
            case JbootCacheConfig.TYPE_NONE_CACHE:
                return new NoneCacheImpl();
            default:
                return JbootSpiLoader.load(JbootCache.class, config.getType());
        }
    }
}
