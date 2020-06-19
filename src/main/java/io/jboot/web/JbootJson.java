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
package io.jboot.web;

import com.alibaba.fastjson.JSON;
import com.jfinal.json.JFinalJson;
import com.jfinal.json.JFinalJsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.Jboot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JbootJson extends JFinalJson {

    protected boolean isCamelCaseJsonStyleEnable = Jboot.config(JbootWebConfig.class).isCamelCaseJsonStyleEnable();
    protected Map<Class, MethodsAndFieldsWrapper> methodAndFieldsCache = new HashMap<>();

    public JbootJson() {

        //跳过 null 值输出到浏览器，提高传输性能
        setSkipNullValueField(true);

        //默认设置为 CamelCase 的属性模式
        if (isCamelCaseJsonStyleEnable) {
            setModelAndRecordFieldNameToCamelCase();
        }


        setToJsonFactory(o -> {
            if (o instanceof Model) {
                return jbootModelToJson;
            } else {
                return null;
            }
        });
    }


    protected JFinalJsonKit.ToJson<Model> jbootModelToJson = (model, depth, ret) -> {
        if (JFinalJsonKit.checkDepth(depth--, ret)) {
            return;
        }

        Map<String, Object> map = new HashMap<>();

        fillMapToMap(CPI.getAttrs(model), map);
        fillBeanGetterValueToMap(model, map);


        optimizeMapAttrs(model, map);

        JFinalJsonKit.mapToJson(map, depth, ret);
    };


    protected void fillMapToMap(Map<String, Object> fillMap, Map<String, Object> toMap) {
        if (fillMap != null && !fillMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : fillMap.entrySet()) {
                String fieldName = entry.getKey();
                if (isCamelCaseJsonStyleEnable) {
                    fieldName = StrKit.toCamelCase(fieldName, true);
                }
                toMap.put(fieldName, entry.getValue());
            }
        }
    }


    protected void fillBeanGetterValueToMap(Object bean, Map toMap) {

        MethodsAndFieldsWrapper wrapper = methodAndFieldsCache.get(bean.getClass());
        if (wrapper == null) {
            synchronized (this) {
                if (wrapper == null) {
                    wrapper = new MethodsAndFieldsWrapper(bean.getClass());
                } else {
                    wrapper = methodAndFieldsCache.get(bean.getClass());
                }
            }
        }

        if (wrapper.fields.size() > 0) {
            int index = 0;
            for (String field : wrapper.fields) {
                try {
                    Object value = wrapper.methods.get(index++).invoke(bean);
                    toMap.put(field, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    protected void optimizeMapAttrs(Model model, Map<String, Object> map) {
    }


    public static class MethodsAndFieldsWrapper {

        private List<String> fields = new ArrayList<>();
        private List<Method> methods = new ArrayList<>();

        public MethodsAndFieldsWrapper(Class reflectiveClass) {

            Method[] methodArray = reflectiveClass.getMethods();
            for (Method m : methodArray) {
                if (m.getParameterCount() != 0
                        || m.getReturnType() == void.class
                        || !Modifier.isPublic(m.getModifiers())) {
                    continue;
                }

                String methodName = m.getName();
                int indexOfGet = methodName.indexOf("get");
                if (indexOfGet == 0 && methodName.length() > 3) {    // Only getter
                    String attrName = methodName.substring(3);
                    if (!attrName.equals("Class")) {                // Ignore Object.getClass()
                        fields.add(StrKit.firstCharToLowerCase(attrName));
                        methods.add(m);
                    }
                } else {
                    int indexOfIs = methodName.indexOf("is");
                    if (indexOfIs == 0 && methodName.length() > 2) {
                        String attrName = methodName.substring(2);
                        fields.add(StrKit.firstCharToLowerCase(attrName));
                        methods.add(m);
                    }
                }
            }
        }


        public List<String> getFields() {
            return fields;
        }

        public List<Method> getMethods() {
            return methods;
        }
    }


    @Override
    public <T> T parse(String jsonString, Class<T> type) {
        return JSON.parseObject(jsonString, type);
    }
}
