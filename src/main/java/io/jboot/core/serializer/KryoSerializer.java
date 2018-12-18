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
package io.jboot.core.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.ByteArrayOutputStream;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Kryo 序列化
 * @Description: 性能和 fst一样
 * @Package io.jboot.core.serializer
 */
public class KryoSerializer implements ISerializer {


    private KryoFactory kryoFactory = new KryoFactory() {
        public Kryo create() {
            return new Kryo();
        }
    };

    private KryoPool kryoPool = new KryoPool.Builder(kryoFactory).
            softReferences()
            .build();

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) return null;
        Output output = null;
        Kryo kryo = kryoPool.borrow();
        try {
            output = new Output(new ByteArrayOutputStream());
            kryo.writeClassAndObject(output, obj);
            return output.toBytes();
        } finally {
            if (output != null) {
                output.close();
            }
            kryoPool.release(kryo);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;
        ByteBufferInput input = null;
        Kryo kryo = kryoPool.borrow();
        try {
            input = new ByteBufferInput(bytes);
            return kryo.readClassAndObject(input);
        } finally {
            if (input != null) {
                input.close();
            }
            kryoPool.release(kryo);
        }
    }
}
