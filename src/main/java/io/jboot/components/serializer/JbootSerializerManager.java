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
package io.jboot.components.serializer;

import io.jboot.Jboot;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JbootSerializerManager {


    private static JbootSerializerManager me = new JbootSerializerManager();

    private static Map<String, JbootSerializer> serializerCaches = new ConcurrentHashMap<>();

    public static JbootSerializerManager me() {
        return me;
    }


    public JbootSerializer getSerializer() {
        JbootSerializerConfig config = Jboot.config(JbootSerializerConfig.class);
        if (StrUtil.isBlank(config.getType())) {
            throw new JbootIllegalConfigException("can not get serializer config, please set jboot.serializer value to jboot.proerties");
        }
        return getSerializer(config.getType());
    }


    public JbootSerializer getSerializer(String serializerName) {
        JbootSerializer serializer = serializerCaches.get(serializerName);
        if (serializer == null) {
            synchronized (this) {
                serializer = serializerCaches.get(serializerName);
                if (serializer == null) {
                    serializer = buildSerializer(serializerName);
                    serializerCaches.put(serializerName, serializer);
                }
            }
        }

        return serializer;
    }

    public JbootSerializer buildSerializer(String serializerName) {
        if (serializerName == null) {
            throw new NullPointerException("SerializerName must not be null");
        }


        switch (serializerName.toLowerCase()) {
            case JbootSerializerConfig.KRYO:
                return new KryoSerializer();
            case JbootSerializerConfig.FST:
                return new FstSerializer();
            case JbootSerializerConfig.FASTJSON:
                return new FastJsonSerializer();
            default:
                return JbootSpiLoader.load(JbootSerializer.class, serializerName);
        }
    }


}
