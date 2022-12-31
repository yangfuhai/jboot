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
package io.jboot.app.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.app.config.support.apollo.ApolloConfigManager;
import io.jboot.app.config.support.nacos.NacosConfigManager;

import java.io.File;
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
        String fileName = getConfigValue(null, "jboot_properties_name");
        if (fileName == null || fileName.length() == 0) {
            fileName = "jboot";
        }

        String pathName = getConfigValue(null, "jboot_properties_path");
        mainProperties = JbootConfigKit.readProperties(pathName, fileName);


        //可以直接在 默认目录下的 jboot.properties 再次指定外部目录
        String newFileName = getConfigValue(mainProperties, "jboot_properties_name");
        if (newFileName != null && newFileName.length() > 0 && "jboot".equals(fileName)) {
            fileName = newFileName;
        }

        String newPathName = getConfigValue(mainProperties, "jboot_properties_path");
        if (newPathName != null && newPathName.length() > 0 && (pathName == null || pathName.length() == 0)) {
            pathName = newPathName;
        }


        //配置了 pathName，需要再去 path 读取 jboot.properties 文件
        if (pathName != null && pathName.length() > 0) {
            Properties newMainProperties = JbootConfigKit.readProperties(pathName, fileName);
            mainProperties.putAll(newMainProperties);
        }


        String mode = getConfigValue("jboot.app.mode");
        if (JbootConfigKit.isNotBlank(mode)) {
            //开始加载 mode properties
            //并全部添加覆盖掉掉 main properties
            String modeFileName = fileName + "-" + mode;
            Properties modeProperties = JbootConfigKit.readProperties(pathName, modeFileName);

            mainProperties.putAll(modeProperties);
        }


        //通过启动参数 --config=./xxx.properties 来指定配置文件启动
        String configFile = getConfigValue(null, "config");
        Properties configFileProperties = null;
        if (configFile != null && configFile.startsWith("./")) {
            configFileProperties = JbootConfigKit.readExternalProperties(configFile.substring(2));
        } else if (configFile != null) {
            configFileProperties = JbootConfigKit.readPropertiesFile(new File(configFile));
        } else {
            //通过在 fatjar 的相同目录下，创建 jboot.properties 配置文件来启动
            configFileProperties = JbootConfigKit.readExternalProperties(fileName);
        }

        if (configFileProperties != null && !configFileProperties.isEmpty()) {
            mainProperties.putAll(configFileProperties);
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
        ConfigModel configModel = clazz.getAnnotation(ConfigModel.class);
        if (configModel == null) {
            return get(clazz, null, null);
        }
        return get(clazz, configModel.prefix(), configModel.file());
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
            Object obj = createConfigObject(clazz, prefix, file);
            configCache.putIfAbsent(clazz.getName() + prefix, obj);
            configObject = configCache.get(clazz.getName() + prefix);
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
        ConfigModel configModel = clazz.getAnnotation(ConfigModel.class);
        if (configModel == null) {
            return refreshAndGet(clazz, null, null);
        }
        return refreshAndGet(clazz, configModel.prefix(), configModel.file());
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

        mainProperties.putAll(JbootConfigKit.readProperties("jboot"));

        String mode = getConfigValue(mainProperties, "jboot.app.mode");
        if (JbootConfigKit.isNotBlank(mode)) {
            String modeFileName = "jboot-" + mode;
            mainProperties.putAll(JbootConfigKit.readProperties(modeFileName));
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
    public synchronized <T> T createConfigObject(Class<T> clazz, String prefix, String file) {
        T configObject = JbootConfigKit.newInstance(clazz);
        for (Method setterMethod : JbootConfigKit.getClassSetMethods(clazz)) {
            String key = buildKey(prefix, setterMethod);
            String value = getConfigValue(key);

            if (JbootConfigKit.isNotBlank(file)) {
                JbootProp prop = new JbootProp(file);
                String filePropValue = getConfigValue(prop.getProperties(), key);
                if (JbootConfigKit.isNotBlank(filePropValue)) {
                    value = filePropValue;
                }
            }

            if (JbootConfigKit.isNotBlank(value)) {
                Object val = JbootConfigKit.convert(setterMethod.getParameterTypes()[0], value, setterMethod.getGenericParameterTypes()[0]);
                if (val != null) {
                    try {
                        setterMethod.invoke(configObject, val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return configObject;
    }


    private String buildKey(String prefix, Method method) {
        String key = JbootConfigKit.firstCharToLowerCase(method.getName().substring(3));
        if (JbootConfigKit.isNotBlank(prefix)) {
            key = prefix.trim() + "." + key;
        }
        return key;
    }


    public String getConfigValue(String key) {
        return getConfigValue(mainProperties, key);
    }


    public String getConfigValue(Properties properties, String key) {
        if (JbootConfigKit.isBlank(key)) {
            return null;
        }
        String originalValue = getOriginalConfigValue(properties, key);
        String decryptValue = decryptor != null ? decryptor.decrypt(key, originalValue) : originalValue;
        String parseValue = JbootConfigKit.parseValue(this, decryptValue);
        return parseValue == null || parseValue.trim().length() == 0 ? null : parseValue.trim();
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
            if (JbootConfigKit.isNotBlank(value)) {
                return value.trim();
            }
        }

        //boot arg
        value = getBootArg(key);
        if (JbootConfigKit.isNotBlank(value)) {
            return value.trim();
        }

        //env
        value = System.getenv(key);
        if (JbootConfigKit.isNotBlank(value)) {
            return value.trim();
        }

        //upperCase env
        // 把xxx.xxx.xxx 转换为 XXX_XXX_XXX，
        // 例如：jboot.datasource.url 转换为 JBOOT_DATASOURCE_URL
        String tempKey = key.toUpperCase().replace('.', '_');
        value = System.getenv(tempKey);
        if (JbootConfigKit.isNotBlank(value)) {
            return value.trim();
        }

        //system property
        value = System.getProperty(key);
        if (JbootConfigKit.isNotBlank(value)) {
            return value.trim();
        }

        //user properties
        if (properties != null) {
            value = (String) properties.get(key);
            if (JbootConfigKit.isNotBlank(value)) {
                return value.trim();
            }
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


    public synchronized void setRemoteProperty(String key, String value) {
        if (remoteProperties == null) {
            remoteProperties = new ConcurrentHashMap();
        }
        remoteProperties.put(key, value);
    }


    public void removeRemoteProperty(String key) {
        if (remoteProperties != null) {
            remoteProperties.remove(key);
        }
    }


    public synchronized void setRemoteProperties(Map map) {
        if (remoteProperties == null) {
            remoteProperties = new ConcurrentHashMap();
        }
        remoteProperties.putAll(map);
    }


    public void addConfigChangeListener(JbootConfigChangeListener listener, Class forClass) {
        ConfigModel configModel = (ConfigModel) forClass.getAnnotation(ConfigModel.class);
        if (configModel == null) {
            throw new IllegalArgumentException("forClass:" + forClass + " has no @ConfigModel annotation");
        }

        String prefix = configModel.prefix();
        List<Method> setterMethods = JbootConfigKit.getClassSetMethods(forClass);
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
        listenersMultimap.entries().removeIf(entry -> entry.getValue() == listener);
    }


    public void notifyChangeListeners(String key, String newValue, String oldValue) {
        if (key == null) {
            return;
        }

        Collection<JbootConfigChangeListener> listeners = listenersMultimap.get(key);
        for (JbootConfigChangeListener listener : listeners) {
            try {
                listener.onChange(key, newValue, oldValue);
            } catch (Throwable ex) {
                com.jfinal.kit.LogKit.error(ex.toString(), ex);
            }
        }
    }


    /**
     * 解析启动参数
     *
     * @param args
     */
    public static void parseArgs(String[] args) {
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

    public static void setBootArg(String key, Object value) {
        if (argMap == null) {
            argMap = new HashMap<>();
        }
        if (key == null) {
            return;
        }
        if (value == null || value.toString().trim().length() == 0) {
            argMap.remove(key.trim());
        } else {
            argMap.put(key.trim(), value.toString().trim());
        }
    }

    public static void setBootProperties(Properties properties) {
        Objects.requireNonNull(properties, "properties must not be null");
        properties.forEach((o, o2) -> setBootArg(o.toString(), o2));
    }


    public static void setBootProperties(String propertiesFilePath) {
        File file = new File(propertiesFilePath);
        if (file.exists()) {
            setBootProperties(file);
        } else {
            setBootProperties(new JbootProp(propertiesFilePath).getProperties());
        }
    }


    public static void setBootProperties(File propertiesFile) {
        if (propertiesFile.exists()) {
            setBootProperties(new JbootProp(propertiesFile).getProperties());
        } else {
            System.err.println("Warning: properties file not exists: " + propertiesFile);
        }
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
            String appMode = getConfigValue("jboot.app.mode");
            devMode = (null == appMode || "".equals(appMode.trim()) || "dev".equalsIgnoreCase(appMode.trim()));
        }
        return devMode;
    }

    public void setDevMode(Boolean devMode) {
        this.devMode = devMode;
    }
}
