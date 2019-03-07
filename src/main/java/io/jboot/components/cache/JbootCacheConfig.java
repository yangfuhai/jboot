/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.cache;


import io.jboot.app.config.annotation.ConfigModel;


@ConfigModel(prefix = "jboot.cache")
public class JbootCacheConfig {

    public static final String TYPE_EHCACHE = "ehcache";
    public static final String TYPE_REDIS = "redis";
    public static final String TYPE_EHREDIS = "ehredis";
    public static final String TYPE_J2CACHE = "j2cache";
    public static final String TYPE_NONE = "none";


    private String type = TYPE_EHCACHE;

    // AOP 缓存的默认有效时间，0为永久有效，单位秒，
    // 当 @Cacheable 和 @CachePut 注解不配置的时候默认用这个配置
    private int aopCacheLiveSeconds = 0;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAopCacheLiveSeconds() {
        return aopCacheLiveSeconds;
    }

    public void setAopCacheLiveSeconds(int aopCacheLiveSeconds) {
        this.aopCacheLiveSeconds = aopCacheLiveSeconds;
    }
}
