/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jfinal.kit.LogKit;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.app.config.support.apollo.ApolloConfigManager;
import io.jboot.app.config.support.nacos.NacosConfigManager;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置管理类
 */
public class JbootConfigManager {

    //启动参数
    private static Map<String, String> argMap;

    //jboot.properties 和 jboot-dev.properties 等内容
    private Properties mainProperties;

    //分布式配置
    private Map remoteProperties;

    //ConfigObje 缓存 class + prefix : object
    private Map<String, Object> configCache = new ConcurrentHashMap<>();

    //监听器
    private Multimap<String, JbootConfigChangeListener> listenersMultimap = ArrayListMultimap.create();

    //配置内容解密工具
    private JbootConfigDecryptor decryptor;


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

        mainProperties = new Prop("jboot.properties").getProperties();

        String mode = getConfigValue("jboot.app.mode");

        if (ConfigUtil.isNotBlank(mode)) {
            String p = String.format("jboot-%s.properties", mode);
            mainProperties.putAll(new Prop(p).getProperties());
        }


        NacosConfigManager.me().init(this);
        ApolloConfigManager.me().init(this);
    }

    public JbootConfigDecryptor getDecryptor() {
        return decryptor;
    }

    public void setDecryptor(JbootConfigDecryptor decryptor) {
        this.decryptor = decryptor;
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

        /**
         * 开发模式下，热加载会导致由于 Config 是不同的 ClassLoader 而导致异常，
         * 如果走缓存会Class转化异常
         */
        if (isDevMode()) {
            return createConfigObject(clazz, prefix, file);
        }

        Object configObject = configCache.get(clazz.getName() + prefix);

        if (configObject == null) {
            synchronized (clazz) {
                if (configObject == null) {
                    configObject = createConfigObject(clazz, prefix, file);
                    configCache.put(clazz.getName() + prefix, configObject);
                }
            }
        }

        return (T) configObject;
    }


    /**
     * 刷新数据，并返回新的数据
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T refreshAndGet(Class<T> clazz) {
        ConfigModel propertyConfig = clazz.getAnnotation(ConfigModel.class);
        if (propertyConfig == null) {
            return refreshAndGet(clazz, null, null);
        }
        return refreshAndGet(clazz, propertyConfig.prefix(), propertyConfig.file());
    }


    /**
     * 刷新数据，并返回新的数据
     *
     * @param clazz
     * @param prefix
     * @param file
     * @param <T>
     * @return
     */
    public <T> T refreshAndGet(Class<T> clazz, String prefix, String file) {

        configCache.remove(clazz.getName() + prefix);
        refreshMainProperties();

        return get(clazz, prefix, file);
    }

    private void refreshMainProperties() {

        Properties properties = new Prop("jboot.properties").getProperties();
        mainProperties.putAll(properties);

        String mode = getConfigValue(properties, "jboot.app.mode");
        if (ConfigUtil.isNotBlank(mode)) {
            String p = String.format("jboot-%s.properties", mode);
            mainProperties.putAll(new Prop(p).getProperties());
        }

    }


    /**
     * 创建一个新的配置对象（Object）
     *
     * @param clazz
     * @param prefix
     * @param file
     * @param <T>
     * @return
     */
    public <T> T createConfigObject(Class<T> clazz, String prefix, String file) {
        Object configObject = ConfigUtil.newInstance(clazz);
        List<Method> setterMethods = ConfigUtil.getClassSetMethods(clazz);
        if (setterMethods != null) {
            for (Method setterMethod : setterMethods) {

                String key = buildKey(prefix, setterMethod);
                String value = getConfigValue(key);

                if (ConfigUtil.isNotBlank(file)) {
                    Prop prop = new Prop(file);
                    String filePropValue = getConfigValue(prop.getProperties(), key);
                    if (ConfigUtil.isNotBlank(filePropValue)) {
                        value = filePropValue;
                    }
                }

                if (ConfigUtil.isNotBlank(value)) {
                    Object val = ConfigUtil.convert(setterMethod.getParameterTypes()[0], value, setterMethod.getGenericParameterTypes()[0]);
                    if (val != null) {
                        try {
                            setterMethod.invoke(configObject, val);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return (T) configObject;
    }


    private String buildKey(String prefix, Method method) {
        String key = ConfigUtil.firstCharToLowerCase(method.getName().substring(3));
        if (ConfigUtil.isNotBlank(prefix)) {
            key = prefix.trim() + "." + key;
        }
        return key;
    }


    public String getConfigValue(String key) {
        return getConfigValue(mainProperties, key);
    }


    public String getConfigValue(Properties properties, String key) {
        if (StrUtil.isBlank(key)) {
            return "";
        }
        String originalValue = getOriginalConfigValue(properties, key);
        String stringValue = decryptor != null ? decryptor.decrypt(key, originalValue) : originalValue;

        List<ConfigPart> configParts = ConfigUtil.parseParts(stringValue);
        if (configParts == null || configParts.isEmpty()) {
            return stringValue;
        }

        for (ConfigPart cp : configParts) {
            String value = getConfigValue(properties, cp.getKey());
            value = StrUtil.isNotBlank(value) ? value : cp.getDefaultValue();
            stringValue = stringValue.replace(cp.getPartString(), value);
        }
        return stringValue;
    }


    /**
     * 获取值的优先顺序：1、远程配置  2、启动配置   3、环境变量  4、系统属性  5、properties配置文件
     *
     * @param key
     * @return
     */
    private String getOriginalConfigValue(Properties properties, String key) {

        String value = null;

        //优先读取分布式配置内容
        if (remoteProperties != null) {
            value = (String) remoteProperties.get(key);
            if (ConfigUtil.isNotBlank(value)) {
                return value.trim();
            }
        }

        //boot arg
        value = getBootArg(key);
        if (ConfigUtil.isNotBlank(value)) {
            return value.trim();
        }

        //env
        value = System.getenv(key);
        if (ConfigUtil.isNotBlank(value)) {
            return value.trim();
        }

        //upperCase env
        // 把xxx.xxx.xxx 转换为 XXX_XXX_XXX，
        // 例如：jboot.datasource.url 转换为 JBOOT_DATASOURCE_URL
        String tempKey = key.toUpperCase().replace('.', '_');
        value = System.getenv(tempKey);
        if (ConfigUtil.isNotBlank(value)) {
            return value.trim();
        }

        //system property
        value = System.getProperty(key);
        if (ConfigUtil.isNotBlank(value)) {
            return value.trim();
        }

        //user properties
        value = (String) properties.get(key);
        if (ConfigUtil.isNotBlank(value)) {
            return value.trim();
        }

        return null;
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

        if (remoteProperties != null) {
            properties.putAll(remoteProperties);
        }

        return properties;
    }

    public Map<String, Object> getConfigCache() {
        return configCache;
    }


    public void setRemoteProperty(String key, String value) {
        if (remoteProperties == null) {
            synchronized (this) {
                if (remoteProperties == null) {
                    remoteProperties = new ConcurrentHashMap();
                }
            }
        }
        remoteProperties.put(key, value);
    }

    public void setRemoteProperties(Map map) {
        if (remoteProperties == null) {
            synchronized (this) {
                if (remoteProperties == null) {
                    remoteProperties = new ConcurrentHashMap();
                }
            }
        }
        remoteProperties.putAll(map);
    }


    public void addConfigChangeListener(JbootConfigChangeListener listener, Class forClass) {
        ConfigModel configModel = (ConfigModel) forClass.getAnnotation(ConfigModel.class);
        if (configModel == null) {
            throw new IllegalArgumentException("forClass:" + forClass + " has no @ConfigModel annotation");
        }

        String prefix = configModel.prefix();
        List<Method> setterMethods = ConfigUtil.getClassSetMethods(forClass);
        if (setterMethods != null) {
            for (Method setterMethod : setterMethods) {
                String key = buildKey(prefix, setterMethod);
                listenersMultimap.put(key, listener);
            }
        }
    }


    public void addConfigChangeListener(JbootConfigChangeListener listener, String... forKeys) {
        if (listener == null) {
            throw new NullPointerException("listener must not null.");
        }


        if (forKeys == null || forKeys.length == 0) {
            throw new NullPointerException("forKeys must not null or empty.");
        }

        for (String forKey : forKeys) {
            listenersMultimap.put(forKey, listener);
        }
    }


    public void removeConfigChangeListener(JbootConfigChangeListener listener) {
        for (String key : listenersMultimap.keySet()) {
            Iterator<JbootConfigChangeListener> iterator = listenersMultimap.get(key).iterator();
            while (iterator.hasNext()) {
                JbootConfigChangeListener entry = iterator.next();
                if (listener == entry) {
                    iterator.remove();
                }
            }
        }
    }


    public void notifyChangeListeners(String key, String newValue, String oldValue) {
        if (key == null) {
            return;
        }

        Collection<JbootConfigChangeListener> listeners = listenersMultimap.get(key);
        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        for (JbootConfigChangeListener listener : listeners) {
            try {
                listener.onChange(key, newValue, oldValue);
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
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
        if (argMap == null) {
            return null;
        }
        return argMap.get(key);
    }

    public Map<String, String> getBootArgs() {
        return argMap;
    }


    private Boolean devMode = null;

    public boolean isDevMode() {
        if (devMode == null) {
            synchronized (this) {
                if (devMode == null) {
                    String appMode = getConfigValue("jboot.app.mode");
                    devMode = (null == appMode || "".equals(appMode.trim()) || "dev".equalsIgnoreCase(appMode.trim()));
                }
            }
        }
        return devMode;
    }


}
