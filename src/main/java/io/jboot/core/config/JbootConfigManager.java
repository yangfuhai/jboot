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
package io.jboot.core.config;

import com.jfinal.core.converter.TypeConverter;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import io.jboot.app.JbootApplication;
import io.jboot.core.config.annotation.PropertyModel;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.kits.ArrayKits;
import io.jboot.kits.ClassKits;
import io.jboot.kits.StringKits;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置管理类
 * <p>
 * 用于读取配置信息，包括本地配置信息和分布式远程配置信息
 */
public class JbootConfigManager {

    private static final Log LOG = Log.getLog(JbootConfigManager.class);


    private Properties mainProperties;

    private PropInfoMap propInfoMap = new PropInfoMap();
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



    /**
     * 读取本地配置文件
     */
    public void init() {
        try {
            Prop prop = PropKit.use("jboot.properties");
            mainProperties = prop.getProperties();
        } catch (Throwable ex) {
            LOG.warn("Could not find jboot.properties in your class path.");
            mainProperties = new Properties();
        }

        initModeProp();

    }


    public <T> T get(Class<T> clazz) {
        PropertyModel propertyConfig = clazz.getAnnotation(PropertyModel.class);
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
        if (obj == null) {
            synchronized (clazz) {
                if (obj == null) {
                    obj = newConfigObject(clazz, prefix, file);
                    configs.put(clazz.getName() + prefix, obj);
                }
            }
        }

        return obj;
    }

    public <T> T newConfigObject(Class<T> clazz, String prefix, String file) {
        // 不能通过RPC创建
        // 原因：很多场景下回使用到配置，包括Guice，如果此时又通过Guice来创建Config，会出现循环调用的问题

        T obj = ClassKits.newInstance(clazz, false);
        Collection<Method> setMethods = ClassKits.getClassSetMethods(clazz);

        if (ArrayKits.isNullOrEmpty(setMethods)) {
            configs.put(clazz.getName() + prefix, obj);
            return obj;
        }

        for (Method method : setMethods) {

            String key = getKeyByMethod(prefix, method);
            String value = getValueByKey(key);

            if (StringKits.isNotBlank(file)) {
                try {
                    Prop prop = PropKit.use(file);
                    String filePropValue = prop.get(key);
                    if (StringKits.isNotBlank(filePropValue)) {
                        value = filePropValue;
                    }
                } catch (Throwable ex) {
                    LOG.warn("Could not find " + file + " in your class path, use jboot.properties to replace. ");
                }
            }

            try {
                if (StringKits.isNotBlank(value)) {
                    Object val = convert(method.getParameterTypes()[0], value);
                    method.invoke(obj, val);
                }
            } catch (Throwable ex) {
                LogKit.error(ex.toString(), ex);
            }
        }

        return obj;
    }

    private String getKeyByMethod(String prefix, Method method) {

        String key = StrKit.firstCharToLowerCase(method.getName().substring(3));

        if (StringKits.isNotBlank(prefix)) {
            key = prefix.trim() + "." + key;
        }

        return key;
    }

    /**
     * 根据 key 获取value的值
     * <p>
     * 优先获取系统启动设置参数
     * 第二 从系统的环境变量中获取
     * 第三 获取本地配置
     *
     * @param key
     * @return
     */
    public String getValueByKey(String key) {

        String value = JbootApplication.getBootArg(key);

        if (StringKits.isBlank(value)) {
            value = System.getenv(key);
        }

        if (StringKits.isBlank(value)) {
            value = (String) mainProperties.get(key);
        }
        return value;
    }


    /**
     * 或者Jboot默认的配置信息
     *
     * @return
     */
    public Properties getProperties() {
        Properties properties = new Properties();

        properties.putAll(mainProperties);

        if (JbootApplication.getBootArgs() != null) {
            for (Map.Entry<String, String> entry : JbootApplication.getBootArgs().entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }

        return properties;
    }


    /**
     * 初始化不同model下的properties文件
     */
    private void initModeProp() {
        String mode = (String) mainProperties.get("jboot.mode");
        if (StringKits.isBlank(mode)) {
            return;
        }

        Prop modeProp = null;
        try {
            String p = String.format("jboot-%s.properties", mode);
            modeProp = PropKit.use(p);
        } catch (Throwable ex) {
        }

        if (modeProp == null) {
            return;
        }

        mainProperties.putAll(modeProp.getProperties());
    }


    /**
     * 数据转化
     *
     * @param type
     * @param s
     * @return
     */
    private static final Object convert(Class<?> type, String s) {

        try {
            return TypeConverter.me().convert(type,s.trim());
        } catch (ParseException e) {
            throw new JbootIllegalConfigException(type.getName() + " can not be converted, please use other type in your config class!");
        }
    }




    public PropInfoMap getPropInfoMap() {
        return propInfoMap;
    }


}
