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
package io.jboot.web.session;

import io.jboot.Jboot;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 若使用ehcache作为缓存，那么
 */
public class JbootCacheSessionWapper extends JbootSessionWapperBase implements HttpSession {

    private static final int CACHE_TIME = 60 * 60 * 2; // 2 hours

    private String getSessionCacheName() {
        return "session:" + getOrCreatSessionId();
    }

    @Override
    public Object getAttribute(String name) {
        return Jboot.me().getCache().get(getSessionCacheName(), name);
    }


    @Override
    public void setAttribute(String name, Object value) {
        Jboot.me().getCache().put(getSessionCacheName(), name, value, CACHE_TIME);
    }


    @Override
    public void removeAttribute(String name) {
        Jboot.me().getCache().remove(getSessionCacheName(), name);
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
