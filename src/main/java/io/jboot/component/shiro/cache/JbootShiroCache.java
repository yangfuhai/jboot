/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.component.shiro.cache;

import io.jboot.Jboot;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;

import java.util.*;

/**
 * 自定义 shiro cache
 *
 * @param <K>
 * @param <V>
 */
public class JbootShiroCache<K, V> implements Cache<K, V> {

    private String cacheName;

    public JbootShiroCache(String cacheName) {
        this.cacheName = "shiroCache:" + cacheName;
    }

    @Override
    public V get(K key) throws CacheException {
        return Jboot.me().getCache().get(cacheName, key);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        Jboot.me().getCache().put(cacheName, key, value);
        return value;
    }

    @Override
    public V remove(K key) throws CacheException {
        V value = Jboot.me().getCache().get(cacheName, key);
        Jboot.me().getCache().remove(cacheName, key);
        return value;
    }

    @Override
    public void clear() throws CacheException {
        Jboot.me().getCache().removeAll(cacheName);
    }

    @Override
    public int size() {
        Set<K> keys = keys();
        return keys == null ? 0 : keys.size();
    }

    @Override
    public Set<K> keys() {
        List list = Jboot.me().getCache().getKeys(cacheName);
        return list == null ? null : new HashSet<K>(list);
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = Collections.emptyList();
        List keys = Jboot.me().getCache().getKeys(cacheName);

        if (!CollectionUtils.isEmpty(keys)) {
            values = new ArrayList<V>(keys.size());
            for (Object key : keys) {
                V value = Jboot.me().getCache().get(cacheName, key);
                if (value != null) {
                    values.add(value);
                }
            }
        }

        return values;
    }

}
