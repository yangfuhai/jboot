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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package io.jboot.web.session
 */
public class JbootHttpSession implements HttpSession {

    /**
     * session id
     */
    private final String id;

    /**
     * session created time
     */
    private final long createdAt;

    /**
     * session last access time
     */
    private volatile long lastAccessedAt;

    /**
     * session max active
     */
    private int maxInactiveInterval;

    private final ServletContext servletContext;

    /**
     * the new attributes of the current request
     */
    private final Map<String, Object> newAttributes = Maps.newHashMap();

    /**
     * the deleted attributes of the current request
     */
    private final Set<String> deleteAttribute = Sets.newHashSet();

    /**
     * session attributes store
     */
    private final Map<String, Object> sessionStore;

    /**
     * true if session invoke invalidate()
     */
    private volatile boolean invalid;

    /**
     * true if session attrs updated
     */
    private volatile boolean dirty;

    public JbootHttpSession(String id, ServletContext servletContext, Map<String, Object> sessionStore) {
        this.id = id;
        this.servletContext = servletContext;
        this.createdAt = System.currentTimeMillis();
        this.lastAccessedAt = createdAt;
        this.sessionStore = sessionStore;
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
        if (value != null) {
            newAttributes.put(name, value);
            deleteAttribute.remove(name);
        } else {
            deleteAttribute.add(name);
            newAttributes.remove(name);
        }
        dirty = true;
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        checkValid();
        deleteAttribute.add(name);
        newAttributes.remove(name);
        dirty = true;
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
        dirty = true;
    }

    @Override
    public void invalidate() {
        invalid = true;
        dirty = true;
    }

    public boolean isNew() {
        return Boolean.TRUE;
    }


    public boolean isDirty() {
        return dirty;
    }

    /**
     * get session attributes' snapshot
     *
     * @return session attributes' map object
     */
    public Map<String, Object> snapshot() {
        Map<String, Object> snap = Maps.newHashMap();
        snap.putAll(sessionStore);
        snap.putAll(newAttributes);
        for (String name : deleteAttribute) {
            snap.remove(name);
        }
        return snap;
    }

    /**
     * the session is valid or not
     *
     * @return return true if the session is valid, or false
     */
    public boolean isValid() {
        return !invalid;
    }

    protected void checkValid() throws IllegalStateException {
        if (invalid) {
            throw new IllegalStateException("http session has invalidate");
        }
    }
}
