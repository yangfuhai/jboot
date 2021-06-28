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
import com.jfinal.core.Path;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.ReflectUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        for (Class<?> controllerClass : controllerClasses) {
            if (StrUtil.isNotBlank(config.getPackagePrefix())) {
                if (controllerClass.getName().startsWith(config.getPackagePrefix())) {
                    apiDocuments.add(buildDocument(controllerClass));
                }
            } else {
                apiDocuments.add(buildDocument(controllerClass));
            }
        }

        if (render != null) {
            render.render(apiDocuments, config);
        }
    }

    private ApiDocument buildDocument(Class<?> controllerClass) {

        Api api = controllerClass.getAnnotation(Api.class);
        ApiDocument document = new ApiDocument();
        document.setControllerClass(controllerClass);
        document.setValue(api.value());
        document.setNotes(api.notes());

        setDocmentPathAndMethod(document, controllerClass);

        String filePath = api.filePath();
        if (StrUtil.isBlank(filePath)) {
            document.setFilePath(filePath);
        }

        List<Method> methods = ReflectUtil.searchMethodList(controllerClass,
                method -> method.getAnnotation(ApiOper.class) != null && Modifier.isPublic(method.getModifiers()));

        for (Method method : methods) {
            ApiOperation apiOperation = new ApiOperation();
            apiOperation.setMethodAndInfo(method, document.getControllerPath(), getMethodHttpMethods(method, document.getControllerMethod()));

            ApiOper apiOper = method.getAnnotation(ApiOper.class);
            apiOperation.setValue(apiOper.value());
            apiOperation.setNotes(apiOper.notes());
            apiOperation.setContentType(apiOper.contentType());

            document.addOperation(apiOperation);
        }

        return document;
    }


    private void setDocmentPathAndMethod(ApiDocument docment, Class<?> controllerClass) {
        RequestMapping rm = controllerClass.getAnnotation(RequestMapping.class);
        if (rm != null) {
            docment.setMappingAndFilePath(AnnotationUtil.get(rm.value()));
            docment.setControllerMethod(HttpMethod.ALL);
        }

        Path path = controllerClass.getAnnotation(Path.class);
        if (path != null) {
            docment.setMappingAndFilePath(AnnotationUtil.get(path.value()));
            docment.setControllerMethod(HttpMethod.ALL);
        }

        PostMapping pm = controllerClass.getAnnotation(PostMapping.class);
        if (pm != null) {
            docment.setMappingAndFilePath(AnnotationUtil.get(pm.value()));
            docment.setControllerMethod(HttpMethod.POST);
        }

        GetMapping gm = controllerClass.getAnnotation(GetMapping.class);
        if (gm != null) {
            docment.setMappingAndFilePath(AnnotationUtil.get(gm.value()));
            docment.setControllerMethod(HttpMethod.GET);
        }
    }


    private HttpMethod[] getMethodHttpMethods(Method method, HttpMethod defaultMethod) {
        Set<HttpMethod> httpMethods = new HashSet<>();
        if (method.getAnnotation(GetRequest.class) != null) {
            httpMethods.add(HttpMethod.GET);
        }
        if (method.getAnnotation(PostRequest.class) != null) {
            httpMethods.add(HttpMethod.POST);
        }
        if (method.getAnnotation(PutRequest.class) != null) {
            httpMethods.add(HttpMethod.PUT);
        }
        if (method.getAnnotation(DeleteRequest.class) != null) {
            httpMethods.add(HttpMethod.DELETE);
        }
        if (method.getAnnotation(PatchRequest.class) != null) {
            httpMethods.add(HttpMethod.PATCH);
        }
        return httpMethods.isEmpty() ? new HttpMethod[]{defaultMethod} : httpMethods.toArray(new HttpMethod[]{});
    }


}
