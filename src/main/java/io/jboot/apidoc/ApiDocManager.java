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

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.ReflectUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ApiDocManager {

    private static final ApiDocManager me = new ApiDocManager();

    public static ApiDocManager me() {
        return me;
    }

    //渲染器
    private ApiDocRender render = ApiDocRender.MARKDOWN_RENDER;

    //每个类对于的属性名称，一般支持从数据库读取 COMMENT 填充，来源于 api-model.json
    private Map<Class<?>, Map<String, String>> modelFieldNames = new HashMap<>();

    //ClassType Mocks，来源于 api-mock.json
    private Map<String, Object> classTypeMocks = new HashMap<>();

    //ApiOperation 排序方式
    private Comparator<ApiOperation> operationComparator;


    public ApiDocRender getRender() {
        return render;
    }

    public void setRender(ApiDocRender render) {
        this.render = render;
    }

    public Map<Class<?>, Map<String, String>> getModelFieldNames() {
        return modelFieldNames;
    }

    public void setModelFieldNames(Map<Class<?>, Map<String, String>> modelFieldNames) {
        this.modelFieldNames = modelFieldNames;
    }

    public void addModelFieldNames(Class<?> modelClass, Map<String, String> fieldNames) {
        this.modelFieldNames.put(modelClass, fieldNames);
    }

    public Map<String, Object> getClassTypeMocks() {
        return classTypeMocks;
    }

    public void setClassTypeMocks(Map<String, Object> classTypeMocks) {
        this.classTypeMocks = classTypeMocks;
    }

    public void addClassTypeMocks(String classType, Object mockData) {
        this.classTypeMocks.put(classType, mockData);
    }

    public Comparator<ApiOperation> getOperationComparator() {
        return operationComparator;
    }

    public void setOperationComparator(Comparator<ApiOperation> operationComparator) {
        this.operationComparator = operationComparator;
    }


    String getMockJson(ClassType classType, Method method) {
        return JsonKit.toJson(getMockObject(classType, method));
    }


    private Object getMockObject(ClassType classType, Method method) {
        Object jsonDataObject = classTypeMocks.get(classType.toString());
        if (jsonDataObject != null) {
            return jsonDataObject;
        }
        if (Ret.class.isAssignableFrom(classType.getMainClass())) {
            Ret ret = Ret.ok();
            if (classType.isGeneric()) {
                ClassType[] genericTypes = classType.getGenericTypes();
                if (genericTypes.length == 1) {
                    Class<?> type = genericTypes[0].getMainClass();
                    if (List.class.isAssignableFrom(type)) {
                        ret.set("list", getMockObject(genericTypes[0], method));
                    } else if (Map.class.isAssignableFrom(type)) {
                        ret.set("map", getMockObject(genericTypes[0], method));
                    } else if (Page.class.isAssignableFrom(type)) {
                        ret.set("page", getMockObject(genericTypes[0], method));
                    } else {
                        ret.set("object", getMockObject(genericTypes[0], method));
                    }
                }
            }
            return ret;
        } else if (Map.class.isAssignableFrom(classType.getMainClass())) {
            Map map = new HashMap();
            if (classType.isGeneric()) {
                Object key = getMockObject(classType.getGenericTypes()[0], method);
                if (key == null) {
                    key = "key";
                }
                Object value = getMockObject(classType.getGenericTypes()[1], method);
                map.put(key, value);
            }
            return map;
        } else if (List.class.isAssignableFrom(classType.getMainClass())) {
            List list = new ArrayList();
            if (classType.isGeneric()) {
                Object value = getMockObject(classType.getGenericTypes()[0], method);
                list.add(value);
            }
            return list;
        } else if (String.class == classType.getMainClass()){
            return "string";
        }else {
            return null;
        }


    }


    /**
     * 生成 API 文档
     *
     * @param config
     */
    public void genDocs(ApiDocConfig config) {
        List<Class> controllerClasses = ClassScanner.scanClass(aClass -> Controller.class.isAssignableFrom(aClass) && aClass.getAnnotation(Api.class) != null);
        if (controllerClasses.isEmpty()) {
            return;
        }

        List<ApiDocument> apiDocuments = new ArrayList<>();

        if (config.isAllInOneEnable()) {
            ApiDocument document = new ApiDocument();
            document.setValue(config.getAllInOneTitle());
            document.setNotes(config.getAllInOneNotes());
            document.setFilePath(config.getAllInOneFilePath());

            buildAllInOneDocument(document, controllerClasses);

            apiDocuments.add(document);
        } else {
            for (Class<?> controllerClass : controllerClasses) {
                if (StrUtil.isNotBlank(config.getPackagePrefix())) {
                    if (controllerClass.getName().startsWith(config.getPackagePrefix())) {
                        apiDocuments.add(buildDocument(controllerClass));
                    }
                } else {
                    apiDocuments.add(buildDocument(controllerClass));
                }
            }
        }

        if (render != null) {
            render.render(apiDocuments, config);
        }
    }


    private void buildAllInOneDocument(ApiDocument document, List<Class> controllerClasses) {
        for (Class<?> controllerClass : controllerClasses) {
            buildOperation(document, controllerClass);
        }

        List<ApiOperation> operations = document.getApiOperations();
        if (operations != null) {
            if (operationComparator != null) {
                operations.sort(operationComparator);
            } else {
                operations.sort(Comparator.comparing(ApiOperation::getActionKey));
            }
        }
    }


    private ApiDocument buildDocument(Class<?> controllerClass) {

        Api api = controllerClass.getAnnotation(Api.class);
        ApiDocument document = new ApiDocument();
        document.setControllerClass(controllerClass);
        document.setValue(api.value());
        document.setNotes(api.notes());
        document.setFilePathByControllerPath(ApiDocUtil.getControllerPath(controllerClass));


        String filePath = api.filePath();
        if (StrUtil.isNotBlank(filePath)) {
            document.setFilePath(filePath);
        }

        buildOperation(document, controllerClass);

        List<ApiOperation> operations = document.getApiOperations();
        if (operations != null) {
            if (operationComparator != null) {
                operations.sort(operationComparator);
            } else {
                operations.sort(new Comparator<ApiOperation>() {
                    @Override
                    public int compare(ApiOperation o1, ApiOperation o2) {
                        return o1.getOrderNo() == o2.getOrderNo() ? o1.getMethod().getName().compareTo(o2.getMethod().getName()) : o1.getOrderNo() - o2.getOrderNo();
                    }
                });
            }
        }

        return document;
    }


    private void buildOperation(ApiDocument document, Class<?> controllerClass) {

        List<Method> methods = ReflectUtil.searchMethodList(controllerClass,
                method -> method.getAnnotation(ApiOper.class) != null && Modifier.isPublic(method.getModifiers()));

        String controllerPath = ApiDocUtil.getControllerPath(controllerClass);
        HttpMethod defaultHttpMethod = ApiDocUtil.getControllerMethod(controllerClass);

        for (Method method : methods) {
            ApiOperation apiOperation = new ApiOperation();
            apiOperation.setControllerClass(controllerClass);
            apiOperation.setMethodAndInfo(method, controllerPath, ApiDocUtil.getMethodHttpMethods(method, defaultHttpMethod));

            ApiOper apiOper = method.getAnnotation(ApiOper.class);
            apiOperation.setValue(apiOper.value());
            apiOperation.setNotes(apiOper.notes());
            apiOperation.setParaNotes(apiOper.paraNotes());
            apiOperation.setOrderNo(apiOper.orderNo());
            apiOperation.setContentType(apiOper.contentType());

            document.addOperation(apiOperation);
        }


        Api api = controllerClass.getAnnotation(Api.class);
        if (api != null) {
            Class<?>[] collectClasses = api.collect();
            for (Class<?> cClass : collectClasses) {

                String tempControllerPath = ApiDocUtil.getControllerPath(cClass);
                HttpMethod tempDefaultHttpMethod = ApiDocUtil.getControllerMethod(cClass);

                List<Method> collectMethods = ReflectUtil.searchMethodList(cClass,
                        method -> method.getAnnotation(ApiOper.class) != null && Modifier.isPublic(method.getModifiers()));

                for (Method method : collectMethods) {
                    ApiOperation apiOperation = new ApiOperation();
                    apiOperation.setControllerClass(cClass);
                    apiOperation.setMethodAndInfo(method, tempControllerPath, ApiDocUtil.getMethodHttpMethods(method, tempDefaultHttpMethod));

                    ApiOper apiOper = method.getAnnotation(ApiOper.class);
                    apiOperation.setValue(apiOper.value());
                    apiOperation.setNotes(apiOper.notes());
                    apiOperation.setParaNotes(apiOper.paraNotes());
                    apiOperation.setOrderNo(apiOper.orderNo());
                    apiOperation.setContentType(apiOper.contentType());

                    document.addOperation(apiOperation);
                }
            }
        }

    }


}
