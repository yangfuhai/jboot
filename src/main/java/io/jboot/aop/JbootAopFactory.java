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
package io.jboot.aop;

import com.jfinal.aop.AopFactory;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.proxy.Proxy;
import com.jfinal.proxy.ProxyManager;
import io.jboot.aop.annotation.*;
import io.jboot.aop.cglib.JbootCglibProxyFactory;
import io.jboot.aop.javassist.JbootJavassistProxyFactory;
import io.jboot.app.JbootApplicationConfig;
import io.jboot.app.config.JbootConfigKit;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.mq.JbootmqMessageListener;
import io.jboot.components.rpc.*;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.db.model.JbootModel;
import io.jboot.exception.JbootException;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.*;
import io.jboot.web.controller.JbootController;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JbootAopFactory extends AopFactory {

    private static final Log LOG = Log.getLog(JbootAopFactory.class);

    //排除默认的映射
    private final static Class<?>[] DEFAULT_EXCLUDES_MAPPING_CLASSES = new Class[]{
            JbootEventListener.class
            , JbootmqMessageListener.class
            , Serializable.class
    };

    private static JbootAopFactory me = new JbootAopFactory();


    public static JbootAopFactory me() {
        return me;
    }

    private boolean defaultLazyInit = false;

    public boolean isDefaultLazyInit() {
        return defaultLazyInit;
    }

    public void setDefaultLazyInit(boolean defaultLazyInit) {
        this.defaultLazyInit = defaultLazyInit;
    }

    private Map<String, Object> beansCache = new ConcurrentHashMap<>();
    private Map<String, Class<?>> beanNameClassesMapping = new ConcurrentHashMap<>();


    private JbootAopFactory() {

        if ("javassist".equalsIgnoreCase(JbootApplicationConfig.get().getProxy())) {
            ProxyManager.me().setProxyFactory(new JbootJavassistProxyFactory());
        } else {
            ProxyManager.me().setProxyFactory(new JbootCglibProxyFactory());
        }

        setInjectSuperClass(true);
        initBeanMapping();
    }

    @Override
    protected Object createObject(Class<?> targetClass) {
        ConfigModel configModel = targetClass.getAnnotation(ConfigModel.class);
        if (configModel != null) {
            return JbootConfigManager.me().get(targetClass);
        }

        StaticConstruct staticConstruct = targetClass.getAnnotation(StaticConstruct.class);
        if (staticConstruct != null) {
            return ClassUtil.newInstanceByStaticConstruct(targetClass, staticConstruct);
        }

        return Proxy.get(targetClass);
    }


    @Override
    protected void doInject(Class<?> targetClass, Object targetObject) throws ReflectiveOperationException {
        targetClass = getUsefulClass(targetClass);
        doInjectTargetClass(targetClass, targetObject);
        doInvokePostConstructMethod(targetClass, targetObject);
    }


    /**
     * 执行  @PostConstruct 注解方法
     *
     * @param targetClass
     * @param targetObject
     * @throws ReflectiveOperationException
     */
    protected void doInvokePostConstructMethod(Class<?> targetClass, Object targetObject) throws ReflectiveOperationException {
        Method[] methods = targetClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterCount() == 0 && method.getAnnotation(PostConstruct.class) != null) {
                method.setAccessible(true);
                method.invoke(targetObject);
                break;
            }
        }

        Class<?> superClass = targetClass.getSuperclass();
        if (notSystemClass(superClass)) {
            doInvokePostConstructMethod(superClass, targetObject);
        }
    }


    /**
     * 执行注入操作
     *
     * @param targetClass
     * @param targetObject
     * @throws ReflectiveOperationException
     */
    protected void doInjectTargetClass(Class<?> targetClass, Object targetObject) throws ReflectiveOperationException {
        Field[] fields = targetClass.getDeclaredFields();

        if (fields.length != 0) {
            for (Field field : fields) {
                Object fieldValue = null;
                if (defaultLazyInit) {
                    fieldValue = createFieldObjectLazy(targetObject, field);
                } else {
                    Lazy Lazy = field.getAnnotation(Lazy.class);
                    if (Lazy != null) {
                        fieldValue = createFieldObjectLazy(targetObject, field);
                    } else {
                        fieldValue = createFieldObjectNormal(targetObject, field);
                    }
                }

                if (fieldValue != null) {
                    field.setAccessible(true);
                    field.set(targetObject, fieldValue);
                }
            }
        }


        // 是否对超类进行注入
        if (injectSuperClass) {
            Class<?> superClass = targetClass.getSuperclass();
            if (notSystemClass(superClass)) {
                doInjectTargetClass(superClass, targetObject);
            }
        }
    }


    protected Object createFieldObjectLazy(Object targetObject, Field field) throws ReflectiveOperationException {
        return JbootLazyLoaderFactory.me().getLoader().loadLazyObject(targetObject, field);
    }


    public Object createFieldObjectNormal(Object targetObject, Field field) throws ReflectiveOperationException {
        Inject inject = field.getAnnotation(Inject.class);
        if (inject != null) {
            Bean bean = field.getAnnotation(Bean.class);
            String beanName = bean != null ? AnnotationUtil.get(bean.name()) : null;
            if (StrUtil.isNotBlank(beanName)) {
                return createFieldObjectByBeanName(targetObject, field, beanName);
            } else {
                return createFieldObjectByJfinalOriginal(targetObject, field, inject);
            }
        }

        RPCInject rpcInject = field.getAnnotation(RPCInject.class);
        if (rpcInject != null) {
            return createFieldObjectByRPCComponent(targetObject, field, rpcInject);
        }

        ConfigValue configValue = field.getAnnotation(ConfigValue.class);
        if (configValue != null) {
            return createFieldObjectByConfigValue(targetObject, field, configValue);
        }

        return null;
    }


    protected boolean notSystemClass(Class clazz) {
        return clazz != JbootController.class
                && clazz != Controller.class
                && clazz != JbootServiceBase.class
                && clazz != Object.class
                && clazz != JbootModel.class
                && clazz != Model.class
                && clazz != null;
    }


    private Object createFieldObjectByBeanName(Object targetObject, Field field, String beanName) throws ReflectiveOperationException {
        Object fieldInjectedObject = beansCache.get(beanName);
        if (fieldInjectedObject == null) {
            Class<?> fieldInjectedClass = beanNameClassesMapping.get(beanName);
            if (fieldInjectedClass == null || fieldInjectedClass == Void.class) {
                fieldInjectedClass = field.getType();
            }

            fieldInjectedObject = doGet(fieldInjectedClass);
            beansCache.put(beanName, fieldInjectedObject);
        }

        return fieldInjectedObject;
    }

    /**
     * JFinal 原生 service 注入
     *
     * @param targetObject
     * @param field
     * @param inject
     * @throws ReflectiveOperationException
     */
    private Object createFieldObjectByJfinalOriginal(Object targetObject, Field field, Inject inject) throws ReflectiveOperationException {
        Class<?> fieldInjectedClass = inject.value();
        if (fieldInjectedClass == Void.class) {
            fieldInjectedClass = field.getType();
        }

        return doGet(fieldInjectedClass);
    }

    /**
     * 注入 rpc service
     *
     * @param targetObject
     * @param field
     * @param rpcInject
     */
    private Object createFieldObjectByRPCComponent(Object targetObject, Field field, RPCInject rpcInject) {
        try {
            Class<?> fieldInjectedClass = field.getType();

            JbootrpcReferenceConfig config = ReferenceConfigCache.get(fieldInjectedClass, rpcInject);
            Jbootrpc jbootrpc = JbootrpcManager.me().getJbootrpc();
            return jbootrpc.serviceObtain(fieldInjectedClass, config);
        } catch (NullPointerException npe) {
            LOG.error("Can not inject rpc service for \"" + field.getName() + "\" in class \"" + ClassUtil.getUsefulClass(targetObject.getClass()).getName() + "\", because @RPCInject.check ==\"true\" and target is not available. \n" + rpcInject, npe);
        } catch (Exception ex) {
            LOG.error("Can not inject rpc service for \"" + field.getName() + "\" in class \"" + ClassUtil.getUsefulClass(targetObject.getClass()).getName() + "\" \n" + rpcInject, ex);
        }
        return null;
    }

    /**
     * 注入配置文件
     *
     * @param targetObject
     * @param field
     * @param configValue
     * @throws IllegalAccessException
     */
    private Object createFieldObjectByConfigValue(Object targetObject, Field field, ConfigValue configValue) throws IllegalAccessException {
        String key = AnnotationUtil.get(configValue.value());
        Class<?> fieldInjectedClass = field.getType();
        String value = JbootConfigManager.me().getConfigValue(key);

        Object fieldObject = null;
        if (StrUtil.isNotBlank(value)) {
            fieldObject = JbootConfigKit.convert(fieldInjectedClass, value, field.getGenericType());
        }

        if (fieldObject == null) {
            field.setAccessible(true);
            fieldObject = field.get(targetObject);
        }
        return fieldObject;
    }


    /**
     * 允许多次执行 AddMapping，方便在应用运行中可以切换 Mapping
     *
     * @param from
     * @param to
     * @param <T>
     * @return
     */
    @Override
    public synchronized <T> AopFactory addMapping(Class<T> from, Class<? extends T> to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("The parameter from and to can not be null");
        }

        if (mapping == null) {
            mapping = new HashMap<>(128, 0.25F);
        }

        Class mappingClass = mapping.get(from);
        if (mappingClass != null) {
            if (mappingClass == to) {
                return this;
            } else {
                singletonCache.remove(mappingClass);
            }
        }

        mapping.put(from, to);
        return this;
    }


    protected void setFieldValue(Field field, Object toObj, Object data) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(toObj, data);
    }


    /**
     * 初始化 @Bean 注解的映射关系
     */
    private void initBeanMapping() {

        // 初始化 @Configuration 里的 beans
        initConfigurationBeansObject();

        // 添加映射
        initBeansMapping();
    }


    /**
     * 初始化 @Configuration 里的 bean 配置
     */
    private void initConfigurationBeansObject() {
        List<Class> configurationClasses = ClassScanner.scanClassByAnnotation(Configuration.class, true);
        for (Class<?> configurationClass : configurationClasses) {
            Object configurationObj = ClassUtil.newInstance(configurationClass, false);
            if (configurationObj == null) {
                throw new NullPointerException("can not newInstance for class : " + configurationClass);
            }
            Method[] methods = configurationClass.getDeclaredMethods();
            for (Method method : methods) {
                Bean beanAnnotation = method.getAnnotation(Bean.class);
                if (beanAnnotation != null) {
                    Class<?> returnType = method.getReturnType();
                    if (returnType == void.class) {
                        throw new JbootException("@Bean annotation can not use for void method: " + ClassUtil.buildMethodString(method));
                    }

                    String beanName = StrUtil.obtainDefault(AnnotationUtil.get(beanAnnotation.name()), method.getName());
                    if (beansCache.containsKey(beanName)) {
                        throw new JbootException("application has contains beanName \"" + beanName + "\" for " + getBean(beanName)
                                + ", can not add again by method: " + ClassUtil.buildMethodString(method));
                    }

                    try {
                        Object methodObj = method.invoke(configurationObj);
                        if (methodObj != null) {
                            beansCache.put(beanName, methodObj);
                            singletonCache.put(returnType, methodObj);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }


    /**
     * 添加 所有的 Bean 和实现类 的映射
     */
    private void initBeansMapping() {
        List<Class> classes = ClassScanner.scanClassByAnnotation(Bean.class, true);
        for (Class implClass : classes) {
            Bean bean = (Bean) implClass.getAnnotation(Bean.class);
            String beanName = AnnotationUtil.get(bean.name());
            if (StrUtil.isNotBlank(beanName)) {
                if (beanNameClassesMapping.containsKey(beanName)) {
                    throw new JbootException("application has contains beanName \"" + beanName + "\" for " + getBean(beanName)
                            + ", can not add for class " + implClass);
                }
                beanNameClassesMapping.put(beanName, implClass);
            } else {
                Class<?>[] interfaceClasses = implClass.getInterfaces();

                if (interfaceClasses.length == 0) {
                    //add self
                    this.addMapping(implClass, implClass);
                } else {
                    Class<?>[] excludes = buildExcludeClasses(implClass);
                    for (Class<?> interfaceClass : interfaceClasses) {
                        if (!inExcludes(interfaceClass, excludes)) {
                            this.addMapping(interfaceClass, implClass);
                        }
                    }
                }
            }
        }
    }


    private Class<?>[] buildExcludeClasses(Class<?> implClass) {
        BeanExclude beanExclude = implClass.getAnnotation(BeanExclude.class);

        //对某些系统的类 进行排除，例如：Serializable 等
        return beanExclude == null
                ? DEFAULT_EXCLUDES_MAPPING_CLASSES
                : ArrayUtil.concat(DEFAULT_EXCLUDES_MAPPING_CLASSES, beanExclude.value());
    }


    private boolean inExcludes(Class<?> interfaceClass, Class<?>[] excludes) {
        for (Class<?> ex : excludes) {
            if (ex.isAssignableFrom(interfaceClass)) {
                return true;
            }
        }
        return false;
    }


    public <T> T getBean(String name) {
        T ret = (T) beansCache.get(name);
        if (ret == null) {
            if (beanNameClassesMapping.containsKey(name)) {
                try {
                    ret = (T) doGet(beanNameClassesMapping.get(name));
                    beansCache.put(name, ret);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return ret;
    }

    public void setBean(String name, Object obj) {
        beansCache.put(name, obj);
    }

}
