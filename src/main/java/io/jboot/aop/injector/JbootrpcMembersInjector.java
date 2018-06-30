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
package io.jboot.aop.injector;

import com.google.inject.MembersInjector;
import com.jfinal.log.Log;
import io.jboot.core.rpc.JbootrpcManager;
import io.jboot.core.rpc.JbootrpcServiceConfig;
import io.jboot.core.rpc.annotation.JbootrpcService;

import java.lang.reflect.Field;

/**
 * RPC 的注入器，用来初始化RPC对象
 */
public class JbootrpcMembersInjector implements MembersInjector {

    private static Log log = Log.getLog(JbootrpcMembersInjector.class);
    private Field field;

    public JbootrpcMembersInjector(Field field) {
        this.field = field;
    }

    @Override
    public void injectMembers(Object instance) {

        JbootrpcService annotation = field.getAnnotation(JbootrpcService.class);

        Object implObject = null;
        try {
            implObject = JbootrpcManager.me().getJbootrpc().serviceObtain(field.getType(), new JbootrpcServiceConfig(annotation));
        } catch (Throwable e) {
            log.error(e.toString(), e);
        }

        if (implObject == null) {
            return;
        }

        try {
            field.setAccessible(true);
            field.set(instance, implObject);
        } catch (Throwable e) {
            log.error(e.toString(), e);
        }
    }
}
