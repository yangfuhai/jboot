/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.cache.ehredis;

import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;
import io.jboot.cache.JbootCacheBase;
import io.jboot.cache.ehcache.JbootEhcacheImpl;
import io.jboot.cache.redis.JbootRedisCacheImpl;
import io.jboot.mq.JbootmqMessageListener;
import io.jboot.utils.StringUtils;

import java.util.List;

/**
 * 基于 ehcache和redis做的二级缓存
 * 优点是：减少高并发下redis的io瓶颈
 */
public class JbootEhredisCacheImpl extends JbootCacheBase implements JbootmqMessageListener {

    public static final String DEFAULT_NOTIFY_CHANNEL = "jboot_ehredis_channel";

    JbootEhcacheImpl ehcache;
    JbootRedisCacheImpl redisCache;

    private String channel = DEFAULT_NOTIFY_CHANNEL;
    private String uuid;


    public JbootEhredisCacheImpl() {
        Jboot.getMq().addMessageListener(this, channel);
        uuid = StringUtils.uuid();
    }


    @Override
    public List getKeys(String cacheName) {
        List list = ehcache.getKeys(cacheName);
        if (list == null) {
            list = redisCache.getKeys(cacheName);
        }
        return list;
    }

    @Override
    public <T> T get(String cacheName, Object key) {
        T obj = ehcache.get(cacheName, key);
        if (obj == null) {
            obj = redisCache.get(cacheName, key);
        }
        return obj;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        try {
            ehcache.put(cacheName, key, value);
            redisCache.put(cacheName, key, value);
        } finally {
            Jboot.getMq().publish(new JbootEhredisMessage(uuid, JbootEhredisMessage.ACTION_PUT, cacheName, key), channel);
        }
    }

    @Override
    public void remove(String cacheName, Object key) {
        try {
            ehcache.remove(cacheName, key);
            redisCache.remove(cacheName, key);
        } finally {
            Jboot.getMq().publish(new JbootEhredisMessage(uuid, JbootEhredisMessage.ACTION_REMOVE, cacheName, key), channel);
        }
    }

    @Override
    public void removeAll(String cacheName) {
        try {
            ehcache.removeAll(cacheName);
            redisCache.removeAll(cacheName);
        } finally {
            Jboot.getMq().publish(new JbootEhredisMessage(uuid, JbootEhredisMessage.ACTION_REMOVE_ALL, cacheName, null), channel);
        }
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        T obj = get(cacheName, key);
        if (obj != null) {
            return obj;
        }

        obj = (T) dataLoader.load();
        if (obj != null) {
            put(cacheName, key, obj);
        }
        return obj;
    }


    @Override
    public void onMessage(String channel, Object obj) {

        JbootEhredisMessage message = (JbootEhredisMessage) obj;
        /**
         * 不处理自己发送的消息
         */
        if (uuid.equals(message.getId())) {
            return;
        }


        switch (message.getAction()) {
            case JbootEhredisMessage.ACTION_PUT:
                ehcache.remove(message.getCacheName(), message.getKey());
                break;
            case JbootEhredisMessage.ACTION_REMOVE:
                ehcache.remove(message.getCacheName(), message.getKey());
                break;
            case JbootEhredisMessage.ACTION_REMOVE_ALL:
                ehcache.removeAll(message.getCacheName());
                break;
        }

    }
}
