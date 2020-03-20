/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.rpc;

import io.jboot.Jboot;
import io.jboot.utils.CollectionUtil;
import io.jboot.utils.StrUtil;
import org.apache.dubbo.common.utils.StringUtils;

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
public class Utils {

    public static void appendAnnotation(Class<?> annotationClass, Object annotation, Object appendTo) {
        Method[] methods = annotationClass.getMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass() != Object.class
                    && method.getReturnType() != void.class
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
                        Class<?> parameterType = getBoxedClass(method.getReturnType());
                        if ("filter".equals(property) || "listener".equals(property)) {
                            parameterType = String.class;
                            value = StringUtils.join((String[]) value, ",");
                        } else if ("parameters".equals(property)) {
                            parameterType = Map.class;
                            value = CollectionUtil.string2Map((String) value);
                        }
                        try {
                            Method setterMethod = appendTo.getClass().getMethod(setter, parameterType);
                            setterMethod.invoke(appendTo, value);
                        } catch (NoSuchMethodException e) {
                            // ignore
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
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
                Method method = copyTo.getClass().getDeclaredMethod("set" + StrUtil.firstCharToUpperCase(field.getName()), field.getType());
                method.invoke(copyTo, field.get(copyFrom));
            } catch (Exception e) {
                // ignore
            }
        }
    }


    public static <T, F> void appendChildConfig(Map<String, T> appendTo, Map<String, F> dataFrom, String prefix, String arrName) {
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
                        F fillObj = dataFrom.get(arg);
                        if (fillObj != null) {
                            argCfgList.add(fillObj);
                        }
                    }
                    if (!argCfgList.isEmpty()) {
                        try {
                            //setArguments/setMethods/setRegistries
                            String setterMethodName = arrName.endsWith("registry")
                                    ? "setRegistries"
                                    : "set" + StrUtil.firstCharToUpperCase(arrName) + "s";

                            Method method = entry.getValue().getClass().getDeclaredMethod(setterMethodName, List.class);
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
