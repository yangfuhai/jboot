package io.jboot.aop;

import com.google.common.collect.Lists;
import com.jfinal.aop.AopFactory;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Singleton;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.aop.annotation.Bean;
import io.jboot.aop.annotation.BeanExclude;
import io.jboot.aop.annotation.ConfigValue;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.mq.JbootmqMessageListener;
import io.jboot.components.rpc.Jbootrpc;
import io.jboot.components.rpc.JbootrpcManager;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.db.model.JbootModel;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootController;
import io.jboot.web.fixedinterceptor.FixedInterceptor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JbootAopFactory extends AopFactory {

    private JbootAopInterceptor aopInterceptor = new JbootAopInterceptor();

    // 支持循环注入
    protected ThreadLocal<HashMap<Class<?>, Object>> singletonTl = ThreadLocal.withInitial(() -> new HashMap<>());
    protected ThreadLocal<HashMap<Class<?>, Object>> prototypeTl = ThreadLocal.withInitial(() -> new HashMap<>());


    public JbootAopFactory() {
        initBeanMapping();
    }

    @Override
    protected <T> T doGet(Class<T> targetClass, int injectDepth) throws ReflectiveOperationException {
        return doGet(targetClass);
    }

    @SuppressWarnings("unchecked")
    protected <T> T doGet(Class<T> targetClass) throws ReflectiveOperationException {
        targetClass = (Class<T>) getMappingClass(targetClass);
        Singleton si = targetClass.getAnnotation(Singleton.class);
        boolean singleton = (si != null ? si.value() : this.singleton);

        if (singleton) {
            return doGetSingleton(targetClass);
        } else {
            return doGetPrototype(targetClass);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T doGetSingleton(Class<T> targetClass) throws ReflectiveOperationException {
        Object ret = singletonCache.get(targetClass);
        if (ret != null) {
            return (T) ret;
        }

        ret = singletonTl.get().get(targetClass);
        if (ret != null) {        // 发现循环注入
            return (T) ret;
        }

        synchronized (this) {
            ret = singletonCache.get(targetClass);
            if (ret == null) {
                try {
                    ret = createObject(targetClass);
                    singletonTl.get().put(targetClass, ret);
                    doInject(targetClass, ret);
                    singletonCache.put(targetClass, ret);
                } finally {
                    singletonTl.remove();
                }
            }
        }

        return (T) ret;
    }


    @SuppressWarnings("unchecked")
    protected <T> T doGetPrototype(Class<T> targetClass) throws ReflectiveOperationException {
        Object ret;

        HashMap<Class<?>, Object> map = prototypeTl.get();
        if (map.size() > 0) {
            ret = map.get(targetClass);
            if (ret != null) {        // 发现循环注入
                return (T) ret;
            }
        }

        try {
            ret = createObject(targetClass);
            map.put(targetClass, ret);
            doInject(targetClass, ret);
        } finally {
            map.clear();
        }

        return (T) ret;
    }

    @Override
    protected Object createObject(Class<?> targetClass) {
        ConfigModel configModel = targetClass.getAnnotation(ConfigModel.class);
        return configModel != null
                ? JbootConfigManager.me().get(targetClass)
                : com.jfinal.aop.Enhancer.enhance(targetClass, aopInterceptor);
    }


    @Override
    protected void doInject(Class<?> targetClass, Object targetObject, int injectDepth) throws ReflectiveOperationException {
        doInject(targetClass, targetObject);
    }

    protected void doInject(Class<?> targetClass, Object targetObject) throws ReflectiveOperationException {

        targetClass = getUsefulClass(targetClass);
//        Field[] fields = targetClass.getDeclaredFields();

        List<Field> fields = new ArrayList<>();
        doGetFields(targetClass, fields);

        if (fields.size() == 0) {
            return;
        }

        for (Field field : fields) {
//            Inject inject = field.getAnnotation(Inject.class);
//            if (inject == null) {
//                continue ;
//            }
//
//            Class<?> fieldInjectedClass = inject.value();
//            if (fieldInjectedClass == Void.class) {
//                fieldInjectedClass = field.getType();
//            }
//
//            Object fieldInjectedObject = doGet(fieldInjectedClass, injectDepth);
//            field.setAccessible(true);
//            field.set(targetObject, fieldInjectedObject);

            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                doInjectJFinalOrginal(targetObject, field, inject);
                continue;
            }

            ConfigValue configValue = field.getAnnotation(ConfigValue.class);
            if (configValue != null) {
                doInjectConfigValue(targetObject, field, configValue);
                continue;
            }

            RPCInject rpcInject = field.getAnnotation(RPCInject.class);
            if (rpcInject != null) {
                doInjectRPC(targetObject, field, rpcInject);
                continue;
            }

        }
    }

    private void doGetFields(Class clazz, List<Field> fields) {
        Field[] fs = clazz.getDeclaredFields();
        if (fs.length > 0) {
            fields.addAll(Lists.newArrayList(fs));
        }
        Class supperClass = clazz.getSuperclass();
        if (supperClass == JbootController.class
                || supperClass == Controller.class
                || supperClass == JbootServiceBase.class
                || supperClass == Interceptor.class
                || supperClass == FixedInterceptor.class
                || supperClass == JbootModel.class
                || supperClass == Model.class
                || supperClass == Object.class
                || supperClass == null) {
            return;
        }
        doGetFields(supperClass, fields);
    }

    /**
     * JFinal 原生 service 注入
     *
     * @param targetObject
     * @param field
     * @param inject
     * @throws ReflectiveOperationException
     */
    private void doInjectJFinalOrginal(Object targetObject, Field field, Inject inject) throws ReflectiveOperationException {
        Class<?> fieldInjectedClass = inject.value();
        if (fieldInjectedClass == Void.class) {
            fieldInjectedClass = field.getType();
        }

        Object fieldInjectedObject = doGet(fieldInjectedClass);

        field.setAccessible(true);
        field.set(targetObject, fieldInjectedObject);

    }

    /**
     * 注入 rpc service
     *
     * @param targetObject
     * @param field
     * @param rpcInject
     * @throws IllegalAccessException
     */
    private void doInjectRPC(Object targetObject, Field field, RPCInject rpcInject) throws IllegalAccessException {

        JbootrpcServiceConfig serviceConfig = new JbootrpcServiceConfig(rpcInject);
        Class<?> fieldInjectedClass = field.getType();

        Jbootrpc jbootrpc = JbootrpcManager.me().getJbootrpc();

        Object fieldInjectedObject = jbootrpc.serviceObtain(fieldInjectedClass, serviceConfig);
        field.setAccessible(true);
        field.set(targetObject, fieldInjectedObject);

    }

    /**
     * 注入配置文件
     *
     * @param targetObject
     * @param field
     * @param configValue
     * @throws IllegalAccessException
     */
    private void doInjectConfigValue(Object targetObject, Field field, ConfigValue configValue) throws IllegalAccessException {
        String key = AnnotationUtil.get(configValue.value());
        Class<?> fieldInjectedClass = field.getType();
        String value = getConfigValue(key, targetObject, field);

        if (StrUtil.isNotBlank(value)) {
            Object fieldInjectedObject = JbootConfigManager.me().convert(fieldInjectedClass, value);
            field.setAccessible(true);
            field.set(targetObject, fieldInjectedObject);
            return;
        }

        if (configValue.requireNullOrBlank()) {
            field.setAccessible(true);
            if (fieldInjectedClass == int.class) {
                field.set(targetObject, 0);
            } else if (fieldInjectedClass == boolean.class) {
                field.set(targetObject, false);
            } else {
                field.set(targetObject, null);
            }
        }
    }


    private String getConfigValue(String key, Object targetObject, Field field) {
        int indexOf = key.indexOf(":");

        String defaultValue = null;
        if (indexOf != -1) {
            defaultValue = key.substring(indexOf + 1);
            key = key.substring(0, indexOf);
        }

        if (StrUtil.isBlank(key)) {
            throw new RuntimeException("can not inject config by empty key in " + targetObject.getClass() + ":" + field.getName());
        }

        String configValue = JbootConfigManager.me().getConfigValue(key.trim());
        return StrUtil.isBlank(configValue) ? defaultValue : configValue;
    }


    private static Class[] default_excludes = new Class[]{JbootEventListener.class, JbootmqMessageListener.class, Serializable.class};

    /**
     * 初始化 @Bean 注解的映射关系
     */
    private void initBeanMapping() {
        List<Class> classes = ClassScanner.scanClassByAnnotation(Bean.class, true);
        for (Class implClass : classes) {

            Class<?>[] interfaceClasses = implClass.getInterfaces();

            if (interfaceClasses == null || interfaceClasses.length == 0) {
                continue;
            }

            Class[] excludes = buildExcludeClasses(implClass);

            for (Class interfaceClass : interfaceClasses) {
                if (inExcludes(interfaceClass, excludes) == false) {
                    this.addMapping(interfaceClass, implClass);
                }
            }
        }
    }

    private Class[] buildExcludeClasses(Class implClass) {
        BeanExclude beanExclude = (BeanExclude) implClass.getAnnotation(BeanExclude.class);

        //对某些系统的类 进行排除，例如：Serializable 等
        return beanExclude == null
                ? default_excludes
                : ArrayUtil.concat(default_excludes, beanExclude.value());
    }

    private boolean inExcludes(Class interfaceClass, Class[] excludes) {
        for (Class ex : excludes) {
            if (ex.isAssignableFrom(interfaceClass)) {
                return true;
            }
        }
        return false;
    }
}
