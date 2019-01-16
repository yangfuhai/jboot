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
package io.jboot.aop.interceptor.cache;

import com.jfinal.log.Log;
import com.jfinal.template.Engine;
import io.jboot.Jboot;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.exception.JbootException;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class Utils {


    static final Log LOG = Log.getLog(Utils.class);
    static final Engine ENGINE = new Engine("JbootCacheRender");

    /**
     * use jfinal engine render text
     *
     * @param template
     * @param method
     * @param arguments
     * @return
     */
    static String engineRender(String template, Method method, Object[] arguments) {

        Map<String, Object> datas = new HashMap();
        int x = 0;
        /**
         * 在java8下，通过添加 -parameters 进行编译，可以获取 Parameter 的编译前的名字
         * 否则 只能获取 编译后的名字
         */
        for (Parameter p : method.getParameters()) {
            if (!p.isNamePresent()) {
                LOG.warn(" Maven or IDE config is error. see http://www.jfinal.com/doc/3-3 ");
                break;
            }
            datas.put(p.getName(), arguments[x++]);
        }

        return ENGINE.getTemplateByString(template).renderToString(datas);

    }

    static String buildCacheKey(String key, Class clazz, Method method, Object[] arguments) {

        clazz = ClassUtil.getUsefulClass(clazz);

        if (StrUtil.isNotBlank(key)) {
            return renderKey(key, method, arguments);
        }

        StringBuilder keyBuilder = new StringBuilder(clazz.getName());
        keyBuilder.append("#").append(method.getName());

        if (ArrayUtil.isNullOrEmpty(arguments)) {
            return keyBuilder.toString();
        }

        Class[] paramTypes = method.getParameterTypes();
        int index = 0;
        for (Object argument : arguments) {
            String argStr = converteToString(argument);
            ensureArgumentNotNull(argStr, clazz, method);
            keyBuilder
                    .append(paramTypes[index++].getClass().getName())
                    .append(":")
                    .append(argStr)
                    .append("-");
        }

        //remove last chat '-'
        return keyBuilder.deleteCharAt(keyBuilder.length() - 1).toString();

    }

    private static String renderKey(String key, Method method, Object[] arguments) {
        if (!key.contains("#(") || !key.contains(")")) {
            return key;
        }

        return engineRender(key, method, arguments);
    }

    private static void ensureArgumentNotNull(String argument, Class clazz, Method method) {
        if (argument == null) {
            throw new JbootException("not support empty key for annotation @Cacheable, @CacheEvict or @CachePut " +
                    "at method[" + clazz.getName() + "." + method.getName() + "()] " +
                    "with argument class:" + argument.getClass().getName() + ", " +
                    "please config key properties in @Cacheable, @CacheEvict or @CachePut annotation.");
        }
    }


    static boolean isPrimitive(Class clazz) {
        return clazz == String.class
                || clazz == Integer.class
                || clazz == int.class
                || clazz == Long.class
                || clazz == long.class
                || clazz == Double.class
                || clazz == double.class
                || clazz == Float.class
                || clazz == float.class
                || clazz == Boolean.class
                || clazz == boolean.class
                || clazz == BigDecimal.class
                || clazz == BigInteger.class
                || clazz == java.util.Date.class
                || clazz == java.sql.Date.class
                || clazz == java.sql.Timestamp.class
                || clazz == java.sql.Time.class;

    }

    static String converteToString(Object object) {
        if (object == null) {
            return "null";
        }
        if (!isPrimitive(object.getClass())) {
            return null;
        }

        if (object instanceof java.util.Date) {
            return String.valueOf(((java.util.Date) object).getTime());
        }

        if (object instanceof java.sql.Date) {
            return String.valueOf(((java.sql.Date) object).getTime());
        }
        if (object instanceof java.sql.Timestamp) {
            return String.valueOf(((java.sql.Timestamp) object).getTime());
        }
        if (object instanceof java.sql.Time) {
            return String.valueOf(((java.sql.Time) object).getTime());
        }

        return String.valueOf(object);

    }


    static boolean isUnless(String unlessString, Method method, Object[] arguments) {

        if (StrUtil.isBlank(unlessString)) {
            return false;
        }

        String template = new StringBuilder("#(")
                .append(unlessString)
                .append(")")
                .toString();

        String unlessBoolString = engineRender(template,
                method, arguments);

        return "true".equals(unlessBoolString);
    }


    static void doCacheEvict(Object[] arguments, Class targetClass, Method method, CacheEvict evict) {
        String unless = AnnotationUtil.get(evict.unless());
        if (Utils.isUnless(unless, method, arguments)) {
            return;
        }

        String cacheName = AnnotationUtil.get(evict.name());
        if (StrUtil.isBlank(cacheName)) {
            throw new JbootException(String.format("CacheEvict.name()  must not empty in method [%s].",
                    ClassUtil.getUsefulClass(targetClass).getName() + "." + method.getName()));
        }

        String cacheKey = AnnotationUtil.get(evict.key());

        if ("*".equals(cacheKey)) {
            Jboot.getCache().removeAll(cacheName);
            return;
        }

        cacheKey = Utils.buildCacheKey(cacheKey, targetClass, method, arguments);
        Jboot.getCache().remove(cacheName, cacheKey);
    }


    public static void main(String[] args) {

        Map<String, Object> datas = new HashMap();
        datas.put("key", "value");

        String template = "#(key)";

        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            ENGINE.getTemplateByString(template).renderToString(datas);
        }

        System.out.println("time : " + (System.currentTimeMillis() - time));
    }

}
