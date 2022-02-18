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
package io.jboot.support.shiro.cache;

import com.google.common.cache.CacheBuilder;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 封装 shiro cache manager
 *
 * 通过 shiro.ini 的进行配置，配置如下 ：
 *
 * shiroCacheManager = io.jboot.support.shiro.cache.JbootShiroCacheManager
 * securityManager.cacheManager = $shiroCacheManager
 */
public class JbootShiroCacheManager implements CacheManager {


    private static final com.google.common.cache.Cache<String, Cache> guavaCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(40, TimeUnit.MINUTES)
            .expireAfterAccess(40, TimeUnit.MINUTES)
            .build();


    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        try {
            return guavaCache.get(name, () -> new JbootShiroCache(name));
        } catch (ExecutionException e) {
            throw new CacheException(e);
        }
    }

}
