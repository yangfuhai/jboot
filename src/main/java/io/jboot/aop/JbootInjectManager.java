/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.aop;


import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.jfinal.aop.Before;
import io.jboot.aop.annotation.Bean;
import io.jboot.aop.injector.JbootHystrixInjector;
import io.jboot.aop.injector.JbootrpcMembersInjector;
import io.jboot.aop.interceptor.JFinalBeforeInterceptor;
import io.jboot.aop.interceptor.JbootCacheInterceptor;
import io.jboot.aop.interceptor.JbootHystrixCommandInterceptor;
import io.jboot.aop.interceptor.JbootrpcInterceptor;
import io.jboot.component.hystrix.annotation.EnableHystrixCommand;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Inject管理器
 */
public class JbootInjectManager implements Module, TypeListener {

    private static JbootInjectManager manager;

    public static JbootInjectManager me() {
        if (manager == null) {
            manager = ClassNewer.newInstance(JbootInjectManager.class);
        }
        return manager;
    }


    private Injector injector;

    private JbootInjectManager() {
        injector = Guice.createInjector(this);
    }


    public Injector getInjector() {
        return injector;
    }


    /**
     * module implements
     *
     * @param binder
     */
    @Override
    public void configure(Binder binder) {
        binder.bindListener(Matchers.any(), this);
        binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(JbootrpcService.class), new JbootrpcInterceptor());
        binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(EnableHystrixCommand.class), new JbootHystrixCommandInterceptor());
        binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(Before.class), new JFinalBeforeInterceptor());
        binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cacheable.class), new JbootCacheInterceptor());


        autoBind(binder);
    }

    /**
     * auto bind interface impl
     *
     * @param binder
     */
    private void autoBind(Binder binder) {
        List<Class> classes = ClassScanner.scanClassByAnnotation(Bean.class, true);
        for (Class beanClass : classes) {
            Class<?>[] interfaceClasses = beanClass.getInterfaces();
            for (Class interfaceClass : interfaceClasses) {
                if (interfaceClass == Serializable.class) {
                    continue;
                }
                try {
                    binder.bind(interfaceClass).to(beanClass);
                } catch (Throwable ex) {
                    System.err.println(String.format("can not bind [%s] to [%s]", interfaceClass, beanClass));
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
    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class clazz = type.getRawType();
        if (clazz == null) return;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(JbootrpcService.class)) {
                encounter.register(new JbootrpcMembersInjector(field));
            }

            if (field.isAnnotationPresent(EnableHystrixCommand.class)) {
                encounter.register(new JbootHystrixInjector(field));
            }
        }
    }
}
