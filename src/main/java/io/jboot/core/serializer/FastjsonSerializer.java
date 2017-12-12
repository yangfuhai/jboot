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
package io.jboot.core.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class FastjsonSerializer implements ISerializer {

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) return null;
        FastJsonCacheObject object = new FastJsonCacheObject(obj.getClass(), obj);
        String string = JSON.toJSONString(object);
        return string.getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        String json = new String(bytes);
        JSONObject jsonObject = JSON.parseObject(json);
        Class clazz = jsonObject.getObject("clazz", Class.class);
        return jsonObject.getObject("object", clazz);
    }


    public static class FastJsonCacheObject {
        private Class clazz;
        private Object object;

        public FastJsonCacheObject() {
        }

        public FastJsonCacheObject(Class clazz, Object object) {
            this.clazz = clazz;
            this.object = object;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }


}
