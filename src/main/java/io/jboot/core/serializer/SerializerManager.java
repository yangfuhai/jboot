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

import com.jfinal.kit.LogKit;
import io.jboot.Jboot;
import io.jboot.JbootConfig;
import io.jboot.exception.JbootAssert;
import io.jboot.utils.ClassNewer;


public class SerializerManager {

    public static final String FST = "fst";
    public static final String FST2 = "fst2";
    public static final String FASTJSON = "fastjson";

    private static SerializerManager me;

    public static SerializerManager me() {
        if (me == null) {
            me = ClassNewer.singleton(SerializerManager.class);
        }
        return me;
    }

    private SerializerManager() {

    }


    private ISerializer serializer;

    public ISerializer getSerializer() {

        if (serializer != null) {
            return serializer;
        }

        JbootConfig config = Jboot.getJbootConfig();

        JbootAssert.assertTrue(config.getSerializer() != null, "can not get serializer config, please set jboot.serializer value to jboot.proerties");

        switch (config.getSerializer()) {
//            case FST:
//                serializer = new FstSerializer();
//                break;
            case FST2:
                serializer = new Fst2Serializer();
                break;
            case FASTJSON:
                serializer = new FastjsonSerializer();
                break;
            default:
                try {
                    serializer = ClassNewer.newInstance((Class<ISerializer>) Class.forName(config.getSerializer()));
                } catch (Exception e) {
                    LogKit.error(e.toString(), e);
                }
        }


        return serializer;

    }


}
