package io.jboot.aop;

import com.jfinal.aop.*;
import io.jboot.aop.annotation.Bean;
import io.jboot.aop.annotation.BeanExclude;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.annotation.ConfigInject;
import io.jboot.core.event.JbootEventListener;
import io.jboot.core.mq.JbootmqMessageListener;
import io.jboot.core.rpc.JbootrpcManager;
import io.jboot.core.rpc.JbootrpcServiceConfig;
import io.jboot.core.rpc.annotation.RPCInject;
import io.jboot.kits.ArrayKits;
import io.jboot.kits.ClassScanner;
import io.jboot.kits.StringKits;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class JbootAopFactory extends AopFactory {

    private JbootAopInterceptor aopInterceptor = new JbootAopInterceptor();

    public JbootAopFactory() {
        initBeanMapping();
    }

    @Override
    protected Object createObject(Class<?> targetClass) throws ReflectiveOperationException {
        Object ret =  com.jfinal.aop.Enhancer.enhance(targetClass, aopInterceptor);
        return inject(ret);
    }


    @Override
    public void inject(Class<?> targetClass, Object targetObject, int injectDepth) throws ReflectiveOperationException {
        if ((injectDepth--) <= 0) {
            return;
        }

        targetClass = getUsefulClass(targetClass);
        Field[] fields = targetClass.getDeclaredFields();
        if (fields.length == 0) {
            return;
        }

        for (Field field : fields) {

            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                injectByJFinalInject(targetObject, field, inject);
                return;
            }

            javax.inject.Inject javaxInject = field.getAnnotation(javax.inject.Inject.class);
            if (javaxInject != null) {
                injectByJavaxInject(targetObject, field, javaxInject);
                return;
            }

            ConfigInject configInject = field.getAnnotation(ConfigInject.class);
            if (configInject != null) {
                injectByConfig(targetObject, field, configInject);
                return;
            }

            RPCInject rpcInject = field.getAnnotation(RPCInject.class);
            if (rpcInject != null) {
                injectByRPC(targetObject, field, rpcInject);
                return;
            }
        }
    }


    /**
     * 本地 service 注入
     * @param targetObject
     * @param field
     * @param inject
     * @throws ReflectiveOperationException
     */
    private void injectByJFinalInject(Object targetObject, Field field, Inject inject) throws ReflectiveOperationException {

        Class<?> fieldInjectedClass = inject.value();
        if (fieldInjectedClass == Void.class) {
            fieldInjectedClass = field.getType();
            fieldInjectedClass = getMappingClass(fieldInjectedClass);
        }

        Singleton si = fieldInjectedClass.getAnnotation(Singleton.class);
        boolean singleton = (si != null ? si.value() : this.singleton);

        Object fieldInjectedObject = getOrCreateObject(fieldInjectedClass, singleton);
        field.setAccessible(true);
        field.set(targetObject, fieldInjectedObject);

//        // 递归调用，为当前被注入的对象进行注入
//        this.inject(fieldInjectedObject.getClass(), fieldInjectedObject, injectDepth);
    }


    /**
     * 本地注入，兼容 javax 的注解
     * @param targetObject
     * @param field
     * @param inject
     * @throws ReflectiveOperationException
     */
    private void injectByJavaxInject(Object targetObject, Field field, javax.inject.Inject inject) throws ReflectiveOperationException {

        Class<?> fieldInjectedClass = field.getType();

        Singleton si = fieldInjectedClass.getAnnotation(Singleton.class);
        boolean singleton = (si != null ? si.value() : this.singleton);

        Object fieldInjectedObject = getOrCreateObject(fieldInjectedClass, singleton);
        field.setAccessible(true);
        field.set(targetObject, fieldInjectedObject);

//        // 递归调用，为当前被注入的对象进行注入
//        this.inject(fieldInjectedObject.getClass(), fieldInjectedObject, injectDepth);
    }

    /**
     * 注入 rpc service
     * @param targetObject
     * @param field
     * @param rpcInject
     * @throws IllegalAccessException
     */
    private void injectByRPC(Object targetObject, Field field, RPCInject rpcInject) throws IllegalAccessException {

        JbootrpcServiceConfig serviceConfig = new JbootrpcServiceConfig(rpcInject);
        Class<?> fieldInjectedClass = field.getType();

        Object fieldInjectedObject = JbootrpcManager.me().getJbootrpc().serviceObtain(fieldInjectedClass,serviceConfig);
        field.setAccessible(true);
        field.set(targetObject, fieldInjectedObject);

    }

    /**
     * 注入配置文件
     * @param targetObject
     * @param field
     * @param configInject
     * @throws IllegalAccessException
     */
    private void injectByConfig(Object targetObject, Field field, ConfigInject configInject) throws IllegalAccessException {
        String key = configInject.value();
        Class<?> fieldInjectedClass = field.getType();
        String value = JbootConfigManager.me().getValueByKey(key);

        if (StringKits.isBlank(value)) {
            return;
        }

        Object fieldInjectedObject = JbootConfigManager.me().convert(fieldInjectedClass, value);
        field.setAccessible(true);
        field.set(targetObject, fieldInjectedObject);
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

            BeanExclude beanExclude = (BeanExclude) implClass.getAnnotation(BeanExclude.class);

            //对某些系统的类 进行排除，例如：Serializable 等
            Class[] excludes = beanExclude == null
                    ? default_excludes
                    : ArrayKits.concat(default_excludes, beanExclude.value());

            for (Class interfaceClass : interfaceClasses) {
                if (inExcludes(interfaceClass, excludes) == false) {
                    this.addMapping(interfaceClass, implClass);
                }
            }
        }
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
