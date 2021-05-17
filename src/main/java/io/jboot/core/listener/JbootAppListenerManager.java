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
package io.jboot.core.listener;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.app.JbootApplicationConfig;
import io.jboot.core.weight.WeightUtil;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JbootAppListenerManager implements JbootAppListener {
    private static Log log = Log.getLog(JbootAppListenerManager.class);

    private static JbootAppListenerManager me = new JbootAppListenerManager();


    public static JbootAppListenerManager me() {
        return me;
    }


    List<JbootAppListener> listeners = new ArrayList<>();

    private JbootAppListenerManager() {


        Set<String> listenerset = null;
        String listener = JbootApplicationConfig.get().getListener();
        if (StrUtil.isNotBlank(listener) && !"*".equals(listener)){
            listenerset = StrUtil.splitToSet(listener, ";");
        }

        if (listenerset != null && !listenerset.isEmpty()){
            listenerset.forEach(clazz -> {
                JbootAppListener l = ClassUtil.newInstance(clazz);
                if (l != null) {
                    listeners.add(l);
                }else {
                    LogKit.warn("Can not create JbootAppListener by class: " + clazz);
                }
            });
        }else {
            List<Class<JbootAppListener>> allListeners = ClassScanner.scanSubClass(JbootAppListener.class, true);
            for (Class<? extends JbootAppListener> clazz : allListeners) {
                if (JbootAppListenerManager.class == clazz || JbootAppListenerBase.class == clazz) {
                    continue;
                }
                JbootAppListener l = ClassUtil.newInstance(clazz);
                if (l != null) {
                    listeners.add(l);
                }
            }
        }



        WeightUtil.sort(listeners);
    }


    @Override
    public void onInit() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onInit();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onConstantConfig(Constants constants) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onConstantConfig(constants);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onRouteConfig(Routes routes) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onRouteConfig(routes);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onEngineConfig(Engine engine) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onEngineConfig(engine);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onPluginConfig(JfinalPlugins plugins) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onPluginConfig(plugins);
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
    public void onStartBefore() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onStartBefore();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onStart() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onStart();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onStartFinish() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onStartFinish();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onStop() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onStop();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

}
