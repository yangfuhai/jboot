/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.component.redis;

import io.jboot.Jboot;

import java.util.*;

/**
 * 参考： com.jfinal.plugin.redis
 * JbootRedis 命令文档: http://redisdoc.com/
 */
public abstract class JbootRedisBase implements JbootRedis {


    public byte[] keyToBytes(Object key) {
        return key.toString().getBytes();
    }

    public String bytesToKey(byte[] bytes) {
        return new String(bytes);
    }

    public byte[][] keysToBytesArray(Object... keys) {
        byte[][] result = new byte[keys.length][];
        for (int i = 0; i < result.length; i++)
            result[i] = keyToBytes(keys[i]);
        return result;
    }


    public void fieldSetFromBytesSet(Set<byte[]> data, Set<Object> result) {
        for (byte[] fieldBytes : data) {
            result.add(valueFromBytes(fieldBytes));
        }
    }

    public byte[] valueToBytes(Object value) {
        return Jboot.me().getSerializer().serialize(value);
    }

    public Object valueFromBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return Jboot.me().getSerializer().deserialize(bytes);
    }

    public byte[][] valuesToBytesArray(Object... valuesArray) {
        byte[][] data = new byte[valuesArray.length][];
        for (int i = 0; i < data.length; i++)
            data[i] = valueToBytes(valuesArray[i]);
        return data;
    }

    public void valueSetFromBytesSet(Set<byte[]> data, Set<Object> result) {
        for (byte[] valueBytes : data) {
            result.add(valueFromBytes(valueBytes));
        }
    }

    @SuppressWarnings("rawtypes")
    public List valueListFromBytesList(Collection<byte[]> data) {
        List<Object> result = new ArrayList<Object>();
        for (byte[] d : data) {
            Object object = null;
            try {
                object = valueFromBytes(d);
            } catch (Throwable ex) {
                /**
                 *  有可能出现错误的情况
                 *  在类似blpop等命令，会出现把key也返回，key并不是通过序列化转成byte，而是  key.toString().getBytes()
                 */
                object = new String(d);
            }
            result.add(object);
        }
        return result;
    }

}






