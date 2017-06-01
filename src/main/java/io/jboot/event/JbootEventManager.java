/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.event;

import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;
import io.jboot.event.annotation.EventConfig;
import io.jboot.utils.ArrayUtils;

import java.util.*;
import java.util.concurrent.*;


public class JbootEventManager {

    private final ExecutorService threadPool;
    private final Map<String, List<JbootEventListener>> asyncListenerMap;
    private final Map<String, List<JbootEventListener>> listenerMap;
    private static final Log log = Log.getLog(JbootEventManager.class);

    private static JbootEventManager manager;

    private JbootEventManager() {
        threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.MINUTES,
                new SynchronousQueue<Runnable>());
        asyncListenerMap = new ConcurrentHashMap<>();
        listenerMap = new ConcurrentHashMap<>();

        initListeners();
    }

    public static JbootEventManager me() {
        if (manager == null) {
            manager = ClassNewer.singleton(JbootEventManager.class);
        }
        return manager;
    }

    private void initListeners() {
        List<Class<JbootEventListener>> classes = ClassScanner.scanSubClass(JbootEventListener.class, true);
        if (ArrayUtils.isNullOrEmpty(classes)) {
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
            System.out.println(String.format("listener[%s]-->>unRegisterListener.", listenerClass));
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
            log.warn("listenerClass[" + listenerAnnotation + "] resigter fail,because not use EventConfig annotation.");
            return;
        }

        String[] actions = listenerAnnotation.action();
        if (actions == null || actions.length == 0) {
            log.warn("listenerClass[" + listenerAnnotation + "] resigter fail, because action is null or blank.");
            return;
        }

        if (listenerHasRegisterBefore(listenerClass)) {
            return;
        }

        JbootEventListener listener = ClassNewer.newInstance(listenerClass);
        if (listener == null) {
            return;
        }

        for (String action : actions) {
            List<JbootEventListener> list = null;
            if (listenerAnnotation.async()) {
                list = asyncListenerMap.get(action);
            } else {
                list = listenerMap.get(action);
            }
            if (null == list) {
                list = new ArrayList<>();
            }
            if (list.isEmpty() || !list.contains(listener)) {
                list.add(listener);
            }

            Collections.sort(list, new Comparator<JbootEventListener>() {
                @Override
                public int compare(JbootEventListener o1, JbootEventListener o2) {
                    EventConfig c1 = o1.getClass().getAnnotation(EventConfig.class);
                    EventConfig c2 = o2.getClass().getAnnotation(EventConfig.class);
                    return c1.weight() - c2.weight();
                }
            });


            if (listenerAnnotation.async()) {
                asyncListenerMap.put(action, list);
            } else {
                listenerMap.put(action, list);
            }
        }

        if (Jboot.isDevMode()) {
            System.out.println(String.format("listener[%s]-->>registered.", listener));
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

    public void pulish(final JbootEvent event) {
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
                if (Jboot.isDevMode()) {
                    System.out.println(String.format("listener[%s]-->>onEvent(%s)", listener, event));
                }
                listener.onEvent(event);
            } catch (Throwable e) {
                log.error(String.format("listener[%s] onEvent is error! ", listener.getClass()), e);
            }
        }
    }

    private void invokeListenersAsync(final JbootEvent event, List<JbootEventListener> listeners) {
        for (final JbootEventListener listener : listeners) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (Jboot.isDevMode()) {
                            System.out.println(String.format("listener[%s]-->>onEvent(%s) in async", listener, event));
                        }
                        listener.onEvent(event);
                    } catch (Throwable e) {
                        log.error(String.format("listener[%s] onEvent is error! ", listener.getClass()), e);
                    }
                }
            });
        }
    }

}
