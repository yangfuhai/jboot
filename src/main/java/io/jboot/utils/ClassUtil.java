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
package io.jboot.utils;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import io.jboot.aop.annotation.StaticConstruct;
import io.jboot.exception.JbootException;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类实例创建者创建者
 * Created by michael on 17/3/21.
 */
public class ClassUtil {

    private static Log LOG = Log.getLog(ClassUtil.class);
    private static final Map<Class, Object> singletons = new ConcurrentHashMap<>();


    /**
     * 获取单例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T singleton(Class<T> clazz) {
        return singleton(clazz, true);
    }


    /**
     * 获取单利
     *
     * @param clazz
     * @param createByAop
     * @param <T>
     * @return
     */
    public static <T> T singleton(Class<T> clazz, boolean createByAop) {
        Object ret = singletons.get(clazz);
        if (ret == null) {
            synchronized (clazz) {
                ret = singletons.get(clazz);
                if (ret == null) {
                    ret = newInstance(clazz, createByAop);
                    if (ret != null) {
                        singletons.put(clazz, ret);
                    } else {
                        LOG.error("cannot new newInstance!!!!");
                    }
                }
            }
        }
        return (T) ret;
    }

    public static <T> T singleton(Class<T> clazz, boolean createByAop, boolean inject) {
        Object ret = singletons.get(clazz);
        if (ret == null) {
            synchronized (clazz) {
                ret = singletons.get(clazz);
                if (ret == null) {
                    ret = newInstance(clazz, createByAop);
                    if (ret != null) {
                        if (inject && !createByAop) {
                            Aop.inject(ret);
                        }
                        singletons.put(clazz, ret);
                    } else {
                        LOG.error("cannot new newInstance!!!!");
                    }
                }
            }
        }
        return (T) ret;
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


    /**
     * 创建新的实例，并传入初始化参数
     *
     * @param clazz
     * @param paras
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> clazz, Object... paras) {
        return newInstance(clazz, true, paras);
    }


    /**
     * 是否通过 AOP 来实例化
     *
     * @param clazz
     * @param createByAop
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> clazz, boolean createByAop) {
        if (createByAop) {
            return Aop.get(clazz);
        } else {
            try {
                Constructor constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return (T) constructor.newInstance();
            } catch (Exception e) {
                LOG.error("can not newInstance class:" + clazz + "\n" + e.toString(), e);
            }

            return null;
        }
    }


    public static <T> T newInstance(Class<T> clazz, boolean createByAop, Object... paras) {
        try {
            Class[] classes = new Class[paras.length];
            for (int i = 0; i < paras.length; i++) {
                Object data = paras[i];
                if (data == null) {
                    throw new IllegalArgumentException("paras must not null");
                }
                classes[i] = data.getClass();
            }
            Constructor constructor = clazz.getDeclaredConstructor(classes);
            constructor.setAccessible(true);
            Object ret = constructor.newInstance(paras);
            if (createByAop) {
                Aop.inject(ret);
            }
            return (T) ret;
        } catch (Exception e) {
            LOG.error("can not newInstance class:" + clazz + "\n" + e.toString(), e);
        }

        return null;
    }


    public static <T> T newInstanceByStaticConstruct(Class<T> clazz) {
        StaticConstruct staticConstruct = clazz.getAnnotation(StaticConstruct.class);
        if (staticConstruct == null) {
            return null;
        }

        return newInstanceByStaticConstruct(clazz, staticConstruct);
    }


    public static <T> T newInstanceByStaticConstruct(Class<T> clazz, StaticConstruct staticConstruct) {

        Method method = getStaticConstruct(staticConstruct.value(), clazz);

        if (method == null) {
            throw new JbootException("can not new instance by static constrauct for class : " + clazz);
        }

        try {
            return (T) method.invoke(null, null);
        } catch (Exception e) {

            LOG.error("can not invoke method:" + method.getName()
                    + " in class : "
                    + clazz + "\n"
                    + e.toString(), e);

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }


    private static Method getStaticConstruct(String name, Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())
                    && method.getReturnType() == clazz) {
                if (StrUtil.isBlank(name)) {
                    return method;
                } else if (name.equals(method.getName())) {
                    return method;
                }
            }
        }
        return null;
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
    public static <T> T newInstance(String clazzName, boolean createByAop) {
        return newInstance(clazzName, createByAop, Thread.currentThread().getContextClassLoader());
    }


    /**
     * 创建新的实例
     *
     * @param clazzName
     * @param createByAop
     * @param classLoader
     * @param <T>
     * @return
     */
    public static <T> T newInstance(String clazzName, boolean createByAop, ClassLoader classLoader) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(clazzName, false, classLoader);
            return newInstance(clazz, createByAop);
        } catch (Exception e) {
            LOG.error("can not newInstance class:" + clazzName + "\n" + e.toString(), e);
        }

        return null;
    }


    private static final String ENHANCER_BY = "$$EnhancerBy";

    public static Class getUsefulClass(Class<?> clazz) {
        //ControllerTest$ServiceTest$$EnhancerByGuice$$40471411#hello
        //com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158
        return clazz.getName().indexOf(ENHANCER_BY) == -1 ? clazz : clazz.getSuperclass();
    }


    public static Class getGenericClass(Class<?> clazz) {
        return getGenericClass(getUsefulClass(clazz).getGenericSuperclass());
    }


    public static Class[] getGenericClass(Method method) {
        Type[] type = method.getGenericParameterTypes();
        Class[] classes = new Class[type.length];
        for (int i = 0; i < type.length; i++) {
            classes[i] = getGenericClass(type[i]);
        }
        return classes;
    }


    public static Class getGenericClass(Field field) {
        return getGenericClass(field.getType());
    }


    public static Class getGenericClass(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            return null;
        }
    }


    public static String buildMethodString(Method method) {

        StringBuilder sb = new StringBuilder()
                .append(method.getDeclaringClass().getName())
                .append(".")
                .append(method.getName())
                .append("(");

        Class<?>[] params = method.getParameterTypes();
        int in = 0;
        for (Class<?> clazz : params) {
            sb.append(clazz.getName());
            if (++in < params.length) {
                sb.append(",");
            }
        }

        return sb.append(")").toString();
    }


    public static boolean hasClass(String className) {
        try {
            Class.forName(className, false, getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public static ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : ClassUtil.class.getClassLoader();
    }

}
