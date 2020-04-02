package io.jboot.components.restful.exception;

/**
 * 参数类型错误
 */
public class ParameterParseErrorException extends RuntimeException {


    private String parameterValue;

    private String parameterName;

    private Class<?> parameterType;

    public String getParameterValue() {
        return parameterValue;
    }

    public String getParameterName() {
        return parameterName;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public ParameterParseErrorException(String parameterValue, String parameterName, Class<?> parameterType) {
        super("Error resolving parameter '" + parameterName + "', unable to match value '"
                + parameterValue + "' to specified type '" + parameterType.getName() + "'");
        this.parameterValue = parameterValue;
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }

    public ParameterParseErrorException(Exception e) {
        super(e);
    }

}
