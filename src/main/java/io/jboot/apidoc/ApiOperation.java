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

import io.jboot.aop.annotation.DefaultValue;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.apidoc.annotation.ApiParas;
import io.jboot.utils.ClassType;
import io.jboot.utils.ClassUtil;
import io.jboot.web.json.JsonBody;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ApiOperation implements Serializable {

    private String value;
    private String notes;
    private String paraNotes;
    private int orderNo;

    private String actionKey;
    private ContentType contentType;
    private List<ApiParameter> apiParameters;

    private ClassType retType;
    private String retMockJson;
    private Map<String, List<ApiResponse>> retRemarks;


    private Class<?> controllerClass;
    private Method method;


    public ApiOperation() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getParaNotes() {
        return paraNotes;
    }

    public void setParaNotes(String paraNotes) {
        this.paraNotes = paraNotes;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getActionKey() {
        return actionKey;
    }

    public void setActionKey(String actionKey) {
        this.actionKey = actionKey;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public List<ApiParameter> getApiParameters() {
        return apiParameters;
    }

    public void setApiParameters(List<ApiParameter> apiParameters) {
        this.apiParameters = apiParameters;
    }

    public void addApiParameter(ApiParameter parameter) {
        if (apiParameters == null) {
            apiParameters = new LinkedList<>();
        }
        apiParameters.add(parameter);
    }

    public boolean hasParameter() {
        return apiParameters != null && apiParameters.size() > 0;
    }

    public ClassType getRetType() {
        return retType;
    }

    public void setRetType(ClassType retType) {
        this.retType = retType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }


    public void setMethodAndInfo(Method method, String controllerPath, HttpMethod[] defaultMethods) {

        this.method = method;
        this.actionKey = ApiDocUtil.getActionKey(method, controllerPath);
        this.retType = ClassUtil.getClassType(method.getGenericReturnType(), getControllerClass());

        if (retType.getMainClass() != void.class) {
            this.retMockJson = ApiDocManager.me().buildMockJson(retType,method);
            this.retRemarks = ApiDocManager.me().buildRemarks(retType,method);
        }

        setParameters(method, defaultMethods);
    }


    private void setParameters(Method method, HttpMethod[] defaultMethods) {
        ApiParas apiParas = method.getAnnotation(ApiParas.class);
        if (apiParas != null) {
            for (ApiPara apiPara : apiParas.value()) {
                addApiParameter(new ApiParameter(apiPara, defaultMethods));
            }
        }

        ApiPara apiPara = method.getAnnotation(ApiPara.class);
        if (apiPara != null) {
            addApiParameter(new ApiParameter(apiPara, defaultMethods));
        }

        Parameter[] parameters = method.getParameters();
        Type[] paraTypes = method.getGenericParameterTypes();
        for (int i = 0; i < parameters.length; i++) {

            ApiParameter apiParameter = new ApiParameter();
            Parameter parameter = parameters[i];
            apiParameter.setName(parameter.getName());
            apiParameter.setDataType(ClassUtil.getClassType(paraTypes[i], getControllerClass()));

            if (parameter.getAnnotation(JsonBody.class) != null) {
                apiParameter.setHttpMethods(new HttpMethod[]{HttpMethod.POST});
            } else {
                apiParameter.setHttpMethods(defaultMethods);
            }

            ApiPara paraAnnotation = parameter.getAnnotation(ApiPara.class);
            if (paraAnnotation != null) {
                apiParameter.setValue(paraAnnotation.value());
                apiParameter.setNotes(paraAnnotation.notes());

                if (paraAnnotation.method().length > 0) {
                    apiParameter.setHttpMethods(paraAnnotation.method());
                }
            }

            if (parameter.getAnnotation(NotNull.class) != null) {
                apiParameter.setRequire(true);
            }

            if (parameter.getAnnotation(NotBlank.class) != null) {
                apiParameter.setNotBlank(true);
                apiParameter.setRequire(true);
            }

            if (parameter.getAnnotation(NotEmpty.class) != null) {
                apiParameter.setNotEmpty(true);
                apiParameter.setRequire(true);
            }

            if (parameter.getAnnotation(Email.class) != null) {
                apiParameter.setEmail(true);
                apiParameter.setRequire(true);
            }

            Min min = parameter.getAnnotation(Min.class);
            if (min != null) {
                apiParameter.setMin(min.value());
                apiParameter.setRequire(true);
            }

            Max max = parameter.getAnnotation(Max.class);
            if (max != null) {
                apiParameter.setMax(max.value());
                apiParameter.setRequire(true);
            }

            Pattern pattern = parameter.getAnnotation(Pattern.class);
            if (pattern != null) {
                apiParameter.setPattern(pattern.regexp());
                apiParameter.setRequire(true);
            }

            DefaultValue defaultValue = parameter.getAnnotation(DefaultValue.class);
            if (defaultValue != null) {
                apiParameter.setDefaultValue(defaultValue.value());
            }

            addApiParameter(apiParameter);
        }
    }

    public String getRetMockJson() {
        return retMockJson;
    }

    public void setRetMockJson(String retMockJson) {
        this.retMockJson = retMockJson;
    }

    public Map<String, List<ApiResponse>> getRetRemarks() {
        return retRemarks;
    }

    public void setRetRemarks(Map<String, List<ApiResponse>> retRemarks) {
        this.retRemarks = retRemarks;
    }
}
