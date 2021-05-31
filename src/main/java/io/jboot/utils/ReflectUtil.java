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
            Field field = searchField(dClass, fieldName);
            if (field == null) {
                throw new NoSuchFieldException(fieldName);
            }
            field.setAccessible(true);
            return (T) field.get(from);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private static Field searchField(Class<?> dClass, String fieldName) {
        if (dClass == null) {
            return null;
        }
        Field[] fields = dClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return searchField(dClass.getSuperclass(), fieldName);
    }
}
