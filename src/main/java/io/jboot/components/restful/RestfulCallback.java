package io.jboot.components.restful;

import com.jfinal.proxy.Callback;

public class RestfulCallback implements Callback {

    private RestfulAction restfulAction;

    private Object target;

    public RestfulCallback(RestfulAction restfulAction, Object target) {
        this.restfulAction = restfulAction;
        this.target = target;
    }

    public RestfulAction getRestfulAction() {
        return restfulAction;
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public Object call(Object[] args) throws Throwable {
        return restfulAction.getMethod().invoke(target, args);
    }

}
