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
package io.jboot.server.listener;

import com.google.inject.Binder;
import com.jfinal.config.*;
import com.jfinal.log.Log;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.server.ContextListeners;
import io.jboot.server.JbootServer;
import io.jboot.server.Servlets;
import io.jboot.utils.ClassKits;
import io.jboot.utils.ClassScanner;

import java.util.ArrayList;
import java.util.List;

public class JbootAppListenerManager implements JbootAppListener {
    private static Log log = Log.getLog(JbootAppListenerManager.class);

    private static JbootAppListenerManager me = new JbootAppListenerManager();


    public static JbootAppListenerManager me() {
        return me;
    }


    List<JbootAppListener> listeners = new ArrayList<>();

    private JbootAppListenerManager() {
        List<Class<JbootAppListener>> allListeners = ClassScanner.scanSubClass(JbootAppListener.class, true);
        if (allListeners == null || allListeners.size() == 0) {
            return;
        }

        for (Class<? extends JbootAppListener> clazz : allListeners) {
            if (JbootAppListenerManager.class == clazz || JbootAppListenerBase.class == clazz) {
                continue;
            }

            JbootAppListener listener = ClassKits.newInstance(clazz, false);
            if (listener != null) {
                listeners.add(listener);
            }
        }
    }


    @Override
    public void onJbootDeploy(Servlets servlets, ContextListeners listeners) {
        for (JbootAppListener listener : this.listeners) {
            try {
                listener.onJbootDeploy(servlets, listeners);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onJfinalConstantConfig(Constants constants) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onJfinalConstantConfig(constants);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onJfinalRouteConfig(Routes routes) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onJfinalRouteConfig(routes);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onJfinalEngineConfig(Engine engine) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onJfinalEngineConfig(engine);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onJfinalPluginConfig(JfinalPlugins plugins) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onJfinalPluginConfig(plugins);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onInterceptorConfig(Interceptors interceptors) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onInterceptorConfig(interceptors);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onHandlerConfig(handlers);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onJFinalStarted() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onJFinalStarted();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onJFinalStop() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onJFinalStop();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onJbootStarted() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onJbootStarted();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onAppStartBefore(JbootServer jbootServer) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onAppStartBefore(jbootServer);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onGuiceConfigure(Binder binder) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onGuiceConfigure(binder);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }
}
