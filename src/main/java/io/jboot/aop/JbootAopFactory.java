package io.jboot.aop;

import com.jfinal.aop.AopFactory;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Singleton;
import io.jboot.aop.annotation.Bean;
import io.jboot.aop.annotation.BeanExclude;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.annotation.ConfigInject;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.mq.JbootmqMessageListener;
import io.jboot.components.rpc.Jbootrpc;
import io.jboot.components.rpc.JbootrpcManager;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StrUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class JbootAopFactory extends AopFactory {

    private JbootAopInterceptor aopInterceptor = new JbootAopInterceptor();
    private ThreadLocal<HashMap<Class<?>, Object>> context = ThreadLocal.withInitial(() -> new HashMap<>());

    public JbootAopFactory() {
        setInjectDepth(MAX_INJECT_DEPTH);
        initBeanMapping();
    }

    @Override
    protected <T> T doGet(Class<T> targetClass, int injectDepth) throws ReflectiveOperationException {
        // Aop.get(obj.getClass()) 可以用 Aop.inject(obj)，所以注掉下一行代码
        // targetClass = (Class<T>)getUsefulClass(targetClass);

        targetClass = (Class<T>) getMappingClass(targetClass);

        Singleton si = targetClass.getAnnotation(Singleton.class);
        boolean singleton = (si != null ? si.value() : this.singleton);

        Object ret;
        if (!singleton) {
            ret = createObject(targetClass);
            doInject(targetClass, ret, injectDepth);
            return (T) ret;
        }

        ret = singletonCache.get(targetClass);
        if (ret != null){
            return (T) ret;
        }


        ret = context.get().get(targetClass);
        if (ret == null) {
            synchronized (this) {
                ret = singletonCache.get(targetClass);
                if (ret == null) {
//                    ret = createObject(targetClass);
//                    doInject(targetClass, ret, injectDepth);
//                    singletonCache.put(targetClass, ret);

                    ret = createObject(targetClass);
                    context.get().put(targetClass,ret);
                    doInject(targetClass,ret,injectDepth);
                    singletonCache.put(targetClass,ret);
                }
            }
        }

        return (T) ret;
    }

    @Override
    protected Object createObject(Class<?> targetClass) throws ReflectiveOperationException {
        ConfigModel configModel = targetClass.getAnnotation(ConfigModel.class);
        return configModel != null
                ? JbootConfigManager.me().get(targetClass)
                : com.jfinal.aop.Enhancer.enhance(targetClass, aopInterceptor);
    }


    @Override
    protected void doInject(Class<?> targetClass, Object targetObject, int injectDepth) throws ReflectiveOperationException {
        if ((injectDepth--) <= 0) {
            return;
        }

        targetClass = getUsefulClass(targetClass);
        Field[] fields = targetClass.getDeclaredFields();
        if (fields.length == 0) {
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
                injectByJFinalInject(targetObject, field, inject, injectDepth);
                continue;
            }

            ConfigInject configInject = field.getAnnotation(ConfigInject.class);
            if (configInject != null) {
                injectByConfig(targetObject, field, configInject);
                continue;
            }

            RPCInject rpcInject = field.getAnnotation(RPCInject.class);
            if (rpcInject != null) {
                injectByRPC(targetObject, field, rpcInject);
                continue;
            }

        }
    }

    /**
     * JFinal 原生 service 注入
     *
     * @param targetObject
     * @param field
     * @param inject
     * @throws ReflectiveOperationException
     */
    private void injectByJFinalInject(Object targetObject, Field field, Inject inject, int injectDepth) throws ReflectiveOperationException {
        Class<?> fieldInjectedClass = inject.value();
        if (fieldInjectedClass == Void.class) {
            fieldInjectedClass = field.getType();
        }

        Object fieldInjectedObject = doGet(fieldInjectedClass, injectDepth);

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
    private void injectByRPC(Object targetObject, Field field, RPCInject rpcInject) throws IllegalAccessException {

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
     * @param configInject
     * @throws IllegalAccessException
     */
    private void injectByConfig(Object targetObject, Field field, ConfigInject configInject) throws IllegalAccessException {
        String key = AnnotationUtil.get(configInject.value());
        Class<?> fieldInjectedClass = field.getType();
        String value = getConfigValue(key, targetObject, field);

        if (StrUtil.isBlank(value)) {
            return;
        }

        Object fieldInjectedObject = JbootConfigManager.me().convert(fieldInjectedClass, value);
        field.setAccessible(true);
        field.set(targetObject, fieldInjectedObject);
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
