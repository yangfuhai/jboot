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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import com.jfinal.log.Log;


public class FastJsonSerializer implements JbootSerializer {

    private static final Log LOG = Log.getLog(FastJsonSerializer.class);

    private final static ParserConfig autoTypeSupportConfig = new ParserConfig();
    static {
        autoTypeSupportConfig.setAutoTypeSupport(true);
    }

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return null;
        }

        return JSON.toJSONBytes(obj
                , SerializerFeature.WriteClassName
                , SerializerFeature.SkipTransientField
                , SerializerFeature.IgnoreErrorGetter
//                , SerializerFeature.IgnoreNonFieldGetter
        );
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
//            return JSON.parse(bytes, Feature.SupportAutoType);
            return JSON.parse(new String(bytes, IOUtils.UTF8), autoTypeSupportConfig);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }

        return null;
    }


}
