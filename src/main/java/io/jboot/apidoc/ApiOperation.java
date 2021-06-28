package io.jboot.apidoc;

import com.jfinal.core.ActionKey;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.apidoc.annotation.ApiParas;

import javax.validation.constraints.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ApiOperation {

    private String value;
    private String notes;
    private String actionKey;
    private ContentType contentType;
    private List<ApiParameter> apiParameters;

    private Class<?> retType;
    private Type[] retGenericTypes;

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
            apiParameters = new ArrayList<>();
        }
        apiParameters.add(parameter);
    }

    public boolean hasParameter(){
        return apiParameters != null && apiParameters.size() > 0;
    }

    public Class<?> getRetType() {
        return retType;
    }

    public void setRetType(Class<?> retType) {
        this.retType = retType;
    }

    public Type[] getRetGenericTypes() {
        return retGenericTypes;
    }

    public void setRetGenericTypes(Type[] retGenericTypes) {
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
        this.actionKey = getActionKey(method, controllerPath);


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
            apiParameter.setHttpMethods(defaultMethods);

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
            }

            if (parameter.getAnnotation(NotEmpty.class) != null) {
                apiParameter.setNotEmpty(true);
            }

            if (parameter.getAnnotation(Email.class) != null) {
                apiParameter.setEmail(true);
            }

            Min min = parameter.getAnnotation(Min.class);
            if (min != null) {
                apiParameter.setMin(min.value());
            }

            Max max = parameter.getAnnotation(Max.class);
            if (max != null) {
                apiParameter.setMax(max.value());
            }

            Pattern pattern = parameter.getAnnotation(Pattern.class);
            if (pattern != null) {
                apiParameter.setPattern(pattern.regexp());
            }

            addApiParameter(apiParameter);
        }
    }

    private static final String SLASH = "/";

    private static String getActionKey(Method method, String controllerPath) {
        String methodName = method.getName();
        ActionKey ak = method.getAnnotation(ActionKey.class);
        String actionKey;
        if (ak != null) {
            actionKey = ak.value().trim();

            if (actionKey.startsWith(SLASH)) {
                //actionKey = actionKey
            } else if (actionKey.startsWith("./")) {
                actionKey = controllerPath + actionKey.substring(1);
            } else {
                actionKey = SLASH + actionKey;
            }
//                        if (!actionKey.startsWith(SLASH)) {
//                            actionKey = SLASH + actionKey;
//                        }
        } else if (methodName.equals("index")) {
            actionKey = controllerPath;
        } else {
            actionKey = controllerPath.equals(SLASH) ? SLASH + methodName : controllerPath + SLASH + methodName;
        }

        return actionKey;
    }
}
