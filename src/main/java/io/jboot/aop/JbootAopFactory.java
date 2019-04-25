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
package io.jboot.aop;

import com.jfinal.aop.AopFactory;
import com.jfinal.aop.Enhancer;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.aop.annotation.Bean;
import io.jboot.aop.annotation.BeanExclude;
import io.jboot.aop.annotation.ConfigValue;
import io.jboot.aop.annotation.StaticConstruct;
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
import io.jboot.utils.*;
import io.jboot.web.controller.JbootController;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public class JbootAopFactory extends AopFactory {

    private static final Log LOG = Log.getLog(JbootAopFactory.class);

    //排除默认的映射
    private final static Class[] DEFAULT_EXCLUDES_MAPPING_CLASSES = new Class[]{
            JbootEventListener.class
            , JbootmqMessageListener.class
            , Serializable.class
    };

    private static JbootAopFactory me = new JbootAopFactory();

    //只用用户配置自己的 service 层的拦截器
    protected List<InterceptorWapper> interceptorWappers = Collections.synchronizedList(new ArrayList<>());
    protected InterceptorWapper defaultAopInterceptor = new InterceptorWapper(new JbootAopInterceptor());

    //所有的aop拦截器
    private Interceptor[] aopInterceptors;

    public static JbootAopFactory me() {
        return me;
    }


    private JbootAopFactory() {
        setInjectSuperClass(true);
        initBeanMapping();
    }


    public JbootAopFactory addInterceptor(Interceptor interceptor) {
        interceptorWappers.add(new InterceptorWapper(interceptor));
        clearInterceptorsAndObjectCache();
        return this;
    }


    public JbootAopFactory addInterceptor(Interceptor interceptor, int orderNo) {
        interceptorWappers.add(new InterceptorWapper(interceptor, orderNo));
        clearInterceptorsAndObjectCache();
        return this;
    }

    public List<InterceptorWapper> getInterceptorWappers() {
        return interceptorWappers;
    }

    public Interceptor[] getAopInterceptors() {
        return aopInterceptors;
    }

    protected void clearInterceptorsAndObjectCache() {
        aopInterceptors = null;
        singletonCache.clear();
    }

    protected Interceptor[] buildAopInterceptors() {
        if (aopInterceptors != null) {
            return aopInterceptors;
        }

        // 只有第一次
        // 或动态新增、删除拦截器的时候
        // 会执行
        synchronized (this) {
            if (aopInterceptors == null) {
                if (!interceptorWappers.contains(defaultAopInterceptor)) {
                    interceptorWappers.add(defaultAopInterceptor);
                }
                interceptorWappers.sort(Comparator.comparingInt(InterceptorWapper::getOrderNo));
                Interceptor[] interceptors = new Interceptor[interceptorWappers.size()];
                int i = 0;
                for (InterceptorWapper w : interceptorWappers) interceptors[i++] = w.getInterceptor();
                aopInterceptors = interceptors;
            }
        }

        return aopInterceptors;
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

        return Enhancer.enhance(targetClass, buildAopInterceptors());
    }

    @Override
    protected void doInject(Class<?> targetClass, Object targetObject) throws ReflectiveOperationException {
        targetClass = getUsefulClass(targetClass);
        Field[] fields = targetClass.getDeclaredFields();

        if (fields.length != 0) {

            for (Field field : fields) {

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

        setFiled(field, targetObject, fieldInjectedObject);

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
            JbootrpcServiceConfig serviceConfig = new JbootrpcServiceConfig(rpcInject);
            Class<?> fieldInjectedClass = field.getType();

            Jbootrpc jbootrpc = JbootrpcManager.me().getJbootrpc();

            Object fieldInjectedObject = jbootrpc.serviceObtain(fieldInjectedClass, serviceConfig);

            setFiled(field, targetObject, fieldInjectedObject);

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
            Object fieldInjectedObject = JbootConfigManager.me().convert(fieldInjectedClass, value);

            setFiled(field, targetObject, fieldInjectedObject);
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
        return AnnotationUtil.getConfigValueByKeyString(key);
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
                LogKit.warn("Aop Class[" + from + "] mapping changed from  " + mappingClass + " to " + to);
            }
        }

        mapping.put(from, to);
        return this;
    }


    protected void setFiled(Field filed, Object toObj, Object data) throws IllegalAccessException {
        if (!filed.isAccessible()) {
            filed.setAccessible(true);
        }

        filed.set(toObj, data);
    }


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
}
