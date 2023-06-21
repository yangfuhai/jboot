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
package io.jboot.components.event;

import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.core.weight.WeightUtil;
import io.jboot.utils.*;
import io.jboot.components.event.annotation.EventConfig;

import java.util.*;
import java.util.concurrent.*;


public class JbootEventManager {

    private static final Log LOG = Log.getLog(JbootEventManager.class);
    private static JbootEventManager manager = new JbootEventManager();

    private final Map<String, List<JbootEventListener>> asyncListenerMap;
    private final Map<String, List<JbootEventListener>> listenerMap;


    private ExecutorService threadPool;


    private JbootEventManager() {
        asyncListenerMap = new ConcurrentHashMap<>();
        listenerMap = new ConcurrentHashMap<>();
        threadPool = NamedThreadPools.newFixedThreadPool("jboot-event");

        initListeners();
    }

    public static JbootEventManager me() {
        return manager;
    }

    private void initListeners() {
        List<Class<JbootEventListener>> classes = ClassScanner.scanSubClass(JbootEventListener.class, true);
        if (ArrayUtil.isNullOrEmpty(classes)) {
            return;
        }
        for (Class<JbootEventListener> clazz : classes) {
            registerListener(clazz);
        }
    }

    public void unRegisterListener(Class<? extends JbootEventListener> listenerClass) {

        deleteListner(listenerMap, listenerClass);
        deleteListner(asyncListenerMap, listenerClass);

        if (Jboot.isDevMode()) {
            LOG.debug(String.format("listener[%s]-->>unRegisterListener.", listenerClass));
        }

    }

    private void deleteListner(Map<String, List<JbootEventListener>> map, Class<? extends JbootEventListener> listenerClass) {
        for (Map.Entry<String, List<JbootEventListener>> entry : map.entrySet()) {
            JbootEventListener deleteListener = null;
            for (JbootEventListener listener : entry.getValue()) {
                if (listener.getClass() == listenerClass) {
                    deleteListener = listener;
                }
            }
            if (deleteListener != null) {
                entry.getValue().remove(deleteListener);
            }
        }
    }

    public void registerListener(Class<? extends JbootEventListener> listenerClass) {

        if (listenerClass == null) {
            return;
        }

        EventConfig listenerAnnotation = listenerClass.getAnnotation(EventConfig.class);
        if (listenerAnnotation == null) {
            //当未配置 EventConfig 的时候，可以手动注册
            return;
        }

        String[] actions = AnnotationUtil.get(listenerAnnotation.action());
        if (actions == null) {
            LOG.warn("listenerClass[" + listenerAnnotation + "] register fail, because action is null or blank.");
            return;
        }

        if (listenerHasRegisterBefore(listenerClass)) {
            return;
        }

        JbootEventListener listener = ClassUtil.newInstance(listenerClass);
        if (listener == null) {
            return;
        }

        registerListener(listener, listenerAnnotation.async(), actions);

    }


    public void unRegisterListener(JbootEventListener eventListener) {
        unRegisterListener(eventListener.getClass());
    }

    /**
     * 手从初始化 EventListener ，手动注册
     *
     * @param eventListener
     * @param async
     * @param actions
     */
    public void registerListener(JbootEventListener eventListener, boolean async, String... actions) {

        for (String action : actions) {
            List<JbootEventListener> list = async ? asyncListenerMap.get(action) : listenerMap.get(action);

            if (null == list) {
                list = new ArrayList<>();
            }

            if (list.contains(eventListener)) {
                continue;
            }

            list.add(eventListener);

            WeightUtil.sort(list);

            if (async) {
                asyncListenerMap.put(action, list);
            } else {
                listenerMap.put(action, list);
            }
        }

        if (Jboot.isDevMode()) {
            LOG.debug(String.format("listener[%s]-->>registered.", eventListener));
        }
    }


    private boolean listenerHasRegisterBefore(Class<? extends JbootEventListener> listenerClass) {
        return findFromMap(listenerClass, listenerMap)
                || findFromMap(listenerClass, asyncListenerMap);
    }

    private boolean findFromMap(Class<? extends JbootEventListener> listenerClass, Map<String, List<JbootEventListener>> map) {
        for (Map.Entry<String, List<JbootEventListener>> entry : map.entrySet()) {
            List<JbootEventListener> listeners = entry.getValue();
            if (listeners == null || listeners.isEmpty()) {
                continue;
            }
            for (JbootEventListener ml : listeners) {
                if (listenerClass == ml.getClass()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void publish(final JbootEvent event) {
        String action = event.getAction();

        List<JbootEventListener> syncListeners = listenerMap.get(action);
        if (syncListeners != null && !syncListeners.isEmpty()) {
            invokeListeners(event, syncListeners);
        }

        List<JbootEventListener> listeners = asyncListenerMap.get(action);
        if (listeners != null && !listeners.isEmpty()) {
            invokeListenersAsync(event, listeners);
        }

    }

    private void invokeListeners(final JbootEvent event, List<JbootEventListener> syncListeners) {
        for (final JbootEventListener listener : syncListeners) {
            try {
                listener.onEvent(event);
            } catch (Throwable e) {
                LOG.error(String.format("listener[%s] onEvent is error! ", listener.getClass()), e);
            }
        }
    }

    private void invokeListenersAsync(final JbootEvent event, List<JbootEventListener> listeners) {
        for (final JbootEventListener listener : listeners) {
            threadPool.execute(() -> {
                try {
                    listener.onEvent(event);
                } catch (Throwable e) {
                    LOG.error(String.format("listener[%s] onEvent is error! ", listener.getClass()), e);
                }
            });
        }
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }
}
