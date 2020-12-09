/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.ActionException;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.render.RenderManager;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

@AutoLoad
public class JsonBodyParseInterceptor implements Interceptor, InterceptorBuilder {

    @Override
    public void intercept(Invocation inv) {
        String rawData = inv.getController().getRawData();
        Parameter[] parameters = inv.getMethod().getParameters();
        for (int index = 0; index < parameters.length; index++) {
            JsonBody jsonBody = parameters[index].getAnnotation(JsonBody.class);
            if (jsonBody != null) {
                if (StrUtil.isBlank(rawData)) {
                    inv.setArg(index, null);
                } else {
                    Class typeClass = parameters[index].getType();
                    Object result = null;
                    try {
                        if (List.class.isAssignableFrom(typeClass) || typeClass.isArray()) {
                            result = parseArray(rawData, typeClass, jsonBody);
                        } else {
                            result = parseObject(rawData, typeClass, jsonBody);
                        }
                    } catch (Exception e) {
                        String message = "Can not parse json to type: " + parameters[index].getType() + " in method: " + ClassUtil.buildMethodString(inv.getMethod());
                        if (jsonBody.skipConvertError()) {
                            LogKit.error(message);
                        } else {
                            throw new ActionException(400, RenderManager.me().getRenderFactory().getErrorRender(400), message);
                        }
                    }
                    inv.setArg(index, result);
                }
            }
        }


        inv.invoke();
    }


    private Object parseObject(String rawData, Class typeClass, JsonBody jsonBody) throws IllegalAccessException, InstantiationException {
        JSONObject rawObject = JSON.parseObject(rawData);
        if (StrUtil.isNotBlank(jsonBody.value())) {
            String[] values = jsonBody.value().split("\\.");
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (StrUtil.isNotBlank(value)) {
                    rawObject = rawObject.getJSONObject(value);
                    if (rawObject == null || rawObject.isEmpty()) {
                        break;
                    }
                }
            }
        }

        if (rawObject == null || rawObject.isEmpty()) {
            return null;
        }

        if (typeClass == Map.class || typeClass == JSONObject.class) {
            return rawObject;
        }

        if (Map.class.isAssignableFrom(typeClass) && canNewInstance(typeClass)) {
            Map map = (Map) typeClass.newInstance();
            for (String key : rawObject.keySet()) {
                map.put(key, rawObject.get(key));
            }
            return map;
        }

        return rawObject.toJavaObject(typeClass);
    }

    private Object parseArray(String rawData, Class typeClass, JsonBody jsonBody) {
        JSONArray jsonArray = null;
        if (StrUtil.isBlank(jsonBody.value())) {
            jsonArray = JSON.parseArray(rawData);
        } else {
            JSONObject jsonObject = JSON.parseObject(rawData);
            String[] values = jsonBody.value().split("\\.");
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (StrUtil.isNotBlank(value)) {
                    if (i == values.length - 1) {
                        jsonArray = jsonObject.getJSONArray(value);
                    } else {
                        jsonObject = jsonObject.getJSONObject(value);
                        if (jsonObject == null || jsonObject.isEmpty()) {
                            break;
                        }
                    }
                }
            }
        }

        if (jsonArray == null || jsonArray.isEmpty()) {
            return null;
        }

        return jsonArray.toJavaObject(typeClass);
    }


    private boolean canNewInstance(Class clazz) {
        int modifiers = clazz.getModifiers();
        return !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers);
    }


    @Override
    public void build(Class<?> serviceClass, Method method, Interceptors interceptors) {
        if (Controller.class.isAssignableFrom(serviceClass)) {
            Parameter[] parameters = method.getParameters();
            if (parameters != null && parameters.length > 0) {
                for (Parameter p : parameters) {
                    if (p.getAnnotation(JsonBody.class) != null) {
                        interceptors.add(this);
                        return;
                    }
                }
            }
        }
    }
}