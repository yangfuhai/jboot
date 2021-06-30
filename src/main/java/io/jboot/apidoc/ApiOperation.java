package io.jboot.apidoc;

import io.jboot.aop.annotation.DefaultValue;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.apidoc.annotation.ApiParas;
import io.jboot.web.json.JsonBody;

import javax.validation.constraints.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;

public class ApiOperation {

    private String value;
    private String notes;
    private String paraNotes;
    private int orderNo;

    private String actionKey;
    private ContentType contentType;
    private List<ApiParameter> apiParameters;

    private Class<?> retType;
    private Class<?>[] retGenericTypes;

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

    public Class<?> getRetType() {
        return retType;
    }

    public void setRetType(Class<?> retType) {
        this.retType = retType;
    }

    public Class<?>[] getRetGenericTypes() {
        return retGenericTypes;
    }

    public void setRetGenericTypes(Class<?>[] retGenericTypes) {
        this.retGenericTypes = retGenericTypes;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setMethodAndInfo(Method method, String controllerPath, HttpMethod[] defaultMethods) {

        this.method = method;
        this.actionKey = ApiDocUtil.getActionKey(method, controllerPath);
        this.retType = method.getReturnType();
//        method.getGenericReturnType()


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
        for (int i = 0; i < parameters.length; i++) {

            ApiParameter apiParameter = new ApiParameter();
            Parameter parameter = parameters[i];
            apiParameter.setName(parameter.getName());
            apiParameter.setDataType(parameter.getType());

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


}
