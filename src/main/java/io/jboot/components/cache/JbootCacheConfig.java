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
package io.jboot.components.cache;


import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.utils.StrUtil;


@ConfigModel(prefix = "jboot.cache")
public class JbootCacheConfig {

    public static final String TYPE_EHCACHE = "ehcache";
    public static final String TYPE_REDIS = "redis";
    public static final String TYPE_EHREDIS = "ehredis";
    public static final String TYPE_J2CACHE = "j2cache";
    public static final String TYPE_CAFFEINE = "caffeine";
    public static final String TYPE_CAREDIS = "caredis";
    public static final String TYPE_NONE = "none";


    private String type = TYPE_CAFFEINE;
    private String defaultCacheNamePrefix;

    // AOP 缓存的默认有效时间，0为永久有效，单位秒，
    // 当 @Cacheable 和 @CachePut 注解不配置的时候默认用这个配置
    private int aopCacheLiveSeconds = 0;
    private String aopCacheType;
    private String aopCacheDefaultCacheNamePrefix;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultCacheNamePrefix() {
        return defaultCacheNamePrefix;
    }

    public void setDefaultCacheNamePrefix(String defaultCacheNamePrefix) {
        this.defaultCacheNamePrefix = defaultCacheNamePrefix;
    }

    public int getAopCacheLiveSeconds() {
        return aopCacheLiveSeconds;
    }

    public void setAopCacheLiveSeconds(int aopCacheLiveSeconds) {
        this.aopCacheLiveSeconds = aopCacheLiveSeconds;
    }

    public String getAopCacheType() {
        if (StrUtil.isBlank(aopCacheType)){
            aopCacheType = getType();
        }
        return aopCacheType;
    }

    public void setAopCacheType(String aopCacheType) {
        this.aopCacheType = aopCacheType;
    }

    private static JbootCacheConfig me;

    public static JbootCacheConfig getInstance() {
        if (me == null) {
            me = JbootConfigManager.me().get(JbootCacheConfig.class);
        }
        return me;
    }

}
