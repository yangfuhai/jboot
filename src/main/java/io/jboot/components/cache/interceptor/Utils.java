/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.cache.interceptor;

import com.jfinal.template.Engine;
import io.jboot.components.cache.ActionCache;
import io.jboot.components.cache.AopCache;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.db.model.Columns;
import io.jboot.exception.JbootException;
import io.jboot.utils.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class Utils {

    static final Engine ENGINE = new Engine("JbootCacheRenderEngine");

    static {
        ENGINE.addDirective("para", ParaDirective.class);
        ENGINE.addSharedStaticMethod(ParaDirective.class);
    }

    /**
     * use jfinal engine render text
     *
     * @param template
     * @param method
     * @param arguments
     * @return
     */
    static String engineRender(String template, Method method, Object[] arguments) {
        Map<String, Object> datas = new HashMap<>();
        int x = 0;
        for (Parameter p : method.getParameters()) {
            if (!p.isNamePresent()) {
                // 必须通过添加 -parameters 进行编译，才可以获取 Parameter 的编译前的名字
                throw new RuntimeException(" Maven or IDE config is error. see https://jfinal.com/doc/3-3 ");
            }
            datas.put(p.getName(), arguments[x++]);
        }

        return ENGINE.getTemplateByString(template).renderToString(datas);
    }

    static String buildCacheKey(String key, Class<?> clazz, Method method, Object[] arguments) {
        clazz = ClassUtil.getUsefulClass(clazz);

        if (StrUtil.isNotBlank(key)) {
            return renderKey(key, method, arguments);
        }

        StringBuilder keyBuilder = new StringBuilder(clazz.getSimpleName());
        keyBuilder.append('.').append(method.getName());

        if (ArrayUtil.isNullOrEmpty(arguments)) {
            return keyBuilder.append("()").toString();
        }

        Class<?>[] paramTypes = method.getParameterTypes();
        int index = 0;
        for (Object argument : arguments) {
            String argString = convertToString(argument, method);
            if (index == 0) {
                keyBuilder.append("(");
            } else {
                keyBuilder.append(", ");
            }
            keyBuilder.append(paramTypes[index++].getSimpleName())
                    .append(':')
                    .append(argString);

            if (index == arguments.length) {
                keyBuilder.append(")");
            }
        }

        return keyBuilder.toString();
    }

    private static String renderKey(String key, Method method, Object[] arguments) {
        int indexOfStartFlag = key.indexOf("#");
        if (indexOfStartFlag > -1) {
            int indexOfEndFlag = key.indexOf(")");
            if (indexOfEndFlag > indexOfStartFlag) {
                return engineRender(key, method, arguments);
            }
        }

        return key;
    }


    public static void ensureCacheNameNotBlank(Method method, String cacheName) {
        if (StrUtil.isBlank(cacheName)) {
            throw new IllegalStateException("Cache Name must not empty or blank in method: " +
                    ClassUtil.buildMethodString(method));
        }
    }


    static boolean isSupportClass(Class<?> clazz) {
        return clazz == String.class
                || clazz == Integer.class
                || clazz == int.class
                || clazz == Short.class
                || clazz == short.class
                || clazz == Long.class
                || clazz == long.class
                || clazz == Double.class
                || clazz == double.class
                || clazz == Float.class
                || clazz == float.class
                || clazz == Boolean.class
                || clazz == boolean.class
                || clazz == char.class
                || clazz == BigDecimal.class
                || clazz == BigInteger.class
                || clazz == java.util.Date.class
                || clazz == java.sql.Date.class
                || clazz == java.sql.Timestamp.class
                || clazz == java.sql.Time.class
                || clazz == LocalDate.class
                || clazz == LocalDateTime.class
                || clazz == LocalTime.class
                || clazz.isArray()
                || Collection.class.isAssignableFrom(clazz)
                || clazz == Columns.class
                ;

    }

    static String convertToString(Object object, Method method) {
        if (object == null) {
            return "null";
        }

        if (!isSupportClass(object.getClass())) {
            String msg = "Unsupport empty key for annotation @Cacheable, @CacheEvict or @CachePut " +
                    "at method [" + ClassUtil.buildMethodString(method) + "], " +
                    "please config key in the annotation.";
            throw new IllegalArgumentException(msg);
        }

        if (object.getClass().isArray()) {
            StringBuilder ret = new StringBuilder();
            Object[] values = (Object[]) object;
            int index = 0;
            for (Object value : values) {
                if (index == 0) {
                    ret.append('[');
                }
                ret.append(convertToString(value, method));
                if (++index != values.length) {
                    ret.append(',');
                } else {
                    ret.append(']');
                }
            }
            return ret.toString();
        }

        if (object instanceof Collection) {
            Collection<?> c = (Collection<?>) object;
            StringBuilder ret = new StringBuilder();
            int index = 0;
            for (Object o : c) {
                if (index == 0) {
                    ret.append('[');
                }
                ret.append(convertToString(o, method));
                if (++index != c.size()) {
                    ret.append(',');
                } else {
                    ret.append(']');
                }
            }
            return ret.toString();
        }

        if (object instanceof java.util.Date) {
            return String.valueOf(((java.util.Date) object).getTime());
        }

        if (object instanceof LocalDateTime) {
            return String.valueOf(DateUtil.toDate((LocalDateTime) object).getTime());
        }

        if (object instanceof LocalDate) {
            return String.valueOf(DateUtil.toDate((LocalDate) object).getTime());
        }

        if (object instanceof LocalTime) {
            return String.valueOf(DateUtil.toDate((LocalTime) object).getTime());
        }

        if (object instanceof Columns) {
            return ((Columns) object).getCacheKey();
        }

        return String.valueOf(object);
    }


    static boolean isUnless(String unlessString, Method method, Object[] arguments) {
        if (StrUtil.isBlank(unlessString)) {
            return false;
        }

        unlessString = "#(" + unlessString + ")";
        return "true".equals(engineRender(unlessString, method, arguments));
    }


    static void removeCache(Object[] arguments, Class<?> targetClass, Method method, CacheEvict evict, boolean isAction) {
        String unless = AnnotationUtil.get(evict.unless());
        if (Utils.isUnless(unless, method, arguments)) {
            return;
        }

        String cacheName = AnnotationUtil.get(evict.name());
        if (StrUtil.isBlank(cacheName)) {
            throw new JbootException(String.format("CacheEvict.name()  must not empty in method [%s].",
                    ClassUtil.buildMethodString(method)));
        }

        String cacheKey = AnnotationUtil.get(evict.key());

        if (StrUtil.isBlank(cacheKey) || "*".equals(cacheKey.trim())) {
            if (isAction) {
                ActionCache.removeAll(cacheName);
            } else {
                AopCache.removeAll(cacheName);
            }
        } else {
            cacheKey = Utils.buildCacheKey(cacheKey, targetClass, method, arguments);
            if (isAction) {
                ActionCache.remove(cacheName, cacheKey);
            } else {
                AopCache.remove(cacheName, cacheKey);
            }
        }
    }


}
