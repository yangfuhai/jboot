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
package io.jboot.components.serializer;

import io.jboot.Jboot;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootAssert;
import io.jboot.utils.ClassUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SerializerManager {


    private static SerializerManager me;

    private static Map<String, ISerializer> serializerCaches = new ConcurrentHashMap<>();

    public static SerializerManager me() {
        if (me == null) {
            me = ClassUtil.singleton(SerializerManager.class);
        }
        return me;
    }


    public ISerializer getSerializer() {
        JbootSerializerConfig config = Jboot.config(JbootSerializerConfig.class);
        return getSerializer(config.getType());
    }

    public ISerializer getSerializer(String serializerString) {

        ISerializer serializer = serializerCaches.get(serializerString);

        if (serializer == null) {

            serializer = buildSerializer(serializerString);
            serializerCaches.put(serializerString, serializer);
        }

        return serializer;
    }

    public ISerializer buildSerializer(String serializerString) {

        JbootAssert.assertTrue(serializerString != null, "can not get serializer config, please set jboot.serializer value to jboot.proerties");

        /**
         * 可能是某个类名
         */
        if (serializerString != null && serializerString.contains(".")) {

            ISerializer serializer = ClassUtil.newInstance(serializerString);

            if (serializer != null) {
                return serializer;
            }
        }


        switch (serializerString) {
            case JbootSerializerConfig.KRYO:
                return new KryoSerializer();
            case JbootSerializerConfig.FST:
                return new FstSerializer();
            case JbootSerializerConfig.FASTJSON:
                return new FastjsonSerializer();

            default:
                return JbootSpiLoader.load(ISerializer.class, serializerString);
        }
    }


}
