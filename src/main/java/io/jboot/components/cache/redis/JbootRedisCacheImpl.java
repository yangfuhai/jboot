/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.cache.redis;

import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;
import io.jboot.support.redis.JbootRedisManager;
import io.jboot.components.cache.JbootCacheBase;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.support.redis.RedisScanResult;
import io.jboot.utils.StrUtil;

import java.util.*;


public class JbootRedisCacheImpl extends JbootCacheBase {


    private JbootRedis redis;
    private JbootRedisCacheConfig cacheConfig;
    private static String redisCacheNamesKey = "jboot_cache_names";
    private String globalKeyPrefix = "";


    public JbootRedisCacheImpl() {
        cacheConfig = Jboot.config(JbootRedisCacheConfig.class);
        if (StrUtil.isNotBlank(cacheConfig.getGlobalKeyPrefix())) {
            globalKeyPrefix = cacheConfig.getGlobalKeyPrefix() + ":";
            redisCacheNamesKey = globalKeyPrefix + redisCacheNamesKey;
        }

        if (cacheConfig.isConfigOk()) {
            redis = JbootRedisManager.me().getRedis(cacheConfig);
        } else {
            redis = Jboot.getRedis();
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
        redis.sadd(redisCacheNamesKey, cacheName);
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
        redis.sadd(redisCacheNamesKey, cacheName);
    }


    @Override
    public void remove(String cacheName, Object key) {
        redis.del(buildKey(cacheName, key));
    }


    @Override
    public void removeAll(String cacheName) {
        String cursor = "0";
        int scanCount = 1000;
        boolean continueState = true;
        do {
            RedisScanResult<String> redisScanResult = redis.scan(cacheName + ":*", cursor, scanCount);
            List<String> scanKeys = redisScanResult.getResults();
            cursor = redisScanResult.getCursor();

            if (scanKeys != null && scanKeys.size() > 0) {
                redis.del(scanKeys.toArray(new String[scanKeys.size()]));
            }

            if (redisScanResult.isCompleteIteration()) {
                continueState = false;
            }
        } while (continueState);

        redis.srem(redisCacheNamesKey, cacheName);
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
        StringBuilder keyBuilder = new StringBuilder(globalKeyPrefix)
                .append(cacheName).append(":");

        if (key instanceof String) {
            keyBuilder.append("S");
        } else if (key instanceof Number) {
            keyBuilder.append("I");
        } else if (key == null) {
            keyBuilder.append("S");
            key = "null";
        } else {
            keyBuilder.append("O");
        }
        return keyBuilder.append(":").append(key).toString();
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


    @Override
    public List getNames() {
        Set set = redis.smembers(redisCacheNamesKey);
        return set == null ? null : new ArrayList(set);
    }


    @Override
    public List getKeys(String cacheName) {
        List<String> keys = new ArrayList<>();
        String cursor = "0";
        int scanCount = 1000;
        boolean continueState = true;
        do {
            RedisScanResult<String> redisScanResult = redis.scan(cacheName + ":*", cursor, scanCount);
            List<String> scanKeys = redisScanResult.getResults();
            cursor = redisScanResult.getCursor();

            if (scanKeys != null && scanKeys.size() > 0) {
                for (String key : scanKeys) {
                    keys.add(key.substring(cacheName.length() + 3));
                }
            }

            if (redisScanResult.isCompleteIteration()) {
                continueState = false;
            }
        } while (continueState);

        return keys;
    }

    public JbootRedis getRedis() {
        return redis;
    }

}
