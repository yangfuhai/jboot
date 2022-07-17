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
package io.jboot.components.rpc;

import io.jboot.components.rpc.annotation.RPCInject;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceConfigCache {

    private static Map<String, JbootrpcReferenceConfig> refrenceConfigCache = new ConcurrentHashMap<>();


    public static JbootrpcReferenceConfig get(Class<?> targetClass, RPCInject rpcInject) {
        String cacheKey = buildKey(targetClass, rpcInject);
        JbootrpcReferenceConfig referenceConfig = refrenceConfigCache.get(cacheKey);
        if (referenceConfig == null) {
            JbootrpcReferenceConfig config = new JbootrpcReferenceConfig();
            RPCUtil.appendAnnotation(RPCInject.class, rpcInject, config);
            refrenceConfigCache.putIfAbsent(cacheKey, config);

            referenceConfig = refrenceConfigCache.get(cacheKey);
        }
        return referenceConfig;
    }



    private static String buildKey(Class<?> targetClass, RPCInject rpcInject) {
        return targetClass.getName() + "@" + hashCode(rpcInject);
    }


    private static int hashCode(RPCInject a) {
        return Objects.hash(a.group(), a.version(), a.url(), a.generic(), a.check(), a.retries(), a.loadbalance(), a.async(), a.actives(), a.timeout()
                , a.application(), a.module(), a.consumer(), a.monitor(), Arrays.hashCode(a.registry()), a.protocol(), a.tag(), a.id());
    }
}