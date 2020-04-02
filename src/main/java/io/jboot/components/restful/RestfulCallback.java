package io.jboot.components.restful;

import com.jfinal.core.Action;
import com.jfinal.proxy.Callback;

public class RestfulCallback implements Callback {

    private Action action;
    private Object target;

    public RestfulCallback(Action restfulAction, Object target) {
        this.action = restfulAction;
        this.target = target;
    }

    @Override
    public Object call(Object[] args) throws Throwable {
        return action.getMethod().invoke(target, args);
    }

}
