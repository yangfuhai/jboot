package io.jboot.components.restful;

import com.jfinal.core.Action;

import java.io.Serializable;

public class RestfulAction implements Serializable {

    private Action action;

    private String actionKey;

    private String method;

    public RestfulAction(Action action, String actionKey, String method) {
        this.action = action;
        this.actionKey = actionKey;
        this.method = method;
    }

    public Action getAction() {
        return action;
    }

    public RestfulAction setAction(Action action) {
        this.action = action;
        return this;
    }

    public String getActionKey() {
        return actionKey;
    }

    public RestfulAction setActionKey(String actionKey) {
        this.actionKey = actionKey;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public RestfulAction setMethod(String method) {
        this.method = method;
        return this;
    }
}
