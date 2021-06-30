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
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.ReflectUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ApiDocManager {

    private static final ApiDocManager me = new ApiDocManager();

    public static ApiDocManager me() {
        return me;
    }

    private ApiDocRender render = ApiDocRender.DEFAULT_RENDER;

    public ApiDocRender getRender() {
        return render;
    }

    public void setRender(ApiDocRender render) {
        this.render = render;
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
            document.setFilePath(config.getAllInFilePath());

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
        for (Class<?> controllerClass : controllerClasses){
            buildOperation(document, controllerClass);
        }

        List<ApiOperation> operations = document.getApiOperations();
        if (operations != null) {
            operations.sort(Comparator.comparing(ApiOperation::getActionKey));
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
            operations.sort(new Comparator<ApiOperation>() {
                @Override
                public int compare(ApiOperation o1, ApiOperation o2) {
                    return o1.getOrderNo() == o2.getOrderNo() ? o1.getMethod().getName().compareTo(o2.getMethod().getName()) : o1.getOrderNo() - o2.getOrderNo();
                }
            });
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
