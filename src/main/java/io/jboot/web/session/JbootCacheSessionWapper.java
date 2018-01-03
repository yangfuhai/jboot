/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.session;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import io.jboot.Jboot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class JbootCacheSessionWapper extends JbootSessionWapperBase implements HttpSession {


    private static LoadingCache<String, Object> sessions = Caffeine.newBuilder()
            .expireAfterAccess(60, TimeUnit.MINUTES)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, Object>() {
                @Override
                public void onRemoval(@Nullable String key, @Nullable Object value, @Nonnull RemovalCause cause) {
                    Jboot.me().getCache().removeAll("SESSION:" + key);
                }
            })
            .build(key -> new Object());


    private String getSessionCacheName() {
        return "SESSION:" + getOrCreatSessionId();
    }

    @Override
    public Object getAttribute(String name) {
        Object data = Jboot.me().getCache().get(getSessionCacheName(), name);
        if (data != null) {
            refreshCache();
        }
        return data;
    }


    @Override
    public void setAttribute(String name, Object value) {
        refreshCache();
        Jboot.me().getCache().put(getSessionCacheName(), name, value);
    }


    @Override
    public void removeAttribute(String name) {
        refreshCache();
        Jboot.me().getCache().remove(getSessionCacheName(), name);
    }

    /**
     * 刷新缓存，刷新后延长40分钟
     */
    private void refreshCache() {
        sessions.getIfPresent(getOrCreatSessionId());
    }


    @Override
    public Enumeration<String> getAttributeNames() {

        List<String> keys = Jboot.me().getCache().getKeys(getSessionCacheName());
        if (keys == null) {
            keys = new ArrayList<>();
        }

        final Iterator<String> iterator = keys.iterator();
        return new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        };
    }


    @Override
    public String[] getValueNames() {

        List<String> keys = Jboot.me().getCache().getKeys(getSessionCacheName());
        if (keys == null) {
            keys = new ArrayList<>();
        }
        return keys.toArray(new String[]{});
    }


}
