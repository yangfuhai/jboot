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
import io.jboot.Jboot;
import io.jboot.core.rpc.JbootrpcConfig;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.utils.StringUtils;

import java.lang.reflect.Field;

/**
 * RPC 的注入器，用来初始化RPC对象
 */
public class JbootrpcMembersInjector implements MembersInjector {

    private static Log log = Log.getLog(JbootrpcMembersInjector.class);

    private Field field;
    private static JbootrpcConfig config = Jboot.config(JbootrpcConfig.class);

    public JbootrpcMembersInjector(Field field) {
        this.field = field;
    }

    @Override
    public void injectMembers(Object instance) {
        Object rpcImpl = null;
        JbootrpcService jbootrpcService = field.getAnnotation(JbootrpcService.class);

        String group = StringUtils.isBlank(jbootrpcService.group()) ? config.getDefaultGroup() : jbootrpcService.group();
        String version = StringUtils.isBlank(jbootrpcService.version()) ? config.getDefaultVersion() : jbootrpcService.version();
        
        try {
            rpcImpl = Jboot.service(field.getType(), group, version);
        } catch (Throwable e) {
            log.error(e.toString(), e);
        }

        if (rpcImpl == null) {
            return;
        }

        try {
            field.setAccessible(true);
            field.set(instance, rpcImpl);
        } catch (Throwable e) {
            log.error(e.toString(), e);
        }
    }
}
