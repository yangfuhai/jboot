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
package io.jboot.apidoc;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.ClassType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiMockBuilders {

    static ApiMockBuilder retBuilder = new ApiMockBuilder() {
        @Override
        Object build(ClassType classType, Method method, int level) {
            Ret ret = Ret.ok();
            if (classType.isGeneric()) {
                ClassType[] genericTypes = classType.getGenericTypes();
                if (genericTypes.length == 1) {
                    Class<?> type = genericTypes[0].getMainClass();
                    if (List.class.isAssignableFrom(type)) {
                        ret.set("list", getMockObject(genericTypes[0], method, level));
                    } else if (Map.class.isAssignableFrom(type)) {
                        ret.set("map", getMockObject(genericTypes[0], method, level));
                    } else if (Page.class.isAssignableFrom(type)) {
                        ret.set("page", getMockObject(genericTypes[0], method, level));
                    } else {
                        ret.set("object", getMockObject(genericTypes[0], method, level));
                    }
                }
            }
            List<ApiResponse> responses = ApiDocUtil.getApiResponseInMethod(method);
            for (ApiResponse response : responses) {
                Object mockObject = null;
                if (level == 0 && !response.isType(Map.class) && !response.isType(Ret.class)) {
                    mockObject = getMockObject(response.getClassType(), method, level);
                }
                if (mockObject == null || "".equals(mockObject)) {
                    mockObject = response.getMockObject();
                }
                ret.put(response.getName(), mockObject);
            }
            return ret;
        }
    };


    static ApiMockBuilder mapBuilder = new ApiMockBuilder() {
        @Override
        Object build(ClassType classType, Method method, int level) {
            // ret 让给 retBuilder 去构建
            if (Ret.class.isAssignableFrom(classType.getMainClass())) {
                return null;
            }

            Map map = new HashMap();
            if (classType.isGeneric()) {
                Object key = getMockObject(classType.getGenericTypes()[0], method, level);
                if (key == null) {
                    key = "key";
                }
                Object value = getMockObject(classType.getGenericTypes()[1], method, level);
                map.put(key, value);
            }

            List<ApiResponse> responses = ApiDocUtil.getApiResponseInMethod(method);
            for (ApiResponse response : responses) {
                Object mockObject = null;
                if (level == 0 && !response.isType(Map.class) && !response.isType(Ret.class)) {
                    mockObject = getMockObject(response.getClassType(), method, level);
                }
                if (mockObject == null || "".equals(mockObject)) {
                    mockObject = response.getMockObject();
                }
                map.put(response.getName(), mockObject);
            }
            return map;
        }
    };


    static ApiMockBuilder listBuilder = new ApiMockBuilder() {
        @Override
        Object build(ClassType classType, Method method, int level) {
            List list = new ArrayList();
            if (classType.isGeneric()) {
                Object value = getMockObject(classType.getGenericTypes()[0], method, level);
                list.add(value);
                Object value2 = getMockObject(classType.getGenericTypes()[0], method, level);
                list.add(value2);
            }
            return list;
        }
    };


    static ApiMockBuilder pageBuilder = new ApiMockBuilder() {
        @Override
        Object build(ClassType classType, Method method, int level) {
            Page page = new Page();
            page.setPageNumber(1);
            page.setPageSize(10);
            page.setTotalPage(1);
            page.setTotalRow(2);

            List list = new ArrayList();
            list.add(getMockObject(classType.getGenericTypes()[0], method, level));
            list.add(getMockObject(classType.getGenericTypes()[0], method, level));

            page.setList(list);
            return page;
        }
    };


    static ApiMockBuilder stringBuilder = new ApiMockBuilder() {
        @Override
        Object build(ClassType classType, Method method, int level) {
            return "";
        }
    };


}
