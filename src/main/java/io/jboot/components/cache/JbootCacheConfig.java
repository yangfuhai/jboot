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
package io.jboot.components.cache;


import com.google.common.collect.Sets;
import io.jboot.Jboot;
import io.jboot.app.config.annotation.ConfigModel;

import java.util.Set;


@ConfigModel(prefix = "jboot.cache")
public class JbootCacheConfig {

    public static final String TYPE_EHCACHE = "ehcache";
    public static final String TYPE_REDIS = "redis";
    public static final String TYPE_EHREDIS = "ehredis";
    public static final String TYPE_J2CACHE = "j2cache";
    public static final String TYPE_CAFFEINE = "caffeine";
    public static final String TYPE_CAREDIS = "caredis";
    public static final String TYPE_NONE = "none";

    public static final Set<String> TYPES = Sets.newHashSet(TYPE_EHCACHE, TYPE_REDIS, TYPE_EHREDIS
            , TYPE_J2CACHE, TYPE_CAFFEINE, TYPE_CAREDIS, TYPE_NONE);

    private String name = "default";
    private String type = TYPE_CAFFEINE;
    private String typeName;

    private String defaultCachePrefix;
    private Boolean devMode = false;

    //只启用一级缓存，针对分布式场景下，redis 有关闭了 scan keys 等指令的时候
    //可以使用 redis 的消息机制做缓存同步
    private boolean useFirstLevelOnly = false;

    private String cacheSyncMqChannel;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDefaultCachePrefix() {
        return defaultCachePrefix;
    }

    public void setDefaultCachePrefix(String defaultCachePrefix) {
        this.defaultCachePrefix = defaultCachePrefix;
    }

    public boolean isDevMode() {
        return devMode == null ? Jboot.isDevMode() : devMode;
    }

    public void setDevMode(Boolean devMode) {
        this.devMode = devMode;
    }

    public boolean isUseFirstLevelOnly() {
        return useFirstLevelOnly;
    }

    public void setUseFirstLevelOnly(boolean useFirstLevelOnly) {
        this.useFirstLevelOnly = useFirstLevelOnly;
    }

    public String getCacheSyncMqChannel() {
        return cacheSyncMqChannel;
    }

    public void setCacheSyncMqChannel(String cacheSyncMqChannel) {
        this.cacheSyncMqChannel = cacheSyncMqChannel;
    }
}
