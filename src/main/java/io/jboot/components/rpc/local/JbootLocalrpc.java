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
package io.jboot.components.rpc.local;

import com.jfinal.aop.Aop;
import com.jfinal.aop.AopManager;
import io.jboot.components.rpc.JbootrpcBase;
import io.jboot.components.rpc.JbootrpcReferenceConfig;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.utils.ClassUtil;


public class JbootLocalrpc extends JbootrpcBase {

    @Override
    public <T> T serviceObtain(Class<T> serviceClass, JbootrpcReferenceConfig referenceConfig) {
        return Aop.get(serviceClass);
    }

    @Override
    public <T> T onServiceCreate(Class<T> serviceClass, JbootrpcReferenceConfig config) {
        return null;
    }

    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, JbootrpcServiceConfig serviceConfig) {
        AopManager.me().addMapping(interfaceClass, ClassUtil.getUsefulClass(object.getClass()));
        return true;
    }
}
