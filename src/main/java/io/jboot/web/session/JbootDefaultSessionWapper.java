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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;


public class JbootDefaultSessionWapper extends JbootSessionWapperBase implements HttpSession {


    private static Cache<String, Object> sessions = CacheBuilder.newBuilder()
            .expireAfterAccess(40, TimeUnit.MINUTES)
            .build();


    @Override
    public Object getAttribute(String name) {
        String key = buildKey(name);
        return sessions.getIfPresent(key);
    }




    @Override
    public void setAttribute(String name, Object value) {
        String key = buildKey(name);
        sessions.put(key, value);
    }



    @Override
    public void removeAttribute(String name) {
        String key = buildKey(name);
        sessions.invalidate(key);
    }

}
