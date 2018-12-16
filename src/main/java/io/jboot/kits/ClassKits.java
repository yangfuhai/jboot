/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.kits;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.log.Log;
import io.jboot.Jboot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类实例创建者创建者
 * Created by michael on 17/3/21.
 */
public class ClassKits {

    public static Log log = Log.getLog(ClassKits.class);
    private static final Map<Class, Object> singletons = new ConcurrentHashMap<>();


    /**
     * 获取单例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T singleton(Class<T> clazz) {
        Object object = singletons.get(clazz);
        if (object == null) {
            synchronized (clazz) {
                object = singletons.get(clazz);
                if (object == null) {
                    object = newInstance(clazz);
                    if (object != null) {
                        singletons.put(clazz, object);
                    } else {
                        Log.getLog(clazz).error("cannot new newInstance!!!!");
                    }

                }
            }
        }

        return (T) object;
    }

    /**
     * 创建新的实例
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> T newInstance(Class<T> clazz) {
        return newInstance(clazz, true);
    }


    public static <T> T newInstance(Class<T> clazz, boolean createByGuice) {
        if (createByGuice) {
            return Jboot.bean(clazz);
        } else {
            try {
                Constructor constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return (T) constructor.newInstance();
            } catch (Exception e) {
                log.error("can not newInstance class:" + clazz + "\n" + e.toString(), e);
            }

            return null;
        }
    }

    /**
     * 创建新的实例
     *
     * @param <T>
     * @param clazzName
     * @return
     */
    public static <T> T newInstance(String clazzName) {
        return newInstance(clazzName, true);
    }

    /**
     * 创建新的实例
     *
     * @param <T>
     * @param clazzName
     * @return
     */
    public static <T> T newInstance(String clazzName, boolean createByGuice) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(clazzName, false, Thread.currentThread().getContextClassLoader());
            return newInstance(clazz, createByGuice);
        } catch (Exception e) {
            log.error("can not newInstance class:" + clazzName + "\n" + e.toString(), e);
        }

        return null;
    }


    public static Class<?> getUsefulClass(Class<?> clazz) {
        //ControllerTest$ServiceTest$$EnhancerByGuice$$40471411#hello
        //com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158
        return clazz.getName().indexOf("$$EnhancerBy") == -1 ? clazz : clazz.getSuperclass();
    }


    /**
     * 类的set方法缓存，用于减少对类的反射工作
     */
    private static Multimap<Class<?>, Method> classMethodsCache = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    /**
     * 获取 某class 下的所有set 方法
     *
     * @param clazz
     * @return
     */
    public static Collection<Method> getClassSetMethods(Class clazz) {

        Collection<Method> setMethods = classMethodsCache.get(clazz);
        if (ArrayUtils.isNullOrEmpty(setMethods)) {
            initSetMethodsCache(clazz);
            setMethods = classMethodsCache.get(clazz);
        }

        return setMethods != null ? new ArrayList<>(setMethods) : null;
    }

    private static void initSetMethodsCache(Class clazz) {
        synchronized (clazz) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("set")
                        && method.getName().length() > 3
                        && method.getParameterCount() == 1) {

                    classMethodsCache.put(clazz, method);
                }
            }
        }
    }


}
