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


import io.jboot.utils.StrUtil;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JbootCacheBase implements JbootCache {

    protected JbootCacheConfig config;

    public JbootCacheBase(JbootCacheConfig config) {
        this.config = config;
    }

    private Set<String> ignoreThreadCacheNames = ConcurrentHashMap.newKeySet();
    private ThreadLocal<String> CACHE_NAME_PREFIX_TL = new ThreadLocal<>();

    @Override
    public JbootCacheConfig getConfig() {
        return config;
    }

    @Override
    public JbootCache setThreadCacheNamePrefix(String cacheNamePrefix) {
        if (StrUtil.isNotBlank(cacheNamePrefix)) {
            CACHE_NAME_PREFIX_TL.set(cacheNamePrefix);
        } else {
            CACHE_NAME_PREFIX_TL.remove();
        }
        return this;
    }

    @Override
    public void clearThreadCacheNamePrefix() {
        CACHE_NAME_PREFIX_TL.remove();
    }


    @Override
    public boolean addThreadCacheNamePrefixIngore(String cacheName) {
        return ignoreThreadCacheNames.add(cacheName);
    }

    @Override
    public boolean removeThreadCacheNamePrefixIngore(String cacheName) {
        return ignoreThreadCacheNames.remove(cacheName);
    }


    /**
     * 构建缓存名称
     *
     * @param cacheName
     * @return
     */
    protected String buildCacheName(String cacheName) {

        String cacheNamePrefix = null;

        if (!ignoreThreadCacheNames.contains(cacheName)) {
            cacheNamePrefix = CACHE_NAME_PREFIX_TL.get();
        }

        if (StrUtil.isBlank(cacheNamePrefix)) {
            cacheNamePrefix = config.getDefaultCachePrefix();
        }

        return StrUtil.isNotBlank(cacheNamePrefix) ? (cacheNamePrefix + ":" + cacheName) : cacheName;
    }


    @Override
    public void refresh(String cacheName, Object key) {

    }

    @Override
    public void refresh(String cacheName) {

    }


    protected void println(String debugInfo) {
        JbootCacheManager.me().getPrinter().println(debugInfo);
    }
}
