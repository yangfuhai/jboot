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
package io.jboot.aop;


import com.jfinal.aop.Aop;
import com.jfinal.aop.AopFactory;
import io.jboot.aop.annotation.Bean;
import io.jboot.aop.annotation.BeanExclude;
import io.jboot.core.mq.JbootmqMessageListener;
import io.jboot.event.JbootEventListener;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StrUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Inject管理器
 */
public class JbootInjectManager {

    private static Class[] default_excludes = new Class[]{JbootEventListener.class, JbootmqMessageListener.class, Serializable.class};

    /**
     * 这个manager的创建不能来之ClassNewer
     * 因为 ClassKits 需要 JbootInjectManager，会造成循环调用。
     */
    private static JbootInjectManager manager = new JbootInjectManager();

    public static JbootInjectManager me() {
        return manager;
    }


//    private Injector injector;

    private JbootInjectManager() {
        AopFactory aopFactory = Aop.getAopFactory();
    }

//    public Injector getInjector() {
////        return injector;
//    }


    public <T> T getInstance(Class<T> clazz) {
        return Aop.get(clazz);
    }

    public void injectMembers(Object o) {
        Aop.inject(o);
    }


    /**
     * module implements
     *
     * @param binder
     */
//    @Override
//    public void configure(Binder binder) {
//
//        // 设置 TypeListener
//        binder.bindListener(Matchers.any(), this);
//
//        Matcher matcher = Matchers.annotatedWith(Bean.class)
//                .or(Matchers.annotatedWith(JbootrpcService.class))
//                .or(Matchers.annotatedWith(Before.class));
//
//
//        Matcher notSynthetic = new AbstractMatcher<Method>() {
//            @Override
//            public boolean matches(Method method) {
//                return !method.isSynthetic();
//            }
//        };
//
//        binder.bindInterceptor(matcher, notSynthetic, new AopInterceptor());
//
//        /**
//         * Bean 注解
//         */
//        beanBind(binder);
//
//    }


    /**
     * auto bind interface impl
     */
    private void beanBind() {

        List<Class> classes = ClassScanner.scanClassByAnnotation(Bean.class, true);
        for (Class implClass : classes) {
            Class<?>[] interfaceClasses = implClass.getInterfaces();

            if (interfaceClasses == null || interfaceClasses.length == 0) {
                continue;
            }

            Bean bean = (Bean) implClass.getAnnotation(Bean.class);
            String name = bean.name();

            BeanExclude beanExclude = (BeanExclude) implClass.getAnnotation(BeanExclude.class);

            //对某些系统的类 进行排除，例如：Serializable 等
            Class[] excludes = beanExclude == null ? default_excludes : ArrayUtils.concat(default_excludes, beanExclude.value());

            for (Class interfaceClass : interfaceClasses) {
                boolean isContinue = false;
                for (Class ex : excludes) {
                    if (ex.isAssignableFrom(interfaceClass)) {
                        isContinue = true;
                        break;
                    }
                }
                if (isContinue) {
                    continue;
                }
                try {
                    if (StrUtils.isBlank(name)) {
                        Aop.addMapping(interfaceClass, implClass);
                    }
                } catch (Throwable ex) {
                    System.err.println(String.format("can not bind [%s] to [%s]", interfaceClass, implClass));
                }
            }
        }
    }

    /**
     * TypeListener  implements
     *
     * @param type
     * @param encounter
     * @param <I>
     */
//    @Override
//    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
//        Class clazz = type.getRawType();
//        if (clazz == null) return;
//
//        for (Field field : clazz.getDeclaredFields()) {
//            if (field.isAnnotationPresent(JbootrpcService.class)) {
//                encounter.register(new JbootrpcMembersInjector(field));
//            }
//        }
//    }
}
