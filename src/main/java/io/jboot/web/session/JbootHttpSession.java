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
package io.jboot.web.session;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;

public class JbootHttpSession implements HttpSession {

    private final String id;

    private final long createdAt;

    private volatile long lastAccessedAt;

    private int maxInactiveInterval;

    private final ServletContext servletContext;

    private final Map<String, Object> newAttributes = Maps.newHashMap();
    private final Set<String> deleteAttribute = Sets.newHashSet();
    private final Map<String, Object> sessionStore;

    private volatile boolean invalid = false;
    private volatile boolean dataChanged = false;
    private volatile boolean empty = false;

    private volatile HttpSession originSession;

    public JbootHttpSession(String id, ServletContext servletContext, Map<String, Object> sessionStore, HttpSession originSession) {
        this.id = id;
        this.servletContext = servletContext;
        this.sessionStore = sessionStore;
        this.createdAt = System.currentTimeMillis();
        this.lastAccessedAt = createdAt;
        this.empty = sessionStore.isEmpty();
        this.originSession = originSession;
    }



    @Override
    public long getCreationTime() {
        return createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedAt;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    @Deprecated
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        checkValid();
        if (newAttributes.containsKey(name)) {
            return newAttributes.get(name);
        } else if (deleteAttribute.contains(name)) {
            return null;
        }
        return sessionStore.get(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkValid();
        Set<String> names = Sets.newHashSet(sessionStore.keySet());
        names.addAll(newAttributes.keySet());
        names.removeAll(deleteAttribute);
        return Collections.enumeration(names);
    }

    @Override
    public String[] getValueNames() {
        checkValid();
        Set<String> names = Sets.newHashSet(sessionStore.keySet());
        names.addAll(newAttributes.keySet());
        names.removeAll(deleteAttribute);
        return names.toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkValid();
        if (value == null) {
            removeAttribute(name);
            return;
        }

        newAttributes.put(name, value);
        deleteAttribute.remove(name);
        empty = false;
        dataChanged = true;

        if (originSession != null) {
            originSession.setAttribute(name, value);
        }

    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        checkValid();
        if (empty && newAttributes.isEmpty()) {
            return;
        }

        if (!newAttributes.containsKey(name) && !sessionStore.containsKey(name)) {
            return;
        }

        deleteAttribute.add(name);
        newAttributes.remove(name);
        dataChanged = true;

        if (originSession != null) {
            originSession.removeAttribute(name);
        }
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        invalid = true;
        dataChanged = true;

        if (originSession != null) {
            originSession.invalidate();
        }
    }

    @Override
    public boolean isNew() {
        return Boolean.TRUE;
    }


    public boolean isDataChanged() {
        return dataChanged;
    }


    public Map<String, Object> snapshot() {
        Map<String, Object> snap = new HashMap<>();
        snap.putAll(sessionStore);
        snap.putAll(newAttributes);
        for (String name : deleteAttribute) {
            snap.remove(name);
        }
        return snap;
    }

    public boolean isValid() {
        return !invalid;
    }

    protected void checkValid() throws IllegalStateException {
        if (invalid) {
            throw new IllegalStateException("http session has invalidate");
        }
    }

    public boolean isEmpty() {
        return empty;
    }
}
