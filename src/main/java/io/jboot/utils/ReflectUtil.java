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
package io.jboot.utils;

import com.jfinal.kit.LogKit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 反射相关操作的工具类
 */
public class ReflectUtil {

    public static <T> T getStaticFieldValue(Class<?> dClass, String fieldName) {
        return getFieldValue(dClass, fieldName, null);
    }

    public static <T> T getFieldValue(Object getFrom, String fieldName) {
        return getFieldValue(getFrom.getClass(), fieldName, getFrom);
    }

    private static <T> T getFieldValue(Class<?> dClass, String fieldName, Object getFrom) {
        try {
            if (StrUtil.isBlank(fieldName)) {
                throw new IllegalArgumentException("fieldName must not be null or empty.");
            }
            Field field = searchField(dClass, f -> f.getName().equals(fieldName));
            if (field == null) {
                throw new NoSuchFieldException(fieldName);
            }

            return getFileValue(getFrom, field);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T getFileValue(Object getFrom, Field field) {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return (T) field.get(getFrom);
        } catch (IllegalAccessException e) {
            LogKit.error(e.toString(), e);
        } finally {
            field.setAccessible(accessible);
        }
        return null;
    }


    public static void setStaticFieldValue(Class<?> dClass, String fieldName, Object value) {
        setFieldValue(dClass, null, fieldName, value);
    }


    public static void setFieldValue(Object setTo, String fieldName, Object value) {
        setFieldValue(setTo.getClass(), setTo, fieldName, value);
    }


    private static void setFieldValue(Class<?> dClass, Object setTo, String fieldName, Object value) {
        setFieldValue(dClass, setTo, f -> f.getName().equals(fieldName), value);
    }


    private static void setFieldValue(Class<?> dClass, Object setTo, Predicate<Field> filter, Object value) {
        Field field = searchField(dClass, filter);
        if (field == null) {
            throw new IllegalArgumentException("No such field");
        }

        setFieldValue(setTo, value, field);
    }

    public static void setFieldValue(Object setTo, Object value, Field field) {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(setTo, value);
        } catch (IllegalAccessException e) {
            LogKit.error(e.toString(), e);
        } finally {
            field.setAccessible(accessible);
        }
    }


    public static Field searchField(Class<?> dClass, Predicate<Field> filter) {
        if (dClass == null) {
            return null;
        }
        Field[] fields = dClass.getDeclaredFields();
        for (Field field : fields) {
            if (filter.test(field)) {
                return field;
            }
        }
        return searchField(dClass.getSuperclass(), filter);
    }


    public static List<Field> searchFieldList(Class<?> dClass, Predicate<Field> filter) {
        List<Field> fields = new LinkedList<>();
        doSearchFieldList(dClass, filter, fields);
        return fields;
    }


    private static void doSearchFieldList(Class<?> dClass, Predicate<Field> filter, List<Field> searchToList) {
        if (dClass == null || dClass == Object.class) {
            return;
        }

        Field[] fields = dClass.getDeclaredFields();
        if (fields.length > 0) {
            if (filter != null) {
                for (Field field : fields) {
                    if (filter.test(field)) {
                        searchToList.add(field);
                    }
                }
            } else {
                searchToList.addAll(Arrays.asList(fields));
            }
        }

        doSearchFieldList(dClass.getSuperclass(), filter, searchToList);
    }


    public static Method searchMethod(Class<?> dClass, Predicate<Method> filter) {
        if (dClass == null) {
            return null;
        }
        Method[] methods = dClass.getDeclaredMethods();
        for (Method method : methods) {
            if (filter.test(method)) {
                return method;
            }
        }
        return searchMethod(dClass.getSuperclass(), filter);
    }


    public static List<Method> searchMethodList(Class<?> dClass, Predicate<Method> filter) {
        List<Method> methods = new LinkedList<>();
        doSearchMethodList(dClass, filter, methods);
        return methods;
    }


    private static void doSearchMethodList(Class<?> dClass, Predicate<Method> filter, List<Method> searchToList) {
        if (dClass == null) {
            return;
        }
        Method[] methods = dClass.getDeclaredMethods();
        if (methods.length > 0) {
            if (filter != null) {
                for (Method method : methods) {
                    if (filter.test(method)) {
                        searchToList.add(method);
                    }
                }
            } else {
                searchToList.addAll(Arrays.asList(methods));
            }
        }

        doSearchMethodList(dClass.getSuperclass(), filter, searchToList);
    }


    public static <T> T invokeStaticMethod(Class<?> dClass, String methodName, Object... args) {
        return invokeStaticMethod(dClass, m -> m.getName().equals(methodName), args);
    }


    public static <T> T invokeStaticMethod(Class<?> dClass, Predicate<Method> filter, Object... args) {
        Method method = searchMethod(dClass, filter);
        if (method == null) {
            throw new IllegalArgumentException("No such method.");
        }
        return invokeMethod(null, method, args);
    }


    public static <T> T invokeMethod(Object obj, String methodName, Object... args) {
        return invokeMethod(obj, m -> m.getName().equals(methodName), args);
    }


    public static <T> T invokeMethod(Object obj, Predicate<Method> filter, Object... args) {
        Method method = searchMethod(obj.getClass(), filter);
        if (method == null) {
            throw new IllegalArgumentException("No such method.");
        }
        return invokeMethod(obj, method, args);
    }


    public static <T> T invokeMethod(Object obj, Method method, Object... args) {
        final boolean accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            return (T) method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LogKit.error(e.toString(), e);
        } finally {
            method.setAccessible(accessible);
        }
        return null;
    }

}
