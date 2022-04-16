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
package io.jboot.web.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.jfinal.json.JFinalJson;
import com.jfinal.json.JFinalJsonKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.Jboot;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


public class JbootJson extends JFinalJson {

    private JbootJsonConfig config = Jboot.config(JbootJsonConfig.class);
    protected static Map<Class<?>, MethodsAndFieldsWrapper> methodAndFieldsCache = new HashMap<>();

    public JbootJson() {

        //跳过 null 值输出到浏览器，提高传输性能
        setSkipNullValueField(config.isSkipNullValueField());

        //设置转换层级
        setConvertDepth(config.getDepth());

        //默认设置为 CamelCase 的属性模式
        if (config.isCamelCaseJsonStyleEnable()) {
            setModelAndRecordFieldNameConverter((fieldName) -> StrKit.toCamelCase(fieldName, config.isCamelCaseToLowerCaseAnyway()));
        }

        setToJsonFactory(o -> o instanceof Model ? jbootModelToJson : null);

        if (StrUtil.isNotBlank(config.getTimestampPattern())) {
            setTimestampPattern(config.getTimestampPattern());
        }
    }


    protected JFinalJsonKit.ToJson<Model<?>> jbootModelToJson = (model, depth, ret) -> {
        if (JFinalJsonKit.checkDepth(depth--, ret)) {
            return;
        }

        Map<String, Object> map = new HashMap<>();

        if (!config.isSkipModelAttrs()) {
            fillModelAttrsToMap(CPI.getAttrs(model), map);
        }

        if (!config.isSkipBeanGetters()) {
            fillBeanToMap(model, map);
        }

        optimizeMapAttrs(map);

        JFinalJsonKit.mapToJson(map, depth, ret);
    };


    protected void fillModelAttrsToMap(Map<String, Object> attrs, Map<String, Object> toMap) {
        if (attrs != null && !attrs.isEmpty()) {
            for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                String fieldName = entry.getKey();
                if (config.isCamelCaseJsonStyleEnable()) {
                    fieldName = StrKit.toCamelCase(fieldName, config.isCamelCaseToLowerCaseAnyway());
                }
                toMap.put(fieldName, entry.getValue());
            }
        }
    }


    protected void fillBeanToMap(Object bean, Map<String, Object> toMap) {

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

        for (String ignoreField : wrapper.ignoreFields) {
            toMap.remove(ignoreField);
        }


        for (int i = 0; i < wrapper.fields.size(); i++) {
            String originalField = wrapper.originalFields.get(i);
            toMap.remove(originalField);

            Object value = invokeGetterMethod(wrapper.getterMethods.get(i), bean);
            String field = wrapper.fields.get(i);
            toMap.put(field, value);
        }

    }


    protected void optimizeMapAttrs(Map<String, Object> map) {
    }

    protected Object invokeGetterMethod(Method method, Object bean) {
        try {
            return method.invoke(bean);
        } catch (Exception ex) {
            LogKit.error("can not invoke method: " + ClassUtil.buildMethodString(method), ex);
            return null;
        }
    }


    @Override
    public <T> T parse(String jsonString, Class<T> type) {
        return JSON.parseObject(jsonString, type);
    }


    public static class MethodsAndFieldsWrapper {


        private static boolean hasFastJson = ClassUtil.hasClass("com.alibaba.fastjson.JSON");

        private List<String> fields = new LinkedList<>();
        private List<Method> getterMethods = new LinkedList<>();
        private List<String> originalFields = new LinkedList<>();

        //需要忽略的字段
        private List<String> ignoreFields = new ArrayList<>();

        public MethodsAndFieldsWrapper(Class reflectiveClass) {

            Method[] methodArray = reflectiveClass.getMethods();
            for (Method method : methodArray) {
                if (method.getParameterCount() != 0
                        || method.getReturnType() == void.class
                        || !Modifier.isPublic(method.getModifiers())
                        || "getClass".equals(method.getName())
                        || method.getDeclaringClass() == JbootModel.class
                        || method.getDeclaringClass() == Model.class
                        || method.getDeclaringClass() == Object.class
                ) {
                    continue;
                }


                String fieldName = getGetterMethodField(method.getName());
                if (fieldName != null) {
                    String attrName = StrKit.firstCharToLowerCase(fieldName);
                    if (isIgnoreFiled(method)) {
                        ignoreFields.add(attrName);
                    } else {
                        originalFields.add(attrName);
                        fields.add(getDefineName(method, attrName));
                        getterMethods.add(method);
                    }
                }

            }
        }

        private String getGetterMethodField(String methodName) {
            if (methodName.startsWith("get") && methodName.length() > 3) {
                return methodName.substring(3);
            } else if (methodName.startsWith("is") && methodName.length() > 2) {
                return methodName.substring(2);
            }
            return null;
        }


        private String getDefineName(Method method, String orginalName) {
            if (hasFastJson) {
                JSONField jsonField = method.getAnnotation(JSONField.class);
                if (jsonField != null && StrUtil.isNotBlank(jsonField.name())) {
                    return jsonField.name();
                }
            }
            return orginalName;
        }

        private boolean isIgnoreFiled(Method method) {
            if (hasFastJson) {
                JSONField jsonField = method.getAnnotation(JSONField.class);
                if (jsonField != null && !jsonField.serialize()) {
                    return true;
                }
            }
            return method.getAnnotation(JsonIgnore.class) != null;
        }
    }
}
