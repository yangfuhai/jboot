///**
// * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.apache.org/licenses/LICENSE-2.0
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package io.jboot.components.serializer;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.type.TypeFactory;
//import com.jfinal.log.Log;
//
//import java.io.Serializable;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.Collection;
//
//
//public class JacksonSerializer implements JbootSerializer {
//
//    private static final Log LOG = Log.getLog(JacksonSerializer.class);
//
//    public final static ObjectMapper MAPPER;
//
//    static {
//        MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    }
//
//
//    @Override
//    public byte[] serialize(Object obj) {
//        if (obj == null) {
//            return null;
//        }
//        final TypeFactory typeFactory = MAPPER.getTypeFactory();
//
//        try {
//            JavaType type =typeFactory.constructType(obj.getClass());
//            if (type.isContainerType()) {
//                type = typeFactory.constructCollectionType((Class<? extends Collection>) type.getRawClass(),getActualType(obj,0));
//            }
//
//            return MAPPER.writeValueAsBytes(new CachedObject(type, MAPPER.writeValueAsString(obj)));
//        } catch (JsonProcessingException e) {
//            LOG.error(e.toString(), e);
//        }
//        return null;
//    }
//
//    public static Class<?> getActualType(Object o, int index) {
//        Type clazz = o.getClass().getGenericSuperclass();
//        ParameterizedType pt = (ParameterizedType) clazz;
//        return (Class<?>) pt.getActualTypeArguments()[index];
//    }
//
//    @Override
//    public Object deserialize(byte[] bytes) {
//        if (bytes == null || bytes.length == 0) {
//            return null;
//        }
//        try {
//            CachedObject cachedObject = MAPPER.readValue(bytes, CachedObject.class);
//            return MAPPER.readValue(cachedObject.getData(), cachedObject.getType());
//        } catch (Exception e) {
//            LOG.error(e.toString(), e);
//        }
//
//        return null;
//    }
//
//
//    public static class CachedObject implements Serializable {
//        private JavaType type;
//        private String data;
//
//        public CachedObject() {
//        }
//
//        public CachedObject(JavaType type, String data) {
//            this.type = type;
//            this.data = data;
//        }
//
//        public JavaType getType() {
//            return type;
//        }
//
//        public void setType(JavaType type) {
//            this.type = type;
//        }
//
//        public String getData() {
//            return data;
//        }
//
//        public void setData(String data) {
//            this.data = data;
//        }
//    }
//
//
//
//
//}
