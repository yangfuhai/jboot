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

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.utils.*;

import java.io.File;
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

    //每个类对于的属性名称，一般支持从数据库读取 字段配置 填充，来源于 api-model.json
    private Map<String, Map<String, String>> modelFieldNames = new HashMap<>();

    //ClassType Mocks，来源于 api-mock.json
    private Map<String, Object> classTypeMockDatas = new HashMap<>();
    private Map<Class<?>, ApiMockBuilder> classTypeMockBuilders = new HashMap<>();

    //ApiOperation 排序方式
    private Comparator<ApiOperation> operationComparator;

    private ApiDocManager() {
        initDefaultClassTypeMockBuilder();
    }

    private void initDefaultClassTypeMockBuilder() {

        addClassTypeMockBuilders(Ret.class, new ApiMockBuilder() {
            @Override
            Object build(ClassType classType) {
                Ret ret = Ret.ok();
                if (classType.isGeneric()) {
                    ClassType[] genericTypes = classType.getGenericTypes();
                    if (genericTypes.length == 1) {
                        Class<?> type = genericTypes[0].getMainClass();
                        if (List.class.isAssignableFrom(type)) {
                            ret.set("list", getMockObject(genericTypes[0]));
                        } else if (Map.class.isAssignableFrom(type)) {
                            ret.set("map", getMockObject(genericTypes[0]));
                        } else if (Page.class.isAssignableFrom(type)) {
                            ret.set("page", getMockObject(genericTypes[0]));
                        } else {
                            ret.set("object", getMockObject(genericTypes[0]));
                        }
                    }
                }
                return ret;
            }
        });


        addClassTypeMockBuilders(Map.class, new ApiMockBuilder() {
            @Override
            Object build(ClassType classType) {
                // ret 让给 retBuilder 去构建
                if (Ret.class.isAssignableFrom(classType.getMainClass())) {
                    return null;
                }

                Map map = new HashMap();
                if (classType.isGeneric()) {
                    Object key = getMockObject(classType.getGenericTypes()[0]);
                    if (key == null) {
                        key = "key";
                    }
                    Object value = getMockObject(classType.getGenericTypes()[1]);
                    map.put(key, value);
                }
                return map;
            }
        });


        addClassTypeMockBuilders(List.class, new ApiMockBuilder() {
            @Override
            Object build(ClassType classType) {
                List list = new ArrayList();
                if (classType.isGeneric()) {
                    Object value = getMockObject(classType.getGenericTypes()[0]);
                    list.add(value);
                    Object value2 = getMockObject(classType.getGenericTypes()[0]);
                    list.add(value2);
                }
                return list;
            }
        });


        addClassTypeMockBuilders(Page.class, new ApiMockBuilder() {
            @Override
            Object build(ClassType classType) {
                Page page = new Page();
                page.setPageNumber(1);
                page.setPageSize(10);
                page.setTotalPage(1);
                page.setTotalRow(2);

                List list = new ArrayList();
                list.add(getMockObject(classType.getGenericTypes()[0]));
                list.add(getMockObject(classType.getGenericTypes()[0]));

                page.setList(list);
                return page;
            }
        });


        addClassTypeMockBuilders(String.class, new ApiMockBuilder() {
            @Override
            Object build(ClassType classType) {
                return "string";
            }
        });

    }

    public ApiDocRender getRender() {
        return render;
    }

    public void setRender(ApiDocRender render) {
        this.render = render;
    }

    public Map<String, Map<String, String>> getModelFieldNames() {
        return modelFieldNames;
    }

    public void setModelFieldNames(Map<String, Map<String, String>> modelFieldNames) {
        this.modelFieldNames = modelFieldNames;
    }

    public void addModelFieldNames(String classOrSimpleName, Map<String, String> fieldNames) {
        this.modelFieldNames.put(classOrSimpleName, fieldNames);
    }

    public Map<String, Object> getClassTypeMockDatas() {
        return classTypeMockDatas;
    }

    public void setClassTypeMockDatas(Map<String, Object> classTypeMockDatas) {
        this.classTypeMockDatas = classTypeMockDatas;
    }

    public void addClassTypeMocks(String classType, Object mockData) {
        this.classTypeMockDatas.put(classType, mockData);
    }

    public Object getClassTypeMockData(String classType) {
        return this.classTypeMockDatas.get(classType);
    }

    public Comparator<ApiOperation> getOperationComparator() {
        return operationComparator;
    }

    public void setOperationComparator(Comparator<ApiOperation> operationComparator) {
        this.operationComparator = operationComparator;
    }

    public Map<Class<?>, ApiMockBuilder> getClassTypeMockBuilders() {
        return classTypeMockBuilders;
    }

    public void setClassTypeMockBuilders(Map<Class<?>, ApiMockBuilder> classTypeMockBuilders) {
        this.classTypeMockBuilders = classTypeMockBuilders;
    }

    public void addClassTypeMockBuilders(Class<?> forClass, ApiMockBuilder builder) {
        this.classTypeMockBuilders.put(forClass, builder);
    }


    String getMockJson(ClassType classType) {
        return ApiDocUtil.prettyJson(JsonKit.toJson(getMockObject(classType)));
    }


    Object getMockObject(ClassType classType) {
        Object retObject = getClassTypeMockData(classType.toString().toLowerCase());

        if (retObject == null) {
            getClassTypeMockData(classType.getMainClass().getName().toLowerCase());
        }

        if (retObject == null) {
            getClassTypeMockData(classType.getMainClass().getSimpleName().toLowerCase());
        }

        if (retObject != null) {
            return retObject;
        }

        for (Class<?> aClass : classTypeMockBuilders.keySet()) {
            if (aClass.isAssignableFrom(classType.getMainClass())) {
                Object object = classTypeMockBuilders.get(aClass).build(classType);
                if (object != null) {
                    return object;
                }
            }
        }
        return null;
    }


    Map<String, List<ApiFieldInfo>> getMockFieldInfo(ClassType classType) {
        Map<String, List<ApiFieldInfo>> retMap = new HashMap<>();
        doGetMockFieldInfo(retMap, classType);
        return retMap;
    }

    private void doGetMockFieldInfo(Map<String, List<ApiFieldInfo>> retMap, ClassType classType) {
        Class<?> mainClass = classType.getMainClass();
        Map<String, String> fieldNames = getFieldNames(mainClass);
        if (fieldNames != null && !fieldNames.isEmpty()) {
            retMap.put(mainClass.getSimpleName(), buildApiFieldInfos(fieldNames, mainClass));
        }

        ClassType[] types = classType.getGenericTypes();
        if (types != null) {
            for (ClassType type : types) {
                doGetMockFieldInfo(retMap, type);
            }
        }
    }

    private Map<String, String> getFieldNames(Class<?> clazz) {
        Map<String, String> ret = modelFieldNames.get(clazz.getName().toLowerCase());
        return ret != null ? ret : modelFieldNames.get(clazz.getSimpleName().toLowerCase());
    }

    private static List<ApiFieldInfo> buildApiFieldInfos(Map<String, String> fieldNames, Class<?> mainClass) {
        List<Method> getterMethods = ReflectUtil.searchMethodList(mainClass, method -> method.getParameterCount() == 0
                && method.getReturnType() != void.class
                && Modifier.isPublic(method.getModifiers())
                && (method.getName().startsWith("get") || method.getName().startsWith("is"))
                && !"getClass".equals(method.getName())
        );

        List<ApiFieldInfo> apiFieldInfos = new ArrayList<>();

        for (Method getterMethod : getterMethods) {
            String filedName = StrUtil.firstCharToLowerCase(getGetterMethodField(getterMethod.getName()));
            ApiFieldInfo fieldInfo = new ApiFieldInfo();
            fieldInfo.setName(filedName);
            fieldInfo.setDataType(getterMethod.getReturnType().getSimpleName());
            fieldInfo.setRemarks(fieldNames.get(filedName));
            apiFieldInfos.add(fieldInfo);
        }

        return apiFieldInfos;
    }

    private static String getGetterMethodField(String methodName) {
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return methodName.substring(3);
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return methodName.substring(2);
        }
        return null;
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

        initMockJson(config);
        initModelInfoJson(config);

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


    private void initMockJson(ApiDocConfig config) {
        File mockJsonFile = new File(config.getMockJsonPathAbsolute());
        if (mockJsonFile.exists()) {
            String mockJsonString = FileUtil.readString(mockJsonFile);
            JSONObject mockJsonObject = JSONObject.parseObject(mockJsonString);
            for (String s : mockJsonObject.keySet()) {
                this.classTypeMockDatas.put(s.toLowerCase(), mockJsonObject.get(s));
            }
        }
    }


    private void initModelInfoJson(ApiDocConfig config) {

        Map<String, String> pageAttrs = new HashMap<>();
        pageAttrs.put("totalRow", "总行数");
        pageAttrs.put("pageNumber", "当前页码");
        pageAttrs.put("firstPage", "是否是第一页");
        pageAttrs.put("lastPage", "是否是最后一页");
        pageAttrs.put("totalPage", "总页数");
        pageAttrs.put("pageSize", "每页数据量");
        pageAttrs.put("list", "数据列表");
        this.modelFieldNames.put(Page.class.getName().toLowerCase(), pageAttrs);


        File modelJsonFile = new File(config.getModelJsonPathAbsolute());
        if (modelJsonFile.exists()) {
            String modelJsonString = FileUtil.readString(modelJsonFile);
            JSONObject modelJsonObject = JSONObject.parseObject(modelJsonString);
            for (String s : modelJsonObject.keySet()) {
                Map<String, String> attrs = new HashMap<>();
                JSONObject value = modelJsonObject.getJSONObject(s);
                value.forEach((k, v) -> attrs.put(StrKit.firstCharToLowerCase(StrKit.toCamelCase(k)), String.valueOf(v)));
                this.modelFieldNames.put(s.toLowerCase(), attrs);
            }
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
