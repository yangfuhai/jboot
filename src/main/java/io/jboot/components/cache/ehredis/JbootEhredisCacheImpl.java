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
package io.jboot.components.cache.ehredis;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCacheBase;
import io.jboot.components.cache.JbootCacheConfig;
import io.jboot.components.cache.ehcache.JbootEhcacheImpl;
import io.jboot.components.cache.redis.JbootRedisCacheImpl;
import io.jboot.components.serializer.JbootSerializer;
import io.jboot.support.redis.JbootRedis;
import io.jboot.utils.StrUtil;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import redis.clients.jedis.BinaryJedisPubSub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于 ehcache和redis做的二级缓存
 * 优点是：减少高并发下redis的io瓶颈
 */
public class JbootEhredisCacheImpl extends JbootCacheBase implements CacheEventListener {

    public static final String DEFAULT_NOTIFY_CHANNEL = "jboot_ehredis_channel";

    private JbootEhcacheImpl ehcacheImpl;
    private JbootRedisCacheImpl redisCacheImpl;
    private JbootRedis redis;
    private JbootSerializer serializer;

    private String channel = DEFAULT_NOTIFY_CHANNEL;
    private String clientId;

    private LoadingCache<String, List> keysCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(key -> null);


    public JbootEhredisCacheImpl(JbootCacheConfig config) {
        super(config);
        this.ehcacheImpl = new JbootEhcacheImpl(config);
        this.ehcacheImpl.setCacheEventListener(this);

        this.redisCacheImpl = new JbootRedisCacheImpl(config);
        this.clientId = StrUtil.uuid();
        this.serializer = Jboot.getSerializer();

        if (StrUtil.isNotBlank(config.getCacheSyncMqChannel())){
            this.channel = config.getCacheSyncMqChannel();
        }

        this.redis = redisCacheImpl.getRedis();
        this.redis.subscribe(new BinaryJedisPubSub() {
            @Override
            public void onMessage(byte[] channel, byte[] message) {
                JbootEhredisCacheImpl.this.onMessage((String) serializer.deserialize(channel), serializer.deserialize(message));
            }
        }, serializer.serialize(channel));
    }


    @Override
    public <T> T get(String cacheName, Object key) {
        T value = ehcacheImpl.get(cacheName, key);
        if (value == null && !config.isUseFirstLevelOnly()) {
            value = redisCacheImpl.get(cacheName, key);
            if (value != null) {
                Integer ttl = redisCacheImpl.getTtl(cacheName, key);
                if (ttl != null && ttl > 0) {
                    ehcacheImpl.put(cacheName, key, value, ttl);
                } else {
                    ehcacheImpl.put(cacheName, key, value);
                }
            }
        }
        return value;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        try {
            ehcacheImpl.put(cacheName, key, value);
            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.put(cacheName, key, value);
            }
        } finally {
            publishMessage(JbootEhredisMessage.ACTION_PUT, cacheName, key);
        }
    }


    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        if (liveSeconds <= 0) {
            put(cacheName, key, value);
            return;
        }
        try {
            ehcacheImpl.put(cacheName, key, value, liveSeconds);

            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.put(cacheName, key, value, liveSeconds);
            }
        } finally {
            publishMessage(JbootEhredisMessage.ACTION_PUT, cacheName, key);
        }
    }

    @Override
    public void remove(String cacheName, Object key) {
        try {
            ehcacheImpl.remove(cacheName, key);

            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.remove(cacheName, key);
            }
        } finally {
            publishMessage(JbootEhredisMessage.ACTION_REMOVE, cacheName, key);
        }
    }

    @Override
    public void removeAll(String cacheName) {
        try {
            ehcacheImpl.removeAll(cacheName);

            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.removeAll(cacheName);
            }
        } finally {
            publishMessage(JbootEhredisMessage.ACTION_REMOVE_ALL, cacheName, null);
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
        Integer ttl = ehcacheImpl.getTtl(cacheName, key);
        if (ttl == null && !config.isUseFirstLevelOnly()) {
            ttl = redisCacheImpl.getTtl(cacheName, key);
        }
        return ttl;
    }


    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        try {
            ehcacheImpl.setTtl(cacheName, key, seconds);

            if (!config.isUseFirstLevelOnly()) {
                redisCacheImpl.setTtl(cacheName, key, seconds);
            }
        } finally {
            publishMessage(JbootEhredisMessage.ACTION_REMOVE, cacheName, key);
        }
    }


    private void publishMessage(int action, String cacheName, Object key) {
        clearKeysCache(cacheName);
        JbootEhredisMessage message = new JbootEhredisMessage(clientId, action, cacheName, key);
        redis.publish(serializer.serialize(channel), serializer.serialize(message));
    }

    private void clearKeysCache(String cacheName) {
        keysCache.invalidate(cacheName);
    }


    @Override
    public void refresh(String cacheName, Object key) {
        publishMessage(JbootEhredisMessage.ACTION_REMOVE, cacheName, key);
    }


    @Override
    public void refresh(String cacheName) {
        publishMessage(JbootEhredisMessage.ACTION_REMOVE_ALL, cacheName, null);
    }

    @Override
    public List getNames() {
        return config.isUseFirstLevelOnly() ? null : redisCacheImpl.getNames();
    }

    @Override
    public List getKeys(String cacheName) {
        List list = keysCache.getIfPresent(cacheName);
        if (list == null && !config.isUseFirstLevelOnly()) {
            list = redisCacheImpl.getKeys(cacheName);
            if (list == null) {
                list = new ArrayList();
            }
            keysCache.put(cacheName, list);
        }
        return list;
    }


    public void onMessage(String channel, Object obj) {

        JbootEhredisMessage message = (JbootEhredisMessage) obj;

        //不处理自己发送的消息
        if (clientId.equals(message.getClientId())) {
            return;
        }

        clearKeysCache(message.getCacheName());

        switch (message.getAction()) {
            case JbootEhredisMessage.ACTION_PUT:
            case JbootEhredisMessage.ACTION_REMOVE:
                ehcacheImpl.remove(message.getCacheName(), message.getKey());
                break;
            case JbootEhredisMessage.ACTION_REMOVE_ALL:
                ehcacheImpl.removeAll(message.getCacheName());
                break;
        }
    }

    public JbootEhcacheImpl getEhcacheImpl() {
        return ehcacheImpl;
    }

    public JbootRedisCacheImpl getRedisCacheImpl() {
        return redisCacheImpl;
    }


    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
    }

    @Override
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
    }

    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
    }

    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
        clearKeysCache(cache.getName());
    }

    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public void dispose() {
    }
}
