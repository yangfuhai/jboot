package io.jboot.components.restful;


import com.jfinal.kit.JsonKit;
import io.jboot.components.restful.annotation.PathVariable;
import io.jboot.components.restful.annotation.RequestBody;
import io.jboot.components.restful.annotation.RequestHeader;
import io.jboot.components.restful.annotation.RequestParam;
import io.jboot.components.restful.exception.ParameterNullErrorException;
import io.jboot.components.restful.exception.ParameterParseErrorException;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class RestfulUtils {

    /**
     * 从url中解析路径参数
     *
     * @param url
     * @param actionKey
     * @return
     */
    public static Map<String, String> parsePathVariables(String url, String actionKey) {
        if (actionKey.contains("{") && actionKey.contains("}")) {
            Map<String, String> pathVariables = new HashMap<>();
            String[] paths = url.split("/");
            String[] _paths = actionKey.split("/");
            for (int i = 0; i < paths.length; i++) {
                if (_paths[i].startsWith("{") && _paths[i].endsWith("}")) {
                    String pathKey = _paths[i].substring(1, _paths[i].length() - 1);
                    String value = paths[i];
                    pathVariables.put(pathKey, value);
                }
            }
            return pathVariables;
        } else {
            return null;
        }
    }

    /**
     * 转换请求action请求的参数信息
     *
     * @param target
     * @param actionKey
     * @param actionMethod
     * @param request
     * @param rawData
     * @return
     * @throws ParameterNullErrorException
     * @throws ParameterParseErrorException
     */
    public static Object[] parseActionMethodParameters(String target, String actionKey, Method actionMethod, HttpServletRequest request, String rawData)
            throws ParameterNullErrorException, ParameterParseErrorException {
        Object[] args = new Object[actionMethod.getParameters().length];
        for (int i = 0; i < actionMethod.getParameters().length; i++) {
            Parameter parameter = actionMethod.getParameters()[i];
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
            RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            String parameterName = parameter.getName();
            String values[];
            if (requestParam != null) {
                if (StrUtil.isNotBlank(requestParam.value())) {
                    parameterName = requestParam.value();
                }
                values = request.getParameterValues(parameterName);
                parameter.getType();
                args[i] = parseRequestParamToParameter(values, parameterName, parameter.getType());
                if (args[i] == null && requestParam.required()) {
                    //要求参数为空，但是却并没有提供参数
                    throw new ParameterNullErrorException(parameterName);
                }
            } else if (requestBody != null) {
                args[i] = parseRequestBodyToParameter(rawData, parameterName, parameter.getType());
            } else if (requestHeader != null) {
                if (StrUtil.isNotBlank(requestHeader.value())) {
                    parameterName = requestHeader.value();
                }
                String value = request.getHeader(parameterName);
                args[i] = parseRequestHeaderToParameter(value, parameterName, parameter.getType());
                if (args[i] == null && requestHeader.required()) {
                    //要求参数为空，但是却并没有提供参数
                    throw new ParameterNullErrorException(parameterName);
                }
            } else if (pathVariable != null) {
                if (StrUtil.isNotBlank(pathVariable.value())) {
                    parameterName = pathVariable.value();
                }
                args[i] = parsePathVariableToParameter(target, actionKey, parameterName, parameter.getType());
            } else {
                args[i] = null;
            }
        }
        return args;
    }

    /**
     * 比对url请求路径
     *
     * @param sourcePaths action配置的原路径
     * @param targetPaths 请求的目标路径
     * @return
     */
    public static boolean comparePaths(String[] sourcePaths, String[] targetPaths) {
        int matchingCount = 0;
        for (int i = 0; i < sourcePaths.length; i++) {
            if (sourcePaths[i].equals(targetPaths[i])
                    || (sourcePaths[i].startsWith("{") && sourcePaths[i].endsWith("}"))) {
                matchingCount += 1;
            }
        }
        return matchingCount == sourcePaths.length;
    }

    private static Object parseRequestParamToParameter(String[] value, String name, Class<?> parameterTypeClass) {
        if(parameterTypeClass.isArray()){
            Object [] objects = new Object[value.length];
            for (int i = 0; i < value.length; i++) {
                objects[i] = parseCommonValue(value[i], name, parameterTypeClass);
            }
            return objects;
        } else {
            if(value != null && value.length > 0){
                return parseCommonValue(value[0], name, parameterTypeClass);
            }
        }

        return null;
    }

    private static Object parseRequestHeaderToParameter(String header, String name, Class<?> parameterTypeClass) {
        return parseCommonValue(header, name, parameterTypeClass);
    }

    private static Object parseRequestBodyToParameter(String body, String name, Class<?> parameterTypeClass) {
        //先当作基本数据来转换
        Object value = parseCommonValue(body, name, parameterTypeClass);
        if(value == null){
            value = JsonKit.parse(body, parameterTypeClass);
        }
        return value;
    }

    private static Object parsePathVariableToParameter(String target, String actionKey, String parameterName, Class<?> parameterTypeClass) {
        Map<String, String> pathVariables = parsePathVariables(target, actionKey);
        String value = pathVariables.get(parameterName);
        return parseCommonValue(value, parameterName, parameterTypeClass);
    }

    /**
     * 转换基本类型参数，目前支持string,int,double,float,boolean,long基本类型数据
     * @param value
     * @param name
     * @param parameterTypeClass
     * @return
     */
    private static Object parseCommonValue(String value, String name, Class<?> parameterTypeClass) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        if (parameterTypeClass.equals(String.class)) {
            return value;
        } else if (parameterTypeClass.equals(int.class)) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                throw new ParameterParseErrorException(value, name, parameterTypeClass);
            }
        } else if (parameterTypeClass.equals(double.class)) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                throw new ParameterParseErrorException(value, name, parameterTypeClass);
            }
        } else if (parameterTypeClass.equals(float.class)) {
            try {
                return Float.valueOf(value);
            } catch (NumberFormatException e) {
                throw new ParameterParseErrorException(value, name, parameterTypeClass);
            }
        } else if (parameterTypeClass.equals(boolean.class)) {
            return Boolean.valueOf(value);
        } else if (parameterTypeClass.equals(long.class)) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException e) {
                throw new ParameterParseErrorException(value, name, parameterTypeClass);
            }
        } else {
            return null;
        }
    }

}
