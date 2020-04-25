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


import io.jboot.utils.StrUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigUtil {


    public static boolean isBlank(String string) {
        return string == null || string.trim().equals("");
    }

    public static boolean isNotBlank(String string) {
        return !isBlank(string);
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ConfigPart> parseParts(String string) {
        if (StrUtil.isBlank(string)) {
            return null;
        }
        List<ConfigPart> configParts = new LinkedList<>();
        char[] chars = string.toCharArray();
        ConfigPart part = null;
        int index = 0;
        for (char c : chars) {
            //第一个字符是 '{' 会出现 ArrayIndexOutOfBoundsException 错误
            if (c == '{' && index > 0 && chars[index - 1] == '$' && part == null) {
                part = new ConfigPart();
                part.setStart(index);
            } else if (c == '}' && part != null) {
                part.setEnd(index);
                configParts.add(part);
                part = null;
            } else if (part != null) {
                part.append(c);
                if (c == ':' && part.getKeyValueIndexOf() == 0) {
                    part.setKeyValueIndexOf(index - part.getStart());
                }
            }
            index++;
        }
        return configParts;
    }


    public static List<Method> getClassSetMethods(Class clazz) {
        List<Method> setMethods = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")
                    && Character.isUpperCase(method.getName().charAt(3))
                    && method.getName().length() > 3
                    && method.getParameterCount() == 1
                    && Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())) {

                setMethods.add(method);
            }
        }
        return setMethods;
    }

    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }


    private static String rootClassPath;

    public static String getRootClassPath() {
        if (rootClassPath == null) {
            try {
                String path = getClassLoader().getResource("").toURI().getPath();
                rootClassPath = new File(path).getAbsolutePath();
            } catch (Exception e) {
                try {
                    String path = ConfigUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                    path = java.net.URLDecoder.decode(path, "UTF-8");
                    if (path.endsWith(File.separator)) {
                        path = path.substring(0, path.length() - 1);
                    }
                    /**
                     * Fix path 带有文件名
                     */
                    if (path.endsWith(".jar")) {
                        path = path.substring(0, path.lastIndexOf("/") + 1);
                    }
                    rootClassPath = path;
                } catch (UnsupportedEncodingException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        return rootClassPath;
    }


    public static ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : ConfigUtil.class.getClassLoader();
    }

    public static void doNothing(Throwable ex) {
    }

    public static final Object convert(Class<?> convertClass, String s, Type genericType) {

        if (convertClass == String.class || s == null) {
            return s;
        }

        if (convertClass == Integer.class || convertClass == int.class) {
            return Integer.parseInt(s);
        } else if (convertClass == Long.class || convertClass == long.class) {
            return Long.parseLong(s);
        } else if (convertClass == Double.class || convertClass == double.class) {
            return Double.parseDouble(s);
        } else if (convertClass == Float.class || convertClass == float.class) {
            return Float.parseFloat(s);
        } else if (convertClass == Boolean.class || convertClass == boolean.class) {
            String value = s.toLowerCase();
            if ("1".equals(value) || "true".equals(value)) {
                return Boolean.TRUE;
            } else if ("0".equals(value) || "false".equals(value)) {
                return Boolean.FALSE;
            } else {
                throw new RuntimeException("Can not parse to boolean type of value: " + s);
            }
        } else if (convertClass == java.math.BigDecimal.class) {
            return new java.math.BigDecimal(s);
        } else if (convertClass == java.math.BigInteger.class) {
            return new java.math.BigInteger(s);
        } else if (convertClass == byte[].class) {
            return s.getBytes();
        } else if (Map.class.isAssignableFrom(convertClass)) {
            if (!s.contains(":") || !genericClassCheck(genericType)) {
                return null;
            } else {
                Map map = convertClass == ConcurrentHashMap.class ? new ConcurrentHashMap() : new HashMap();
                String[] strings = s.split(",");
                for (String kv : strings) {
                    int indexOf = kv.indexOf(":");
                    if (indexOf > 0 && indexOf < kv.trim().length() - 1) {
                        map.put(kv.substring(0, indexOf).trim(), kv.substring(indexOf + 1).trim());
                    }
                }
                return map;
            }
        } else if (List.class.isAssignableFrom(convertClass)) {
            if (genericClassCheck(genericType)) {
                List list = LinkedList.class == convertClass ? new LinkedList() : new ArrayList();
                String[] strings = s.split(",");
                for (String s1 : strings) {
                    if (s != null && s1.trim().length() > 0) {
                        list.add(s1.trim());
                    }
                }
                return list;
            } else {
                return null;
            }
        } else if (Set.class.isAssignableFrom(convertClass)) {
            if (genericClassCheck(genericType)) {
                Set set = LinkedHashSet.class == convertClass ? new LinkedHashSet() : new HashSet();
                String[] strings = s.split(",");
                for (String s1 : strings) {
                    if (s != null && s1.trim().length() > 0) {
                        set.add(s1.trim());
                    }
                }
                return set;
            } else {
                return null;
            }
        } else if (convertClass.isArray() && convertClass.getComponentType() == String.class) {
            List<String> list = new LinkedList();
            String[] strings = s.split(",");
            if (strings != null && strings.length > 0) {
                for (String s1 : strings) {
                    if (s1 != null && s1.trim().length() != 0) {
                        list.add(s1.trim());
                    }
                }
            }
            return list.toArray(new String[0]);
        } else if (Class.class == convertClass) {
            try {
                return Class.forName(s, false, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        throw new RuntimeException(convertClass.getName() + " can not be converted, please use other type in your config class!");

    }

    /**
     * 对泛型类型进行检测，只支持 String 类型的泛型，或者不是泛型才会支持
     *
     * @param type
     * @return
     */
    private static boolean genericClassCheck(Type type) {
        if (type instanceof ParameterizedType) {
            for (Type at : ((ParameterizedType) type).getActualTypeArguments()) {
                if (String.class != at) {
                    return false;
                }
            }
        }
        return true;
    }

}
