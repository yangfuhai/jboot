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
package io.jboot.config;

import com.jfinal.kit.*;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.config.annotation.PropertieConfig;
import io.jboot.exception.JbootException;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置管理类
 * <p>
 * 用于读取配置信息，包括本地配置信息和分布式远程配置信息
 */
public class JbootConfigManager {

    private static JbootConfigManager me = new JbootConfigManager();

    public static JbootConfigManager me() {
        return me;
    }

    private JbootConfigConfig config;
    private Prop jbootProp;

    private PropInfos propInfos = new PropInfos();

    private ConfigFileScanner configFileScanner;
    private ConfigRemoteReader configRemoteReader;


    private ConcurrentHashMap<String, Object> configs = new ConcurrentHashMap<>();
    private static final Log log = Log.getLog(JbootConfigManager.class);


    private Map<String, Method> keyMethod = new HashMap<>();
    private Map<Method, List<Object>> methodObjects = new HashMap<>();
    private Map<Class<?>, List<Method>> classSetMethods = new HashMap<>();


    public JbootConfigManager() {
        config = get(JbootConfigConfig.class);
        jbootProp = PropKit.use("jboot.properties");
        initModeProp(jbootProp);
    }


    public void init() {
        if (config.isServerEnable()) {
            initConfigFileScanner();
        }

        /**
         * 定时获取远程服务配置信息
         */
        else if (config.isRemoteEnable()) {
            initConfigRemoteReader();
        }
    }


    public <T> T get(Class<T> clazz) {
        PropertieConfig propertieConfig = clazz.getAnnotation(PropertieConfig.class);
        if (propertieConfig == null) {
            return get(clazz, null);
        }
        return get(clazz, propertieConfig.prefix());
    }


    /**
     * 获取配置信息，并创建和赋值clazz实例
     *
     * @param clazz  指定的类
     * @param prefix 配置文件前缀
     * @param <T>
     * @return
     */
    public <T> T get(Class<T> clazz, String prefix) {

        T obj = (T) configs.get(clazz.getName() + prefix);
        if (obj != null) {
            return obj;
        }

        obj = ClassNewer.newInstance(clazz);
        List<Method> setMethods = getSetMethods(clazz);

        for (Method method : setMethods) {

            String key = getKeyByMethod(prefix, method);
            String value = getValueByKey(key);

            keyMethod.put(key, method);

            List<Object> objects = methodObjects.get(method);
            if (objects == null) {
                objects = new ArrayList<>();
                methodObjects.put(method, objects);
            }
            objects.add(obj);

            try {
                if (StringUtils.isNotBlank(value)) {
                    Object val = convert(method.getParameterTypes()[0], value);
                    method.invoke(obj, val);
                }
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }

        configs.put(clazz.getName() + prefix, obj);

        return obj;
    }

    private String getKeyByMethod(String prefix, Method method) {

        String key = StrKit.firstCharToLowerCase(method.getName().substring(3));

        if (StringUtils.isNotBlank(prefix)) {
            key = prefix.trim() + "." + key;
        }

        return key;
    }

    /**
     * 根据 key 获取value的值
     * <p>
     * 优先获取系统启动设置参数
     * 第二 获取远程配置
     * 第三 获取本地配置
     *
     * @param key
     * @return
     */
    private String getValueByKey(String key) {

        String value = Jboot.getBootArg(key);

        if (StringUtils.isBlank(value) && configRemoteReader != null) {
            value = (String) configRemoteReader.getRemoteProperties().get(key);
        }


        if (StringUtils.isBlank(value)) {
            value = jbootProp.get(key);
        }
        return value;
    }


    private List<Method> getSetMethods(Class clazz) {
        List<Method> setMethods = classSetMethods.get(clazz);
        if (setMethods == null) {
            setMethods = new ArrayList<>();

            Method[] methods = clazz.getMethods();
            if (ArrayUtils.isNotEmpty(methods)) {
                for (Method m : methods) {
                    if (m.getName().startsWith("set") && m.getName().length() > 3 && m.getParameterCount() == 1) {
                        setMethods.add(m);
                    }
                }
            }

            classSetMethods.put(clazz, setMethods);
        }

        return setMethods;
    }

    /**
     * 或者Jboot默认的配置信息
     *
     * @return
     */
    public Properties getProperties() {
        Properties properties = new Properties();

        properties.putAll(jbootProp.getProperties());

        if (configRemoteReader != null) {
            properties.putAll(configRemoteReader.getRemoteProperties());
        }

        if (Jboot.getBootArgs() != null) {
            for (Map.Entry<String, String> entry : Jboot.getBootArgs().entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }

        return properties;
    }


    /**
     * 初始化不同model下的properties文件
     *
     * @param prop
     */
    private void initModeProp(Prop prop) {
        String mode = PropKit.use("jboot.properties").get("jboot.mode");
        if (StringUtils.isBlank(mode)) {
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

        prop.getProperties().putAll(modeProp.getProperties());
    }


    /**
     * 数据转化
     *
     * @param type
     * @param s
     * @return
     */
    private static final Object convert(Class<?> type, String s) {

        if (type == String.class) {
            return s;
        }


        // mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
        if (type == Integer.class || type == int.class) {
            return Integer.parseInt(s);
        }

        // mysql type: bigint
        if (type == Long.class || type == long.class) {
            return Long.parseLong(s);
        }


        // mysql type: real, double
        if (type == Double.class || type == double.class) {
            return Double.parseDouble(s);
        }

        // mysql type: float
        if (type == Float.class || type == float.class) {
            return Float.parseFloat(s);
        }

        // mysql type: bit, tinyint(1)
        if (type == Boolean.class || type == boolean.class) {
            String value = s.toLowerCase();
            if ("1".equals(value) || "true".equals(value)) {
                return Boolean.TRUE;
            } else if ("0".equals(value) || "false".equals(value)) {
                return Boolean.FALSE;
            } else {
                throw new RuntimeException("Can not parse to boolean type of value: " + s);
            }
        }

        // mysql type: decimal, numeric
        if (type == java.math.BigDecimal.class) {
            return new java.math.BigDecimal(s);
        }

        // mysql type: unsigned bigint
        if (type == java.math.BigInteger.class) {
            return new java.math.BigInteger(s);
        }

        // mysql type: binary, varbinary, tinyblob, blob, mediumblob, longblob. I have not finished the test.
        if (type == byte[].class) {
            return s.getBytes();
        }

        throw new JbootException(type.getName() + " can not be converted, please use other type in your config class!");
    }


    private void initConfigRemoteReader() {
        configRemoteReader = new ConfigRemoteReader(config.getRemoteUrl(), 5) {
            @Override
            public void onChange(String key, String oldValue, String value) {
                if (Jboot.me().isDevMode()) {
                    System.out.println("remote change ---key:" + key + " ---oldValue:" + oldValue + " ---newValue:" + value);
                }

                /**
                 * 过滤掉系统启动参数设置
                 */
                if (Jboot.getBootArg(key) != null) {
                    return;
                }

                Method method = keyMethod.get(key);
                if (method == null) {
                    log.warn("can not set value to config object when get value from remote， key:" + key + "---value:" + value);
                    return;
                }

                List<Object> objects = methodObjects.get(method);
                try {
                    for (Object obj : objects) {
                        if (StringUtils.isBlank(value)) {
                            method.invoke(obj, new Object[]{null});
                        } else {
                            Object val = convert(method.getParameterTypes()[0], value);
                            method.invoke(obj, val);
                        }
                    }
                } catch (Throwable ex) {
                    log.error(ex.toString(), ex);
                }
            }
        };
        configRemoteReader.start();
    }


    private void initConfigFileScanner() {
        configFileScanner = new ConfigFileScanner(config.getPath(), 5) {
            @Override
            public void onChange(String action, String file) {
                switch (action) {
                    case ConfigFileScanner.ACTION_ADD:
                        propInfos.put(HashKit.md5(file), new PropInfos.PropInfo(new File(file)));
                        break;
                    case ConfigFileScanner.ACTION_DELETE:
                        propInfos.remove(HashKit.md5(file));
                        break;
                    case ConfigFileScanner.ACTION_UPDATE:
                        propInfos.put(HashKit.md5(file), new PropInfos.PropInfo(new File(file)));
                        break;
                }
            }
        };

        configFileScanner.start();
    }

    public PropInfos getPropInfos() {
        return propInfos;
    }

    public void destroy() {
        if (configRemoteReader != null) {
            configRemoteReader.stop();
        }
        if (configFileScanner != null) {
            configFileScanner.stop();
        }
    }

}
