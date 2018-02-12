package io.jboot.core.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import io.swagger.models.properties.RefProperty;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lsup
 * 适配swaggerUI, 解决页面"Unknown Type : ref"问题。
 */
public class SwaggerRefPropertySerializer implements ObjectSerializer {
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
