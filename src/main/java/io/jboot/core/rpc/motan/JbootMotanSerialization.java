/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.rpc.motan;

import com.weibo.api.motan.codec.Serialization;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.exception.MotanServiceException;
import io.jboot.Jboot;
import io.jboot.core.rpc.JbootrpcConfig;
import io.jboot.core.serializer.SerializerManager;
import io.jboot.exception.JbootException;

import java.io.IOException;


@SpiMeta(name = "jboot")
public class JbootMotanSerialization implements Serialization {

    JbootrpcConfig config = Jboot.config(JbootrpcConfig.class);

    @Override
    public byte[] serialize(Object obj) throws IOException {
        return SerializerManager.me().getSerializer(config.getSerializer()).serialize(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
        return (T) SerializerManager.me().getSerializer(config.getSerializer()).deserialize(bytes);
    }

    @Override
    public byte[] serializeMulti(Object[] objects) throws IOException {
        if (objects.length == 1) {
            return serialize(objects[0]);
        }
        //TODO mulit param support
        throw new JbootException("JbootMotanSerialization not support serialize multi Object");
    }

    @Override
    public Object[] deserializeMulti(byte[] bytes, Class<?>[] classes) throws IOException {
        if (classes.length == 1) {
            return new Object[]{deserialize(bytes, classes[0])};
        } else {
            StringBuilder sb = new StringBuilder(128);
            sb.append("[");
            for (Class c : classes) {
                sb.append(c.getName()).append(",");
            }
            if (sb.length() > 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]");
            throw new MotanServiceException("JbootMotanSerialization not support deserialize multi Object of " + classes);
        }
    }

    @Override
    public int getSerializationNumber() {
        return 8;
    }
}
