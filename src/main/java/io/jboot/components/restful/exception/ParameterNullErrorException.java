package io.jboot.components.restful.exception;

/**
 * 参数为空错误
 */
public class ParameterNullErrorException extends RuntimeException {

    private String parameterName;


    public String getParameterName() {
        return parameterName;
    }

    public ParameterNullErrorException(String parameterName) {
        super("Parameter '"+parameterName+"' specifies a forced check, but the value is null");
        this.parameterName = parameterName;
    }

    public ParameterNullErrorException(Exception e) {
        super(e);
    }

}
