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
package io.jboot.components.cache.redis;


import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.support.redis.JbootRedisConfig;

/**
 * JbootRedis 缓存的配置文件
 */
@ConfigModel(prefix = "jboot.cache.redis")
public class JbootRedisCacheConfig extends JbootRedisConfig {

    /**
     * 全局的key前缀，所有缓存的key都会自动添加该前缀
     */
    private String globalKeyPrefix;

    public String getGlobalKeyPrefix() {
        return globalKeyPrefix;
    }

    public void setGlobalKeyPrefix(String globalKeyPrefix) {
        this.globalKeyPrefix = globalKeyPrefix;
    }
}
