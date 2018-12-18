/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.app.config;

import io.jboot.app.config.annotation.ConfigModel;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置管理类
 */
public class JbootConfigManager {

    private static Map<String, String> argMap;

    private Properties mainProperties;

    private ConcurrentHashMap<String, Object> configs = new ConcurrentHashMap<>();


    private static JbootConfigManager instance;

    public static JbootConfigManager me() {
        if (instance == null) {
            instance = new JbootConfigManager();
        }
        return instance;
    }

    private JbootConfigManager() {
        init();
    }


    private void init() {

        File jbootPropertiesFile = new File(Kits.getRootClassPath(), "jboot.properties");
        if (!jbootPropertiesFile.exists()) {
            mainProperties = new Properties();
        } else {
            mainProperties = new Prop("jboot.properties").getProperties();
        }

        String mode = getValueByKey("jboot.mode");

        if (Kits.isNotBlank(mode)) {

            String p = String.format("jboot-%s.properties", mode);
            if (new File(Kits.getRootClassPath(), p).exists()) {
                mainProperties.putAll(new Prop(p).getProperties());
            }
        }

    }


    public <T> T get(Class<T> clazz) {
        ConfigModel propertyConfig = clazz.getAnnotation(ConfigModel.class);
        if (propertyConfig == null) {
            return get(clazz, null, null);
        }
        return get(clazz, propertyConfig.prefix(), propertyConfig.file());
    }


    /**
     * 获取配置信息，并创建和赋值clazz实例
     *
     * @param clazz  指定的类
     * @param prefix 配置文件前缀
     * @param <T>
     * @return
     */
    public <T> T get(Class<T> clazz, String prefix, String file) {

        T obj = (T) configs.get(clazz.getName() + prefix);

        if (obj != null) {
           return obj;
        }

        synchronized (clazz) {
            if (obj == null) {
                obj = createConfigObject(clazz, prefix, file);
                configs.put(clazz.getName() + prefix, obj);
            }
        }

        return obj;
    }

    public <T> T createConfigObject(Class<T> clazz, String prefix, String file) {

        T obj = Kits.newInstance(clazz);
        Collection<Method> setMethods = Kits.getClassSetMethods(clazz);

        if (setMethods == null || setMethods.isEmpty()) {
            configs.put(clazz.getName() + prefix, obj);
            return obj;
        }

        for (Method method : setMethods) {

            String key = getKeyByMethod(prefix, method);
            String value = getValueByKey(key);

            if (Kits.isNotBlank(file)) {
                try {
                    Prop prop = new Prop(file);
                    String filePropValue = getValueByKey(prop.getProperties(), key);
                    if (Kits.isNotBlank(filePropValue)) {
                        value = filePropValue;
                    }
                } catch (Throwable ex) {
                    Kits.doNothing(ex);
                }
            }

            try {
                if (Kits.isNotBlank(value)) {
                    Object val = Kits.convert(method.getParameterTypes()[0], value);
                    method.invoke(obj, val);
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return obj;
    }

    private String getKeyByMethod(String prefix, Method method) {

        String key = Kits.firstCharToLowerCase(method.getName().substring(3));

        if (Kits.isNotBlank(prefix)) {
            key = prefix.trim() + "." + key;
        }

        return key;
    }


    public String getValueByKey(String key) {
        return getValueByKey(mainProperties, key);
    }

    /**
     * 获取值的优先顺序：1、启动配置  2、环境变量   3、properties配置文件
     *
     * @param key
     * @return
     */
    public String getValueByKey(Properties properties, String key) {

        String value = getBootArg(key);

        if (Kits.isBlank(value)) {
            value = System.getenv(key);
        }

        if (Kits.isBlank(value)) {
            value = System.getProperty(key);
        }

        if (Kits.isBlank(value)) {
            value = (String) properties.get(key);
        }
        return value == null ? null : value.trim();
    }


    /**
     * 获取Jboot默认的配置信息
     *
     * @return
     */
    public Properties getProperties() {

        Properties properties = new Properties();
        properties.putAll(mainProperties);

        if (System.getenv() != null) {
            for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }

        if (System.getProperties() != null) {
            properties.putAll(System.getProperties());
        }

        if (getBootArgs() != null) {
            for (Map.Entry<String, String> entry : getBootArgs().entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }

        return properties;
    }



    /**
     * 解析启动参数
     *
     * @param args
     */
    public void parseArgs(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }

        for (String arg : args) {
            int indexOf = arg.indexOf("=");
            if (arg.startsWith("--") && indexOf > 0) {
                String key = arg.substring(2, indexOf);
                String value = arg.substring(indexOf + 1);
                setBootArg(key, value);
            }
        }
    }

    public void setBootArg(String key, Object value) {
        if (argMap == null) {
            argMap = new HashMap<>();
        }
        argMap.put(key, value.toString());
    }

    /**
     * 获取启动参数
     *
     * @param key
     * @return
     */
    public String getBootArg(String key) {
        if (argMap == null) return null;
        return argMap.get(key);
    }

    public Map<String, String> getBootArgs() {
        return argMap;
    }


}
