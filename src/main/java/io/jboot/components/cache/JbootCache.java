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

import com.jfinal.plugin.ehcache.IDataLoader;

import java.util.List;


public interface JbootCache extends com.jfinal.plugin.activerecord.cache.ICache {

    JbootCache setCurrentCacheNamePrefix(String cacheNamePrefix);

    void removeCurrentCacheNamePrefix();

    JbootCacheConfig getConfig();

    @Override
    <T> T get(String cacheName, Object key);

    @Override
    void put(String cacheName, Object key, Object value);

    void put(String cacheName, Object key, Object value, int liveSeconds);


    @Override
    void remove(String cacheName, Object key);

    @Override
    void removeAll(String cacheName);

    <T> T get(String cacheName, Object key, IDataLoader dataLoader);

    <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds);

    Integer getTtl(String cacheName, Object key);

    void setTtl(String cacheName, Object key, int seconds);

    void refresh(String cacheName, Object key);

    void refresh(String cacheName);


    List getNames();

    List getKeys(String cacheName);

}
