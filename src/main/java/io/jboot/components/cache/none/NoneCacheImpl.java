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
package io.jboot.components.cache.none;


import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheConfig;

import java.util.List;

/**
 * noneCache 存在的目的：方便通过配置文件的方式关闭缓存功能
 */
public class NoneCacheImpl implements JbootCache {

    private JbootCacheConfig config;

    public NoneCacheImpl(JbootCacheConfig config) {
        this.config = config;
    }

    @Override
    public JbootCache setThreadCacheNamePrefix(String cacheNamePrefix) {
        return this;
    }

    @Override
    public void clearThreadCacheNamePrefix() {

    }

    @Override
    public boolean addThreadCacheNamePrefixIngore(String cacheName) {
        return true;
    }

    @Override
    public boolean removeThreadCacheNamePrefixIngore(String cacheName) {
        return true;
    }

    @Override
    public JbootCacheConfig getConfig() {
        return config;
    }

    @Override
    public <T> T get(String cacheName, Object key) {
        return null;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        //do nothing
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        //do nothing
    }

    @Override
    public void remove(String cacheName, Object key) {
        //do nothing
    }

    @Override
    public void removeAll(String cacheName) {
        //do nothing
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        return (T) dataLoader.load();
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
        return (T) dataLoader.load();
    }

    @Override
    public Integer getTtl(String cacheName, Object key) {
        return null;
    }

    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        //do nothing
    }

    @Override
    public void refresh(String cacheName, Object key) {

    }

    @Override
    public void refresh(String cacheName) {

    }

    @Override
    public List getNames() {
        return null;
    }

    @Override
    public List getKeys(String cacheName) {
        return null;
    }
}
