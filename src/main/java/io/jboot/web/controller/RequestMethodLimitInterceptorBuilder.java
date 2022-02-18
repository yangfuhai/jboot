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
package io.jboot.web.controller;

import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.web.controller.annotation.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@AutoLoad
public class RequestMethodLimitInterceptorBuilder implements InterceptorBuilder {


    @Override
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {
        if (Util.isController(targetClass)) {

            Set<String> supportMethods = new HashSet<>();

            if (Util.hasAnnotation(method, GetRequest.class)) {
                supportMethods.add("GET");
            }
            if (Util.hasAnnotation(method, PostRequest.class)) {
                supportMethods.add("POST");
            }
            if (Util.hasAnnotation(method, PutRequest.class)) {
                supportMethods.add("PUT");
            }
            if (Util.hasAnnotation(method, DeleteRequest.class)) {
                supportMethods.add("DELETE");
            }
            if (Util.hasAnnotation(method, PatchRequest.class)) {
                supportMethods.add("PATCH");
            }

            if (!supportMethods.isEmpty()) {
                interceptors.add(new RequestMethodLimitInterceptor(supportMethods));
            }
        }
    }
}
