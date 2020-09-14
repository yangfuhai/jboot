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
package io.jboot.aop;

import com.jfinal.aop.AopFactory;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.proxy.Proxy;
import com.jfinal.proxy.ProxyManager;
import io.jboot.Jboot;
import io.jboot.aop.annotation.*;
import io.jboot.aop.cglib.CPI;
import io.jboot.aop.cglib.JbootCglibProxyFactory;
import io.jboot.app.config.ConfigUtil;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.mq.JbootmqMessageListener;
import io.jboot.components.rpc.Jbootrpc;
import io.jboot.components.rpc.JbootrpcManager;
import io.jboot.components.rpc.JbootrpcReferenceConfig;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.db.model.JbootModel;
import io.jboot.exception.JbootException;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.*;
import io.jboot.web.controller.JbootController;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class JbootAopFactory extends AopFactory {

    private static final Log LOG = Log.getLog(JbootAopFactory.class);

    //排除默认的映射
    private final static Class[] DEFAULT_EXCLUDES_MAPPING_CLASSES = new Class[]{
            JbootEventListener.class
            , JbootmqMessageListener.class
            , Serializable.class
    };

    private static JbootAopFactory me = new JbootAopFactory();


    public static JbootAopFactory me() {
        return me;
    }


    private Map<String, Object> beansMap = new ConcurrentHashMap<>();
    private List<InterceptorBuilder> interceptorBuilders = new CopyOnWriteArrayList();


    private JbootAopFactory() {
        ProxyManager.me().setProxyFactory(new JbootCglibProxyFactory());
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
        Field[] fields = targetClass.getDeclaredFields();

        if (fields.length != 0) {

            for (Field field : fields) {

                Inject inject = field.getAnnotation(Inject.class);
                if (inject != null) {
                    Bean bean = field.getAnnotation(Bean.class);
                    String beanName = bean != null ? AnnotationUtil.get(bean.name()) : null;
                    if (StrUtil.isNotBlank(beanName)) {
                        doInjectByName(targetObject, field, inject, beanName);
                    } else {
                        doInjectJFinalOrginal(targetObject, field, inject);
                    }
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


        // 是否对超类进行注入
        if (injectSuperClass) {
            Class<?> c = targetClass.getSuperclass();
            if (c != JbootController.class
                    && c != Controller.class
                    && c != JbootServiceBase.class
                    && c != Object.class
                    && c != JbootModel.class
                    && c != Model.class
                    && c != null
            ) {
                doInject(c, targetObject);
            }
        }
    }


    private void doInjectByName(Object targetObject, Field field, Inject inject, String name) throws ReflectiveOperationException {
        Object fieldInjectedObject = getBean(name);
        if (fieldInjectedObject != null) {
            setFieldValue(field, targetObject, fieldInjectedObject);
        } else {
            LOG.warn("can not inject by name [" + name + "] in " + targetObject.getClass() + "." + field.getName() + ", use default.");
            doInjectJFinalOrginal(targetObject, field, inject);
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
    private void doInjectJFinalOrginal(Object targetObject, Field field, Inject inject) throws ReflectiveOperationException {
        Class<?> fieldInjectedClass = inject.value();
        if (fieldInjectedClass == Void.class) {
            fieldInjectedClass = field.getType();
        }

        Object fieldInjectedObject = doGet(fieldInjectedClass);

        setFieldValue(field, targetObject, fieldInjectedObject);
    }

    /**
     * 注入 rpc service
     *
     * @param targetObject
     * @param field
     * @param rpcInject
     */
    private void doInjectRPC(Object targetObject, Field field, RPCInject rpcInject) {

        try {
            JbootrpcReferenceConfig serviceConfig = new JbootrpcReferenceConfig(rpcInject);
            Class<?> fieldInjectedClass = field.getType();

            Jbootrpc jbootrpc = JbootrpcManager.me().getJbootrpc();

            Object fieldInjectedObject = jbootrpc.serviceObtain(fieldInjectedClass, serviceConfig);

            setFieldValue(field, targetObject, fieldInjectedObject);

        } catch (Exception ex) {
            LOG.error("can not inject rpc service in " + targetObject.getClass() + " by config " + rpcInject, ex);
        }
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
            Object fieldInjectedObject = ConfigUtil.convert(fieldInjectedClass, value, field.getGenericType());
            if (fieldInjectedObject != null) {
                setFieldValue(field, targetObject, fieldInjectedObject);
            }
        }
    }


    private String getConfigValue(String key, Object targetObject, Field field) {
        return Jboot.configValue(key);
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
                LogKit.warn("Aop Class[" + from + "] mapping changed from  " + mappingClass + " to " + to);
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
        List<Class> classes = ClassScanner.scanClassByAnnotation(Bean.class, true);
        for (Class implClass : classes) {
            Bean bean = (Bean) implClass.getAnnotation(Bean.class);
            String beanName = AnnotationUtil.get(bean.name());
            if (StrUtil.isNotBlank(beanName)) {
                if (beansMap.containsKey(beanName)) {
                    throw new JbootException("application has contains beanName \"" + beanName + "\" for " + getBean(beanName)
                            + ", can not add for class " + implClass);
                }
                beansMap.put(beanName, get(implClass));
            }

            Class<?>[] interfaceClasses = implClass.getInterfaces();
            if (interfaceClasses == null || interfaceClasses.length == 0) {
                continue;
            }

            Class[] excludes = buildExcludeClasses(implClass);
            for (Class interfaceClass : interfaceClasses) {
                if (!inExcludes(interfaceClass, excludes)) {
                    this.addMapping(interfaceClass, implClass);
                }
            }
        }


        List<Class> configurationClasses = ClassScanner.scanClassByAnnotation(Configuration.class, true);
        for (Class configurationClass : configurationClasses) {
            Object configurationObj = ClassUtil.newInstance(configurationClass);
            if (configurationObj == null) {
                throw new NullPointerException("can not newInstance for class : " + configurationClass);
            }
            Method[] methods = configurationClass.getDeclaredMethods();
            for (Method method : methods) {
                Bean bean = method.getAnnotation(Bean.class);
                if (bean != null) {
                    String beanName = StrUtil.obtainDefaultIfBlank(AnnotationUtil.get(bean.name()), method.getName());
                    if (beansMap.containsKey(beanName)) {
                        throw new JbootException("application has contains beanName \"" + beanName + "\" for " + getBean(beanName)
                                + ", can not add again by method:" + ClassUtil.buildMethodString(method));
                    }

                    Object methodObj = null;
                    try {
                        methodObj = method.invoke(configurationObj);
                        if (methodObj != null) {
                            inject(methodObj);
                            beansMap.put(beanName, methodObj);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    if (methodObj != null) {
                        Class implClass = ClassUtil.getUsefulClass(methodObj.getClass());
                        Class<?>[] interfaceClasses = implClass.getInterfaces();
                        if (interfaceClasses == null || interfaceClasses.length == 0) {
                            continue;
                        }

                        Class[] excludes = buildExcludeClasses(implClass);
                        for (Class interfaceClass : interfaceClasses) {
                            if (!inExcludes(interfaceClass, excludes) && !mapping.containsKey(interfaceClass)) {
                                this.addMapping(interfaceClass, implClass);
                            }
                        }
                    }
                }
            }
        }
    }


    private Class[] buildExcludeClasses(Class implClass) {
        BeanExclude beanExclude = (BeanExclude) implClass.getAnnotation(BeanExclude.class);

        //对某些系统的类 进行排除，例如：Serializable 等
        return beanExclude == null
                ? DEFAULT_EXCLUDES_MAPPING_CLASSES
                : ArrayUtil.concat(DEFAULT_EXCLUDES_MAPPING_CLASSES, beanExclude.value());
    }


    private boolean inExcludes(Class interfaceClass, Class[] excludes) {
        for (Class ex : excludes) {
            if (ex.isAssignableFrom(interfaceClass)) {
                return true;
            }
        }
        return false;
    }


    public <T> T getBean(String name) {
        return (T) beansMap.get(name);
    }

    public void setBean(String name, Object obj) {
        beansMap.put(name, obj);
    }

    public List<InterceptorBuilder> getInterceptorBuilders() {
        return interceptorBuilders;
    }

    public void setInterceptorBuilders(List<InterceptorBuilder> interceptorBuilders) {
        this.interceptorBuilders = interceptorBuilders;
    }


    public void addInterceptorBuilder(InterceptorBuilder interceptorBuilder){
        if(interceptorBuilder == null){
            throw new NullPointerException("interceptorBuilder must not be null.");
        }
        this.interceptorBuilders.add(interceptorBuilder);
        CPI.clearIntersCache();
    }

    public void addInterceptorBuilders(Collection<InterceptorBuilder> interceptorBuilders){
        if(interceptorBuilders == null){
            throw new NullPointerException("interceptorBuilder must not be null.");
        }
        this.interceptorBuilders.addAll(interceptorBuilders);
        CPI.clearIntersCache();
    }

}
