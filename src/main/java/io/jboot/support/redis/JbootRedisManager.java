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
package io.jboot.support.redis;


import io.jboot.Jboot;
import io.jboot.support.redis.jedis.JbootJedisClusterImpl;
import io.jboot.support.redis.jedis.JbootJedisImpl;
import io.jboot.exception.JbootException;

/**
 * 参考： com.jfinal.plugin.redis
 * JbootRedis 命令文档: http://redisdoc.com/
 */
public class JbootRedisManager {

    private static JbootRedisManager manager = new JbootRedisManager();

    private JbootRedisManager() {
    }

    public static JbootRedisManager me() {
        return manager;
    }

    private JbootRedis redis;

    public JbootRedis getRedis() {
        if (redis == null) {
            JbootRedisConfig config = Jboot.config(JbootRedisConfig.class);
            redis = getRedis(config);
        }

        return redis;
    }

    public JbootRedis getRedis(JbootRedisConfig config) {
        if (config == null || !config.isConfigOk()) {
            return null;
        }

        switch (config.getType()) {
            case JbootRedisConfig.TYPE_JEDIS:
                return getJedisClient(config);
            case JbootRedisConfig.TYPE_LETTUCE:
                return getLettuceClient(config);
            case JbootRedisConfig.TYPE_REDISSON:
                return getRedissonClient(config);
        }
        return null;
    }


    private JbootRedis getJedisClient(JbootRedisConfig config) {
        if (config.isCluster()) {
            return new JbootJedisClusterImpl(config);
        } else {
            return new JbootJedisImpl(config);
        }
    }

    private JbootRedis getLettuceClient(JbootRedisConfig config) {
        throw new JbootException("lettuce is not finished.");
    }

    private JbootRedis getRedissonClient(JbootRedisConfig config) {
        throw new JbootException("redisson is not finished.");
    }


}






