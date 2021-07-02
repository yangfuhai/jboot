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
import com.alibaba.fastjson.parser.Feature;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.apidoc.annotation.ApiResp;
import io.jboot.apidoc.annotation.ApiResps;
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

    //每个类对于的属性名称，一般支持从数据库读取 字段配置 填充，来源于 api-remarks.json
    private Map<String, Map<String, String>> modelFieldRemarks = new HashMap<>();
    private Map<String, Map<String, String>> defaultModelFieldRemarks = new HashMap<>();

    //ClassType Mocks，来源于 api-mock.json
    private Map<String, Object> classTypeMockDatas = new HashMap<>();
    private Map<Class<?>, ApiMockBuilder> classTypeMockBuilders = new HashMap<>();

    //ApiOperation 排序方式
    private Comparator<ApiOperation> operationComparator;

    private ApiDocManager() {
        initDefaultClassTypeMockBuilder();
    }

    private void initDefaultClassTypeMockBuilder() {
        addClassTypeMockBuilders(Ret.class, ApiMockBuilders.retBuilder);
        addClassTypeMockBuilders(Map.class, ApiMockBuilders.mapBuilder);
        addClassTypeMockBuilders(List.class, ApiMockBuilders.listBuilder);
        addClassTypeMockBuilders(Page.class, ApiMockBuilders.pageBuilder);
        addClassTypeMockBuilders(String.class, ApiMockBuilders.stringBuilder);
    }

    public ApiDocRender getRender() {
        return render;
    }

    public void setRender(ApiDocRender render) {
        this.render = render;
    }

    public Map<String, Map<String, String>> getModelFieldRemarks() {
        return modelFieldRemarks;
    }

    public void setModelFieldRemarks(Map<String, Map<String, String>> modelFieldRemarks) {
        this.modelFieldRemarks = modelFieldRemarks;
    }

    public void addModelFieldRemarks(String classOrSimpleName, Map<String, String> fieldNames) {
        this.modelFieldRemarks.put(classOrSimpleName, fieldNames);
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


    String buildMockJson(ClassType classType, Method method) {
        return ApiDocUtil.prettyJson(JsonKit.toJson(doBuildMockObject(classType, method, 0)));
    }


    Object doBuildMockObject(ClassType classType, Method method, int level) {
        Object retObject = getClassTypeMockData(classType.toString());

        if (retObject == null) {
            retObject = getClassTypeMockData(classType.getMainClass().getName());
        }

        if (retObject == null) {
            retObject = getClassTypeMockData(StrKit.firstCharToLowerCase(classType.getMainClass().getSimpleName()));
        }

        if (retObject != null) {
            return retObject;
        }

        for (Class<?> aClass : classTypeMockBuilders.keySet()) {
            if (aClass.isAssignableFrom(classType.getMainClass())) {
                Object object = classTypeMockBuilders.get(aClass).build(classType, method, level);
                if (object != null) {
                    return object;
                }
            }
        }
        return null;
    }


    Map<String, List<ApiResponse>> buildRemarks(ClassType classType, Method method) {
        Map<String, List<ApiResponse>> retMap = new LinkedHashMap<>();
        doBuildRemarks(retMap, classType, method, 0);
        return retMap;
    }


    private void doBuildRemarks(Map<String, List<ApiResponse>> retMap, ClassType classType, Method method, int level) {
        Class<?> mainClass = classType.getMainClass();

        Set<ApiResponse> apiResponses = new HashSet<>();

        //根据默认的配置构建
        doBuildRemarksByDefault(apiResponses, classType, method);

        List<Class<?>> dataTypeClasses = null;

        //根据方法的 @Resp 来构建
        if (level == 0) {
            dataTypeClasses = doBuildRemarksByMethodAnnotation(apiResponses, method);
        }

        //根据配置文件来构建
        doBuildRemarksByConfig(apiResponses, classType, method);

        if (!apiResponses.isEmpty()) {
            retMap.put(mainClass.getSimpleName(), new ArrayList<>(apiResponses));
        }

        //必须执行在 retMap.put 之后，才能保证 remarks 在文档中的顺序
        if (dataTypeClasses != null) {
            for (Class<?> dataType : dataTypeClasses) {
                doBuildRemarks(retMap, new ClassType(dataType), method, level + 1);
            }
        }


        ClassType[] types = classType.getGenericTypes();
        if (types != null) {
            for (ClassType type : types) {
                doBuildRemarks(retMap, type, method, level + 1);
            }
        }
    }


    private void doBuildRemarksByDefault(Set<ApiResponse> apiResponses, ClassType classType, Method method1) {
        Map<String, String> defaultModelRemarks = defaultModelFieldRemarks.get(classType.getMainClass().getName());
        if (defaultModelRemarks == null || defaultModelRemarks.isEmpty()) {
            return;
        }

        List<Method> getterMethods = ReflectUtil.searchMethodList(classType.getMainClass(), m -> m.getParameterCount() == 0
                && m.getReturnType() != void.class
                && Modifier.isPublic(m.getModifiers())
                && (m.getName().startsWith("get") || m.getName().startsWith("is"))
                && !"getClass".equals(m.getName())
        );

        Map<String, Method> filedAndMethodMap = new HashMap<>();
        for (Method getterMethod : getterMethods) {
            filedAndMethodMap.put(ApiDocUtil.getterMethod2Field(getterMethod), getterMethod);
        }

        for (String key : defaultModelRemarks.keySet()) {

            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setName(key);
            apiResponse.setRemarks(defaultModelRemarks.get(key));

            Method getterMethod = filedAndMethodMap.get(key);
            if (getterMethod != null) {
                apiResponse.setDataType(getterMethod.getReturnType().getSimpleName());
            }
            //若没有 getter 方法，一般情况下 map 或者 ret 等
            //此时，需要通过 Mock 数据来对 key 的 dataType 进行推断
            else {
                Object object = doBuildMockObject(classType, method1, 0);
                if (object instanceof Map) {
                    Object value = ((Map<?, ?>) object).get(key);
                    if (value != null) {
                        apiResponse.setDataType(value.getClass().getSimpleName());
                    }
                }
            }

            apiResponses.add(apiResponse);
        }

    }


    private List<Class<?>> doBuildRemarksByMethodAnnotation(Set<ApiResponse> apiResponses, Method method) {

        List<ApiResponse> apiResponses1 = new LinkedList<>();
        List<Class<?>> dataTypes = new ArrayList<>();

        ApiResps apiResps = method.getAnnotation(ApiResps.class);
        if (apiResps != null) {
            for (ApiResp apiResp : apiResps.value()) {
                apiResponses1.add(new ApiResponse(apiResp));
                dataTypes.add(apiResp.dataType());
            }
        }

        ApiResp apiResp = method.getAnnotation(ApiResp.class);
        if (apiResp != null) {
            apiResponses1.add(new ApiResponse(apiResp));
            dataTypes.add(apiResp.dataType());
        }

        apiResponses.addAll(apiResponses1);
        return dataTypes;
    }


    private void doBuildRemarksByConfig(Set<ApiResponse> apiResponses, ClassType classType, Method method1) {
        Map<String, String> configRemarks = getConfigRemarks(classType.getMainClass());
        if (configRemarks == null || configRemarks.isEmpty()) {
            return;
        }

        List<Method> getterMethods = ReflectUtil.searchMethodList(classType.getMainClass(), m -> m.getParameterCount() == 0
                && m.getReturnType() != void.class
                && Modifier.isPublic(m.getModifiers())
                && (m.getName().startsWith("get") || m.getName().startsWith("is"))
                && !"getClass".equals(m.getName())
        );

        Map<String, Method> filedAndMethodMap = new HashMap<>();
        for (Method getterMethod : getterMethods) {
            filedAndMethodMap.put(ApiDocUtil.getterMethod2Field(getterMethod), getterMethod);
        }


        for (String key : configRemarks.keySet()) {

            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setName(key);
            apiResponse.setRemarks(configRemarks.get(key));

            Method getterMethod = filedAndMethodMap.get(key);
            if (getterMethod != null) {
                apiResponse.setDataType(getterMethod.getReturnType().getSimpleName());
            }
            //若没有 getter 方法，一般情况下 map 或者 ret 等
            //此时，需要通过 Mock 数据来对 key 的 dataType 进行推断
            else {
                Object object = doBuildMockObject(classType, method1, 0);
                if (object instanceof Map) {
                    Object value = ((Map<?, ?>) object).get(key);
                    if (value != null) {
                        apiResponse.setDataType(value.getClass().getSimpleName());
                    }
                }
            }

            apiResponses.add(apiResponse);
        }
    }

    private Map<String, String> getConfigRemarks(Class<?> clazz) {
        Map<String, String> ret = modelFieldRemarks.get(clazz.getName());
        return ret != null ? ret : modelFieldRemarks.get(StrKit.firstCharToLowerCase(clazz.getSimpleName()));
    }


//    private static String getGetterMethodField(String methodName) {
//        if (methodName.startsWith("get") && methodName.length() > 3) {
//            return methodName.substring(3);
//        } else if (methodName.startsWith("is") && methodName.length() > 2) {
//            return methodName.substring(2);
//        }
//        return null;
//    }

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
        initModelRemarks(config);

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
            JSONObject mockJsonObject = JSONObject.parseObject(mockJsonString, Feature.OrderedField);
            for (String classTypeKey : mockJsonObject.keySet()) {
                this.classTypeMockDatas.put(classTypeKey, mockJsonObject.get(classTypeKey));
            }
        }
    }


    private void initModelRemarks(ApiDocConfig config) {

        Map<String, String> pageRemarks = new LinkedHashMap<>();
        pageRemarks.put("totalRow", "总行数");
        pageRemarks.put("pageNumber", "当前页码");
        pageRemarks.put("firstPage", "是否是第一页");
        pageRemarks.put("lastPage", "是否是最后一页");
        pageRemarks.put("totalPage", "总页数");
        pageRemarks.put("pageSize", "每页数据量");
        pageRemarks.put("list", "数据列表");
        defaultModelFieldRemarks.put(Page.class.getName(), pageRemarks);


        Map<String, String> retRemarks = new LinkedHashMap<>();
        retRemarks.put("state", "状态，成功 ok，失败 fail");
        defaultModelFieldRemarks.put(Ret.class.getName(), retRemarks);

        Map<String, String> apiRetRemarks = new LinkedHashMap<>();
        apiRetRemarks.put("state", "状态，成功 ok，失败 fail");
        apiRetRemarks.put("errorCode", "错误码，可能返回 null");
        apiRetRemarks.put("message", "错误消息");
        apiRetRemarks.put("data", "数据");
        defaultModelFieldRemarks.put(ApiRet.class.getName(), apiRetRemarks);


        File modelJsonFile = new File(config.getRemarksJsonPathAbsolute());
        if (modelJsonFile.exists()) {
            String modelJsonString = FileUtil.readString(modelJsonFile);
            JSONObject modelJsonObject = JSONObject.parseObject(modelJsonString, Feature.OrderedField);
            for (String classOrSimpleName : modelJsonObject.keySet()) {
                Map<String, String> remarks = new LinkedHashMap<>();
                JSONObject modelRemarks = modelJsonObject.getJSONObject(classOrSimpleName);
                modelRemarks.forEach((k, v) -> remarks.put(k, String.valueOf(v)));
                addModelFieldRemarks(classOrSimpleName, remarks);
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
