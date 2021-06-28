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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 反射相关操作的工具类
 */
public class ReflectUtil {

    public static <T> T getFieldValue(Class<?> dClass, String fieldName) {
        return getFieldValue(dClass, fieldName, null);
    }

    public static <T> T getFieldValue(Class<?> dClass, String fieldName, Object from) {
        try {
            if (StrUtil.isBlank(fieldName)) {
                throw new IllegalArgumentException("fieldName must not be null or empty.");
            }
            Field field = searchField(dClass, f -> f.getName().equals(fieldName));
            if (field == null) {
                throw new NoSuchFieldException(fieldName);
            }
            field.setAccessible(true);
            return (T) field.get(from);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
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
        List<Method> methods = new ArrayList<>();
        doSearchMethodList(dClass, filter, methods);
        return methods;
    }

    private static void doSearchMethodList(Class<?> dClass, Predicate<Method> filter, List<Method> list) {
        if (dClass == null) {
            return;
        }
        Method[] methods = dClass.getDeclaredMethods();
        for (Method method : methods) {
            if (filter.test(method)) {
                list.add(method);
            }
        }
        doSearchMethodList(dClass.getSuperclass(), filter, list);
    }
}
