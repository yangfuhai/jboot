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
package io.jboot.core.serializer;

import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootAssert;
import io.jboot.utils.ClassNewer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SerializerManager {

    public static final String FST2 = "fst2";
    public static final String FASTJSON = "fastjson";

    private static SerializerManager me;

    private static Map<String, ISerializer> serializerMap = new ConcurrentHashMap<>();

    public static SerializerManager me() {
        if (me == null) {
            me = ClassNewer.singleton(SerializerManager.class);
        }
        return me;
    }


    public ISerializer getSerializer(String serializerString) {

        ISerializer serializer = serializerMap.get(serializerString);

        if (serializer == null) {

            serializer = buildSerializer(serializerString);
            serializerMap.put(serializerString, serializer);
        }

        return serializer;
    }

    private ISerializer buildSerializer(String serializerString) {

        JbootAssert.assertTrue(serializerString != null, "can not get serializer config, please set jboot.serializer value to jboot.proerties");

        /**
         * 可能是某个类名
         */
        if (serializerString != null && serializerString.contains(".")) {

            ISerializer serializer = ClassNewer.newInstance(serializerString);

            if (serializer != null) {
                return serializer;
            }
        }


        switch (serializerString) {
            case FST2:
                return new Fst2Serializer();
            case FASTJSON:
                return new FastjsonSerializer();
            default:
                return JbootSpiLoader.load(ISerializer.class, serializerString);
        }
    }


}
