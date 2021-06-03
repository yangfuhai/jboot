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
package io.jboot.test.web;

import io.jboot.web.session.JbootHttpSession;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

public class MockHttpSession extends JbootHttpSession {

    protected static Map<String, Map<String, Object>> storeCache = new HashMap<>();

    public MockHttpSession(String id, ServletContext servletContext) {
        super(id, servletContext, createSessionStore(id), null);
    }

    private static Map<String, Object> createSessionStore(String sessionId) {
        Map<String, Object> store = storeCache.get(sessionId);
        if (store == null) {
            store = new HashMap<>();
            storeCache.put(sessionId, store);
        }
        return store;
    }

}
