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
package io.jboot.cache.redis;

import com.jfinal.plugin.ehcache.IDataLoader;
import com.jfinal.plugin.redis.Redis;
import io.jboot.Jboot;
import io.jboot.cache.JbootCacheBase;
import io.jboot.utils.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.FstCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.ArrayList;
import java.util.List;


public class JbootRedisCacheImpl extends JbootCacheBase {


    RedissonClient redissonClient;

    public JbootRedisCacheImpl() {
        JbootRedisCacheConfig redisConfig = Jboot.config(JbootRedisCacheConfig.class);


        Config redissionConfig = new Config();
        redissionConfig.setCodec(new FstCodec());

        if (redisConfig.isCluster()) {
            ClusterServersConfig clusterServersConfig = redissionConfig.useClusterServers();
            clusterServersConfig.addNodeAddress(redisConfig.getAddress().split(","));
            if (StringUtils.isNotBlank(redisConfig.getPassword())) {
                clusterServersConfig.setPassword(redisConfig.getPassword());
            }
        } else {
            SingleServerConfig singleServerConfig = redissionConfig.useSingleServer();
            singleServerConfig.setAddress(redisConfig.getAddress());
            if (StringUtils.isNotBlank(redisConfig.getPassword())) {
                singleServerConfig.setPassword(redisConfig.getPassword());
            }
        }

        redissonClient = Redisson.create(redissionConfig);

//        redissonClient.get
    }



    @Override
    public <T> T get(String cacheName, Object key) {
        return Redis.use().get(buildKey(cacheName, key));
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        Redis.use().set(buildKey(cacheName, key), value);
    }

    @Override
    public List getKeys(String cacheName) {
        List<String> keys = new ArrayList<String>();
        keys.addAll(Redis.use().keys(cacheName + ":*"));
        for (int i = 0; i < keys.size(); i++) {
            keys.set(i, keys.get(i).substring(cacheName.length() + 3));
        }
        return keys;
    }


    @Override
    public void remove(String cacheName, Object key) {
        Redis.use().del(buildKey(cacheName, key));
    }


    @Override
    public void removeAll(String cacheName) {
        String[] keys = new String[]{};
        keys = Redis.use().keys(cacheName + ":*").toArray(keys);
        Redis.use().del(keys);
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
}
