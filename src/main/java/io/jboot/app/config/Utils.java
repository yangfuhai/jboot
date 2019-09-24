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


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class Utils {


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


    public static List<Method> getClassSetMethods(Class clazz) {
        List<Method> setMethods = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")
                    && method.getName().length() > 3
                    && method.getParameterCount() == 1) {

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
                    String path = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                    path = java.net.URLDecoder.decode(path, "UTF-8");
                    if (path.endsWith(File.separator)) {
                        path = path.substring(0, path.length() - 1);
                    }
                    /**
                     * Fix path带有文件名
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
        return ret != null ? ret : Utils.class.getClassLoader();
    }

    public static void doNothing(Throwable ex) {
    }

    public static final Object convert(Class<?> type, String s) {

        if (type == String.class) {
            return s;
        }

        if (type == Integer.class || type == int.class) {
            return Integer.parseInt(s);
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(s);
        } else if (type == Double.class || type == double.class) {
            return Double.parseDouble(s);
        } else if (type == Float.class || type == float.class) {
            return Float.parseFloat(s);
        } else if (type == Boolean.class || type == boolean.class) {
            String value = s.toLowerCase();
            if ("1".equals(value) || "true".equals(value)) {
                return Boolean.TRUE;
            } else if ("0".equals(value) || "false".equals(value)) {
                return Boolean.FALSE;
            } else {
                throw new RuntimeException("Can not parse to boolean type of value: " + s);
            }
        } else if (type == java.math.BigDecimal.class) {
            return new java.math.BigDecimal(s);
        } else if (type == java.math.BigInteger.class) {
            return new java.math.BigInteger(s);
        } else if (type == byte[].class) {
            return s.getBytes();
        }

        throw new RuntimeException(type.getName() + " can not be converted, please use other type in your config class!");

    }

}
