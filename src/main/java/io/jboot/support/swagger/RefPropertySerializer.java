/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.swagger;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import io.swagger.models.properties.RefProperty;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lsup
 * @Description 适配swaggerUI, 解决页面"Unknown Type : ref"问题。
 */
public class RefPropertySerializer implements ObjectSerializer {

    private static final String DOLLAR = "$";

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        RefProperty refProperty = (RefProperty) object;
        Map<String, String> json = new HashMap<String, String>(2);
        if (refProperty.getType().equalsIgnoreCase(RefProperty.TYPE)) {
            json.put(DOLLAR + RefProperty.TYPE, refProperty.get$ref());
        }
        serializer.write(json);
    }
}
