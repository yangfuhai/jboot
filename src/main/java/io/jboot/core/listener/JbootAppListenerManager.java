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
package io.jboot.core.listener;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.kit.LogKit;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.app.JbootApplicationConfig;
import io.jboot.core.weight.WeightUtil;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class JbootAppListenerManager implements JbootAppListener {

    private static JbootAppListenerManager me = new JbootAppListenerManager();

    public static JbootAppListenerManager me() {
        return me;
    }

    private List<JbootAppListener> listeners = new ArrayList<>();

    private JbootAppListenerManager() {

        String listener = JbootApplicationConfig.get().getListener();

        String listenerPackage = JbootApplicationConfig.get().getListenerPackage();
        Set<String> packages = StrUtil.isNotBlank(listenerPackage) && !"*".equals(listenerPackage.trim())
                ? StrUtil.splitToSet(listenerPackage, ";") : new HashSet<>();

        if (StrUtil.isBlank(listener) || "*".equals(listener.trim())) {
            List<Class<JbootAppListener>> allListeners = ClassScanner.scanSubClass(JbootAppListener.class, true);
            allListeners.removeIf((Predicate<Class<? extends JbootAppListener>>) c ->
                    c == JbootAppListenerManager.class || c == JbootAppListenerBase.class);

            allListeners.forEach(clazz -> {
                if (isMatchedPackage(packages, clazz.getCanonicalName())) {
                    JbootAppListener appListener = ClassUtil.newInstance(clazz);
                    if (appListener != null) {
                        listeners.add(appListener);
                    }
                }
            });
        } else {
            StrUtil.splitToSet(listener, ";").forEach(className -> {
                if (isMatchedPackage(packages, className)) {
                    JbootAppListener appListener = ClassUtil.newInstance(className);
                    if (appListener != null) {
                        listeners.add(appListener);
                    } else {
                        // log 组件还未配置，无法使用 log 组件输出
                        System.err.println("Can not create JbootAppListener by class: " + className);
                    }
                }
            });
        }

        WeightUtil.sort(listeners);
    }

    private boolean isMatchedPackage(Set<String> packages, String className) {
        //matched all
        if (packages == null || packages.isEmpty()) {
            return true;
        }

        for (String packageString : packages) {
            if (className.startsWith(packageString)) {
                return true;
            }
        }
        return false;
    }

    public List<JbootAppListener> getListeners() {
        return listeners;
    }

    @Override
    public void onInit() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onInit();
            } catch (Throwable ex) {
                //在 init 的时候， log 组件未初始化，无法使用
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onConstantConfigBefore(Constants constants) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onConstantConfigBefore(constants);
            } catch (Throwable ex) {
                //在 onConstantConfigBefore 的时候， log 组件未初始化，无法使用
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onConstantConfig(Constants constants) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onConstantConfig(constants);
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onRouteConfig(Routes routes) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onRouteConfig(routes);
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onEngineConfig(Engine engine) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onEngineConfig(engine);
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onPluginConfig(JfinalPlugins plugins) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onPluginConfig(plugins);
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onInterceptorConfig(Interceptors interceptors) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onInterceptorConfig(interceptors);
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }


    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onHandlerConfig(handlers);
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onStartBefore() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onStartBefore();
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onStart() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onStart();
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onStartFinish() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onStartFinish();
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

    @Override
    public void onStop() {
        for (JbootAppListener listener : listeners) {
            try {
                listener.onStop();
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

}
