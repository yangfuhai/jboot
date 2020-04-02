package io.jboot.components.restful.exception;

/**
 * 请求方法错误
 */
public class RequestMethodErrorException extends RuntimeException {

    private String actionKey;

    private String actionMethod;

    private String target;

    private String targetMethod;

    public String getActionKey() {
        return actionKey;
    }

    public String getActionMethod() {
        return actionMethod;
    }

    public String getTarget() {
        return target;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public RequestMethodErrorException(String actionKey, String actionMethod, String target, String targetMethod) {
        super("'" + target + "' is specified as a '" + actionMethod + "' request. '" + targetMethod + "' requests are not supported");
        this.actionKey = actionKey;
        this.actionMethod = actionMethod;
        this.target = target;
        this.targetMethod = targetMethod;
    }
}
