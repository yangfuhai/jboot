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
import io.jboot.core.hystrix.annotation.UseHystrixCommand;
import io.jboot.rpc.annotation.JbootrpcService;

import java.lang.reflect.Field;

/**
 * Inject管理器
 */
public class JbootInjectManager {

    static JbootInjectManager me = new JbootInjectManager();

    public static JbootInjectManager me() {
        return me;
    }


    private Injector injector;

    private JbootInjectManager() {
        injector = Guice.createInjector(appModule);
    }


    public Injector getInjector() {
        return injector;
    }


    private Module appModule = new Module() {
        @Override
        public void configure(Binder binder) {
            binder.bindListener(Matchers.any(), rpcListener);
            binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(JbootrpcService.class), new JbootrpcInterceptor());
        }
    };


    private TypeListener rpcListener = new TypeListener() {
        @Override
        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            Class clazz = type.getRawType();
            if (clazz == null) return;

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(JbootrpcService.class)) {
                    encounter.register(new JbootrpcMembersInjector(field));
                }

                if (field.isAnnotationPresent(UseHystrixCommand.class)) {
                    encounter.register(new JbootHystrixInjector(field));
                }
            }
        }
    };


}
