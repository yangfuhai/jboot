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
package io.jboot.components.cache.caredis;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCacheBase;
import io.jboot.components.cache.JbootCacheConfig;
import io.jboot.components.cache.caffeine.CaffeineCacheImpl;
import io.jboot.components.cache.redis.JbootRedisCacheImpl;
import io.jboot.components.serializer.JbootSerializer;
import io.jboot.support.redis.JbootRedis;
import io.jboot.utils.StrUtil;
import redis.clients.jedis.BinaryJedisPubSub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于 caffeine和redis做的二级缓存
 * 优点是：减少高并发下redis的io瓶颈
 */
public class JbootCaredisCacheImpl extends JbootCacheBase {

    public static final String DEFAULT_NOTIFY_CHANNEL = "jboot_caredis_channel";

    private CaffeineCacheImpl caffeineCacheImpl;
    private JbootRedisCacheImpl redisCacheImpl;
    private JbootRedis redis;
    private JbootSerializer serializer;

    private String channel = DEFAULT_NOTIFY_CHANNEL;
    private String clientId;

    private Cache<String, List> keysCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();


    public JbootCaredisCacheImpl(JbootCacheConfig config) {
        super(config);
        this.caffeineCacheImpl = new CaffeineCacheImpl(config);
        this.redisCacheImpl = new JbootRedisCacheImpl(config);
        this.clientId = StrUtil.uuid();
        this.serializer = Jboot.getSerializer();

        //在某些场景下，多个应用使用同一个 redis 实例，此时可以通过配置 cacheSyncMqChannel 来解决缓存冲突的问题
        if (StrUtil.isNotBlank(config.getCacheSyncMqChannel())){
            this.channel = config.getCacheSyncMqChannel();
        }

        this.redis = redisCacheImpl.getRedis();
        this.redis.subscribe(new BinaryJedisPubSub() {
            @Override
            public void onMessage(byte[] channel, byte[] message) {
                JbootCaredisCacheImpl.this.onMessage((String) serializer.deserialize(channel), serializer.deserialize(message));
            }
        }, serializer.serialize(channel));
    }


    @Override
    public <T> T get(String cacheName, Object key) {
        T value = caffeineCacheImpl.get(cacheName, key);
        if (value == null && !config.isUseFirstLevelOnly()) {
            value = redisCacheImpl.get(cacheName, key);
            if (value != null) {
                Integer ttl = redisCacheImpl.getTtl(cacheName, key);
                if (ttl != null && ttl > 0) {
                    caffeineCacheImpl.put(cacheName, key, value, ttl);
                } else {
                    caffeineCacheImpl.put(cacheName, key, value);
                }
            }
        }
        return value;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        try {
            caffeineCacheImpl.put(cacheName, key, value);
            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.put(cacheName, key, value);
            }
        } finally {
            publishMessage(JbootCaredisMessage.ACTION_PUT, cacheName, key);
        }
    }


    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        if (liveSeconds <= 0) {
            put(cacheName, key, value);
            return;
        }
        try {
            caffeineCacheImpl.put(cacheName, key, value, liveSeconds);
            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.put(cacheName, key, value, liveSeconds);
            }
        } finally {
            publishMessage(JbootCaredisMessage.ACTION_PUT, cacheName, key);
        }
    }

    @Override
    public void remove(String cacheName, Object key) {
        try {
            caffeineCacheImpl.remove(cacheName, key);
            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.remove(cacheName, key);
            }
        } finally {
            publishMessage(JbootCaredisMessage.ACTION_REMOVE, cacheName, key);
        }
    }

    @Override
    public void removeAll(String cacheName) {
        try {
            caffeineCacheImpl.removeAll(cacheName);
            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.removeAll(cacheName);
            }
        } finally {
            publishMessage(JbootCaredisMessage.ACTION_REMOVE_ALL, cacheName, null);
        }
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        T value = get(cacheName, key);
        if (value != null) {
            return value;
        }

        value = (T) dataLoader.load();
        if (value != null) {
            put(cacheName, key, value);
        }
        return value;
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
        if (liveSeconds <= 0) {
            return get(cacheName, key, dataLoader);
        }

        T value = get(cacheName, key);
        if (value != null) {
            return value;
        }

        value = (T) dataLoader.load();
        if (value != null) {
            put(cacheName, key, value, liveSeconds);
        }
        return value;
    }

    @Override
    public Integer getTtl(String cacheName, Object key) {
        Integer ttl = caffeineCacheImpl.getTtl(cacheName, key);
        if (ttl == null && !config.isUseFirstLevelOnly()) {
            ttl = redisCacheImpl.getTtl(cacheName, key);
        }
        return ttl;
    }


    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        try {
            caffeineCacheImpl.setTtl(cacheName, key, seconds);

            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.setTtl(cacheName, key, seconds);
            }
        } finally {
            publishMessage(JbootCaredisMessage.ACTION_REMOVE, cacheName, key);
        }
    }


    @Override
    public void refresh(String cacheName, Object key) {
        publishMessage(JbootCaredisMessage.ACTION_REMOVE, cacheName, key);
    }


    @Override
    public void refresh(String cacheName) {
        publishMessage(JbootCaredisMessage.ACTION_REMOVE_ALL, cacheName, null);
    }


    @Override
    public List getNames() {
        return config.isUseFirstLevelOnly() ? null : redisCacheImpl.getNames();
    }


    @Override
    public List getKeys(String cacheName) {
        List list = keysCache.getIfPresent(cacheName);
        if (list != null) {
            return list;
        }

        if (!config.isUseFirstLevelOnly()) {
            list = redisCacheImpl.getKeys(cacheName);
            if (list == null) {
                list = new ArrayList();
            }
            keysCache.put(cacheName, list);
        }

        return list;
    }


    private void publishMessage(int action, String cacheName, Object key) {
        clearKeysCache(cacheName);
        JbootCaredisMessage message = new JbootCaredisMessage(clientId, action, cacheName, key);
        redis.publish(serializer.serialize(channel), serializer.serialize(message));
    }

    private void clearKeysCache(String cacheName) {
        keysCache.invalidate(cacheName);
    }

    public void onMessage(String channel, Object obj) {

        JbootCaredisMessage message = (JbootCaredisMessage) obj;

        //不处理自己发送的消息
        if (clientId.equals(message.getClientId())) {
            return;
        }

        clearKeysCache(message.getCacheName());

        switch (message.getAction()) {
            case JbootCaredisMessage.ACTION_PUT:
            case JbootCaredisMessage.ACTION_REMOVE:
                caffeineCacheImpl.remove(message.getCacheName(), message.getKey());
                break;
            case JbootCaredisMessage.ACTION_REMOVE_ALL:
                caffeineCacheImpl.removeAll(message.getCacheName());
                break;
        }
    }


    public CaffeineCacheImpl getCaffeineCacheImpl() {
        return caffeineCacheImpl;
    }

    public JbootRedisCacheImpl getRedisCacheImpl() {
        return redisCacheImpl;
    }

}
