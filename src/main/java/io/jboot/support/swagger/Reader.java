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
package io.jboot.support.swagger;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import io.jboot.web.controller.JbootControllerManager;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.util.PathUtils;
import io.swagger.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参考 ： https://github.com/swagger-api/swagger-core 的 servlet 模块
 */
public class Reader {

    private final Swagger swagger;

    private Reader(Swagger swagger) {
        this.swagger = swagger;
    }

    /**
     * Scans a set of classes for Swagger annotations.
     *
     * @param swagger is the Swagger instance
     * @param classes are a set of classes to scan
     */
    public static void read(Swagger swagger, List<Class> classes) {
        final Reader reader = new Reader(swagger);
        for (Class cls : classes) {
            ReaderContext context = new ReaderContext(cls, "", null, false);
            reader.read(context);
        }
    }

    private void read(ReaderContext context) {

        for (Method method : context.getCls().getDeclaredMethods()) {
            if (ReflectionUtils.isOverriddenMethod(method, context.getCls())) {
                continue;
            }
            final Operation operation = new Operation();


            final Type[] genericParameterTypes = method.getGenericParameterTypes();
            final Annotation[][] paramAnnotations = method.getParameterAnnotations();

            ControllerReaderExtension extension = new ControllerReaderExtension();

            String methodPath = "index".equals(method.getName()) ? "" : "/" + method.getName();
            String operationPath = JbootControllerManager.me().getPathByController((Class<? extends Controller>) context.getCls()) + methodPath;
            //如果有ActionKey注解的URL路径,则使用该路径而不是方法名
            ActionKey actionKeyAnnotation = ReflectionUtils.getAnnotation(method, ActionKey.class);
            if (actionKeyAnnotation != null && !actionKeyAnnotation.value().isEmpty()) {
                if (actionKeyAnnotation.value().startsWith("./")) {
                    String actionName = actionKeyAnnotation.value().substring(2);
                    String pathByController = JbootControllerManager.me().getPathByController((Class<? extends Controller>) context.getCls());
                    operationPath = pathByController.endsWith("/") ? (pathByController + actionName) : (pathByController + "/" + actionName);
                } else {
                    operationPath = actionKeyAnnotation.value();
                }
            }
            String httpMethod = extension.getHttpMethod(context, method);

            if (operationPath == null || httpMethod == null) {
                continue;
            }

            if (extension.isReadable(context)) {
                extension.setDeprecated(operation, method);
                extension.applyConsumes(context, operation, method);
                extension.applyProduces(context, operation, method);
                extension.applyOperationId(operation, method);
                extension.applySummary(operation, method);
                extension.applyDescription(operation, method);
                extension.applySchemes(context, operation, method);
                extension.applySecurityRequirements(context, operation, method);
                extension.applyTags(context, operation, method);
                extension.applyResponses(swagger, context, operation, method);
                extension.applyImplicitParameters(swagger, context, operation, method);
                extension.applyExtensions(context, operation, method);
                for (int i = 0; i < genericParameterTypes.length; i++) {
                    extension.applyParameters(httpMethod, context, operation, paramAnnotations[i]);
                }

                if ("post".equalsIgnoreCase(httpMethod) && operation.getConsumes() == null) {
                    operation.addConsumes("application/x-www-form-urlencoded");
                }
            }

            if (operation.getResponses() == null) {
                operation.defaultResponse(new Response().description("successful operation"));
            }

            final Map<String, String> regexMap = new HashMap<String, String>();
            final String parsedPath = PathUtils.parsePath(operationPath, regexMap);

            Path path = swagger.getPath(parsedPath);
            if (path == null) {
                path = new SwaggerPath();
                swagger.path(parsedPath, path);
            }
            path.set(httpMethod.toLowerCase(), operation);
        }
    }


}
