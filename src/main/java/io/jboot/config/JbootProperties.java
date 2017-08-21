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


import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.config.annotation.PropertieConfig;
import io.jboot.exception.JbootException;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置文件读取工具类
 */
public class JbootProperties {


    private static ConcurrentHashMap<String, Object> configs = new ConcurrentHashMap<>();
    private static Prop jbootProp;
    public static final Log log = Log.getLog(JbootProperties.class);

    public static <T> T get(Class<T> clazz) {
        PropertieConfig propertieConfig = clazz.getAnnotation(PropertieConfig.class);
        if (propertieConfig == null) {
            return get(null, clazz, null);
        }
        return get(propertieConfig.file(), clazz, propertieConfig.prefix());
    }

    public static <T> T get(Class<T> clazz, String prefix) {
        return get(null, clazz, prefix);
    }


    /**
     * 获取配置信息，并创建和赋值clazz实例
     *
     * @param propFile 除了jboot.properties文件以外的扩展文件，并优先读取扩展文件
     * @param clazz    指定的类
     * @param prefix   配置文件前缀
     * @param <T>
     * @return
     */
    public static <T> T get(String propFile, Class<T> clazz, String prefix) {

        Object obj = configs.get(clazz.getName() + prefix);

        if (obj != null) {
            return (T) obj;
        }

        obj = ClassNewer.newInstance(clazz);


        Prop prop = getJbootProp();
        if (propFile != null) {
            Prop configProp = PropKit.use(propFile);
            prop.getProperties().putAll(configProp.getProperties());
        }

        List<Method> setMethods = new ArrayList<>();

        Method[] methods = obj.getClass().getMethods();
        if (ArrayUtils.isNotEmpty(methods)) {
            for (Method m : methods) {
                if (m.getName().startsWith("set") && m.getName().length() > 3 && m.getParameterCount() == 1) {
                    setMethods.add(m);
                }
            }
        }


        for (Method method : setMethods) {
            try {

                String key = StrKit.firstCharToLowerCase(method.getName().substring(3));

                if (StringUtils.isNotBlank(prefix)) {
                    key = prefix.trim() + "." + key;
                }

                String value = Jboot.getBootArg(key);
                if (StringUtils.isBlank(value)) {
                    value = prop.get(key);
                }

                if (StringUtils.isNotBlank(value)) {
                    Object val = convert(method.getParameterTypes()[0], value);
                    method.invoke(obj, val);
                }
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }

        configs.put(clazz.getName() + prefix, obj);

        return (T) obj;
    }

    /**
     * 或者Jboot默认的配置信息
     *
     * @return
     */
    public static Prop getJbootProp() {
        if (jbootProp == null) {
            jbootProp = PropKit.use("jboot.properties");
            initModeProp(jbootProp, "jboot.properties");
        }

        return jbootProp;
    }


    /**
     * 初始化不同model下的properties文件
     *
     * @param prop
     * @param file
     */
    private static void initModeProp(Prop prop, String file) {
        String mode = PropKit.use("jboot.properties").get("jboot.mode");
        if (StringUtils.isBlank(mode)) {
            return;
        }

        Prop modeProp = null;
        try {
            String p = String.format("%s-%s.properties", file.substring(0, file.lastIndexOf(".")), mode);
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


}
