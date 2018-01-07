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

import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.core.cache.JbootCache;
import io.jboot.exception.JbootException;
import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.J2Cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.core.cache.j2cache
 */
public class J2cacheImpl implements JbootCache {

    private static final Log LOG = Log.getLog(J2cacheImpl.class);

    @Override
    public <T> T get(String cacheName, Object key) {
        try {
            CacheObject cacheObject = J2Cache.getChannel().getObject(cacheName, key.toString());
            return cacheObject != null ? (T) cacheObject.getValue() : null;
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }
        return null;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        try {
            J2Cache.getChannel().set(cacheName, key.toString(), (Serializable) value);
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        try {
            J2Cache.getChannel().set(cacheName, key.toString(), (Serializable) value, liveSeconds);
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }
    }

    @Override
    public List getKeys(String cacheName) {
        try {
            Collection keys = J2Cache.getChannel().keys(cacheName);
            return keys != null ? new ArrayList(keys) : null;
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }
        return null;
    }

    @Override
    public void remove(String cacheName, Object key) {
        try {
            J2Cache.getChannel().exists(cacheName, key.toString());
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }
    }

    @Override
    public void removeAll(String cacheName) {
        try {
            J2Cache.getChannel().clear(cacheName);
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }
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
        throw new JbootException("not support in j2cache");
    }

    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        throw new JbootException("not support in j2cache");
    }
}
