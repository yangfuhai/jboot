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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceConfigCache {

    private static Map<Integer, JbootrpcReferenceConfig> configs = new ConcurrentHashMap<>();

    public static JbootrpcReferenceConfig getReferenceConfig(RPCInject rpcInject) {
        int identityHashCode = System.identityHashCode(rpcInject);
        JbootrpcReferenceConfig referenceConfig = configs.get(identityHashCode);
        if (referenceConfig == null) {
            JbootrpcReferenceConfig config = new JbootrpcReferenceConfig();
            RPCUtil.appendAnnotation(RPCInject.class, rpcInject, config);
            configs.putIfAbsent(identityHashCode, config);

            referenceConfig = configs.get(identityHashCode);
        }

        return referenceConfig;
    }
}
