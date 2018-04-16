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
package io.jboot.core.cache.j2cache;

import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.core.cache.JbootCache;
import io.jboot.exception.JbootException;
import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.J2Cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.core.cache.j2cache
 */
public class J2cacheImpl implements JbootCache {

    @Override
    public <T> T get(String cacheName, Object key) {
        CacheObject cacheObject = J2Cache.getChannel().get(cacheName, key.toString());
        return cacheObject != null ? (T) cacheObject.getValue() : null;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        J2Cache.getChannel().set(cacheName, key.toString(), value);
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        J2Cache.getChannel().set(cacheName, key.toString(), value, liveSeconds);
    }

    @Override
    public List getKeys(String cacheName) {
        Collection keys = J2Cache.getChannel().keys(cacheName);
        return keys != null ? new ArrayList(keys) : null;
    }

    @Override
    public void remove(String cacheName, Object key) {
        J2Cache.getChannel().evict(cacheName, key.toString());
    }

    @Override
    public void removeAll(String cacheName) {
        J2Cache.getChannel().clear(cacheName);
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        Object value = get(cacheName, key);
        if (value == null) {
            value = dataLoader.load();
            if (value != null) {
                put(cacheName, key, value);
            }
        }
        return (T) value;
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
        return (T) data;
    }

    @Override
    public Integer getTtl(String cacheName, Object key) {
        throw new JbootException("getTtl not support in j2cache");
    }

    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        throw new JbootException("setTtl not support in j2cache");
    }
}
