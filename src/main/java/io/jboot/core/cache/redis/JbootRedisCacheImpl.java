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
package io.jboot.core.cache.redis;

import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;
import io.jboot.support.redis.JbootRedisManager;
import io.jboot.core.cache.JbootCacheBase;
import io.jboot.exception.JbootIllegalConfigException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class JbootRedisCacheImpl extends JbootCacheBase {


    private JbootRedis redis;

    public JbootRedisCacheImpl() {
        JbootRedisCacheConfig redisConfig = Jboot.config(JbootRedisCacheConfig.class);
        if (redisConfig.isConfigOk()) {
            redis = JbootRedisManager.me().getRedis(redisConfig);
        } else {
            redis = Jboot.me().getRedis();
        }

        if (redis == null) {
            throw new JbootIllegalConfigException("can not get redis, please check your jboot.properties , please correct config jboot.cache.redis.host or jboot.redis.host ");
        }
    }


    @Override
    public <T> T get(String cacheName, Object key) {
        return redis.get(buildKey(cacheName, key));
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        if (value == null) {
            // if value is null : java.lang.NullPointerException: null at redis.clients.jedis.Protocol.sendCommand(Protocol.java:99)
            return;
        }
        redis.set(buildKey(cacheName, key), value);
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        if (value == null) {
            // if value is null : java.lang.NullPointerException: null at redis.clients.jedis.Protocol.sendCommand(Protocol.java:99)
            return;
        }
        if (liveSeconds <= 0) {
            put(cacheName, key, value);
            return;
        }

        redis.setex(buildKey(cacheName, key), liveSeconds, value);
    }

    @Override
    public List getKeys(String cacheName) {
        Set<String> keyset = redis.keys(cacheName + ":*");
        if (keyset == null || keyset.size() == 0) {
            return null;
        }
        List<String> keys = new ArrayList<>(keyset);
        for (int i = 0; i < keys.size(); i++) {
            keys.set(i, keys.get(i).substring(cacheName.length() + 3));
        }
        return keys;
    }


    @Override
    public void remove(String cacheName, Object key) {
        redis.del(buildKey(cacheName, key));
    }


    @Override
    public void removeAll(String cacheName) {
        String[] keys = new String[]{};
        keys = redis.keys(cacheName + ":*").toArray(keys);
        if (keys != null && keys.length > 0) {
            redis.del(keys);
        }
    }


    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        Object data = get(cacheName, key);
        if (data == null) {
            data = dataLoader.load();
            put(cacheName, key, data);
        }
        return (T) data;
    }


    private String buildKey(String cacheName, Object key) {
        if (key instanceof Number)
            return String.format("%s:I:%s", cacheName, key);
        else {
            Class keyClass = key.getClass();
            if (String.class.equals(keyClass) ||
                    StringBuffer.class.equals(keyClass) ||
                    StringBuilder.class.equals(keyClass)) {
                return String.format("%s:S:%s", cacheName, key);
            }
        }
        return String.format("%s:O:%s", cacheName, key);
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
        Long ttl = redis.ttl(buildKey(cacheName, key));
        return ttl != null ? ttl.intValue() : null;
    }

    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        redis.expire(buildKey(cacheName, key), seconds);
    }

    public JbootRedis getRedis() {
        return redis;
    }

}
