/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.cache.ehredis;

import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;
import io.jboot.component.redis.JbootRedis;
import io.jboot.core.cache.JbootCacheBase;
import io.jboot.core.cache.ehcache.JbootEhcacheImpl;
import io.jboot.core.cache.redis.JbootRedisCacheImpl;
import io.jboot.core.serializer.ISerializer;
import io.jboot.utils.StringUtils;
import redis.clients.jedis.BinaryJedisPubSub;

import java.util.List;

/**
 * 基于 ehcache和redis做的二级缓存
 * 优点是：减少高并发下redis的io瓶颈
 */
public class JbootEhredisCacheImpl extends JbootCacheBase {

    public static final String DEFAULT_NOTIFY_CHANNEL = "jboot_ehredis_channel";

    private JbootEhcacheImpl ehcacheImpl;
    private JbootRedisCacheImpl redisCacheImpl;
    private JbootRedis redis;
    private ISerializer serializer;

    private String channel = DEFAULT_NOTIFY_CHANNEL;
    private String clientId;


    public JbootEhredisCacheImpl() {
        this.ehcacheImpl = new JbootEhcacheImpl();
        this.redisCacheImpl = new JbootRedisCacheImpl();
        this.clientId = StringUtils.uuid();
        this.serializer = Jboot.me().getSerializer();

        this.redis = redisCacheImpl.getRedis();
        this.redis.subscribe(new BinaryJedisPubSub() {
            @Override
            public void onMessage(byte[] channel, byte[] message) {
                JbootEhredisCacheImpl.this.onMessage((String) serializer.deserialize(channel), serializer.deserialize(message));
            }
        }, serializer.serialize(channel));
    }


    @Override
    public List getKeys(String cacheName) {
        List list = ehcacheImpl.getKeys(cacheName);
        if (list == null || list.isEmpty()) {
            list = redisCacheImpl.getKeys(cacheName);
        }
        return list;
    }

    @Override
    public <T> T get(String cacheName, Object key) {
        T value = ehcacheImpl.get(cacheName, key);
        if (value == null) {
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
            redisCacheImpl.put(cacheName, key, value);
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
            redisCacheImpl.put(cacheName, key, value, liveSeconds);
        } finally {
            publishMessage(JbootEhredisMessage.ACTION_PUT, cacheName, key);
        }
    }

    @Override
    public void remove(String cacheName, Object key) {
        try {
            ehcacheImpl.remove(cacheName, key);
            redisCacheImpl.remove(cacheName, key);
        } finally {
            publishMessage(JbootEhredisMessage.ACTION_REMOVE, cacheName, key);
        }
    }

    @Override
    public void removeAll(String cacheName) {
        try {
            ehcacheImpl.removeAll(cacheName);
            redisCacheImpl.removeAll(cacheName);
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
        if (ttl == null) {
            ttl = redisCacheImpl.getTtl(cacheName, key);
        }
        return ttl;
    }


    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        ehcacheImpl.setTtl(cacheName, key, seconds);
        redisCacheImpl.setTtl(cacheName, key, seconds);
    }


    private void publishMessage(int action, String cacheName, Object key) {
        JbootEhredisMessage message = new JbootEhredisMessage(clientId, action, cacheName, key);
        redis.publish(serializer.serialize(channel), serializer.serialize(message));
    }

    public void onMessage(String channel, Object obj) {

        JbootEhredisMessage message = (JbootEhredisMessage) obj;
        /**
         * 不处理自己发送的消息
         */
        if (clientId.equals(message.getClientId())) {
            return;
        }


        switch (message.getAction()) {
            case JbootEhredisMessage.ACTION_PUT:
                ehcacheImpl.remove(message.getCacheName(), message.getKey());
                break;
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

}
