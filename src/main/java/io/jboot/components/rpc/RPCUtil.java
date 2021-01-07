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
package io.jboot.components.rpc;

import io.jboot.Jboot;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.CollectionUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/20
 */
public class RPCUtil {

    /**
     * 根据注解来设置对象内容，参考 dubbo 下的 AbstractConfig
     * 参考 {{@org.apache.dubbo.config.AbstractConfig#appendAnnotation }}
     *
     * @param annotationClass
     * @param annotation
     * @param appendTo
     */
    public static void appendAnnotation(Class<?> annotationClass, Object annotation, Object appendTo) {
        Method[] methods = annotationClass.getMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass() != Object.class
                    && method.getReturnType() != void.class
                    && !"toString".equals(method.getName())
                    && !"hashCode".equals(method.getName())
                    && !"annotationType".equals(method.getName())
                    && method.getParameterTypes().length == 0
                    && Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())) {
                try {
                    String property = method.getName();
                    if ("interfaceClass".equals(property) || "interfaceName".equals(property)) {
                        property = "interface";
                    }
                    String setter = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
                    Object value = method.invoke(annotation);
                    if (value != null && !value.equals(method.getDefaultValue())) {
                        Method setterMethod = null;
                        if ("filter".equals(property) || "listener".equals(property) || "registry".equals(property)) {
                            value = StrUtil.join((String[]) value, ",");
                            setterMethod = getMethod(appendTo.getClass(), setter, String.class);
                        } else if ("parameters".equals(property)) {
                            value = CollectionUtil.string2Map((String) value);
                            setterMethod = getMethod(appendTo.getClass(), setter, Map.class);
                        } else {
                            setterMethod = getMethod(appendTo.getClass(), setter, method.getReturnType());
                        }

                        //fixed : 值内容有 ${} 不生效的问题
                        if (value instanceof String) {
                            value = AnnotationUtil.get((String) value);
                        }

                        if (setterMethod != null) {
                            setterMethod.invoke(appendTo, value);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    /**
     * copy object field value to other
     *
     * @param copyFrom
     * @param copyTo
     */
    public static void copyFields(Object copyFrom, Object copyTo) {
        Field[] fields = copyFrom.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                String setterName = "set" + StrUtil.firstCharToUpperCase(field.getName());
                Method method = getMethod(copyTo.getClass(), setterName, field.getType());

                if (method != null) {
                    field.setAccessible(true);
                    Object value = field.get(copyFrom);
                    if (value != null && !value.equals("0") && !value.equals("")) {
                        method.invoke(copyTo, value);
                    }
                }
            } catch (Exception ex) {
                // ignore
            }
        }
    }


    private static Method getMethod(Class clazz, String methodName, Class type) {
        try {
            return clazz.getMethod(methodName, getBoxedClass(type));
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getMethod(methodName, type);
            } catch (NoSuchMethodException ex) {
            }
        }

        return null;
    }


    /**
     * 设置子节点配置，比如 ProviderConfig 下的 MethodsConfig ，或者 MethodConfig 下的 ArgumentConfig 等
     *
     * @param appendTo   要设置的对象
     * @param dataSource 设置子节点的数据源
     * @param prefix     要设置对象的配置前缀（jboot.properties 下的配置）
     * @param arrName    要设置对象的属性名
     * @param <T>
     * @param <F>
     */
    public static <T, F> void setChildConfig(Map<String, T> appendTo, Map<String, F> dataSource, String prefix, String arrName) {
        if (appendTo != null && !appendTo.isEmpty()) {
            for (Map.Entry<String, T> entry : appendTo.entrySet()) {

                String configKey = "default".equals(entry.getKey())
                        ? prefix + "." + arrName //"jboot.rpc.dubbo.method.argument"
                        : prefix + "." + entry.getKey() + "." + arrName;//"jboot.rpc.dubbo.method."+entry.getKey()+".argument";

                String configValue = Jboot.configValue(configKey);
                if (StrUtil.isNotBlank(configValue)) {
                    List<F> argCfgList = new ArrayList<>();
                    Set<String> arguments = StrUtil.splitToSetByComma(configValue);
                    for (String arg : arguments) {
                        F fillObj = dataSource.get(arg);
                        if (fillObj != null) {
                            argCfgList.add(fillObj);
                        }
                    }
                    if (!argCfgList.isEmpty()) {
                        try {
                            //setArguments/setMethods/setRegistries
                            String setterMethodName = arrName.equals("registry")
                                    ? "setRegistries"
                                    : "set" + StrUtil.firstCharToUpperCase(arrName) + "s";

                            Method method = entry.getValue().getClass().getMethod(setterMethodName, List.class);
                            method.invoke(entry.getValue(), argCfgList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static Class<?> getBoxedClass(Class<?> c) {
        if (c == int.class) {
            c = Integer.class;
        } else if (c == boolean.class) {
            c = Boolean.class;
        } else if (c == long.class) {
            c = Long.class;
        } else if (c == float.class) {
            c = Float.class;
        } else if (c == double.class) {
            c = Double.class;
        } else if (c == char.class) {
            c = Character.class;
        } else if (c == byte.class) {
            c = Byte.class;
        } else if (c == short.class) {
            c = Short.class;
        }
        return c;
    }
}
