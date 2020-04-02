package io.jboot.components.restful;

import com.jfinal.core.Action;
import com.jfinal.render.Render;


public abstract class RestfulErrorRender extends Render {

    private String target;

    private Action action;

    private Exception error;

    public void init (String target, Action action, Exception error) {
        this.target = target;
        this.action = action;
        this.error = error;
    }

    protected RestfulErrorRender() {
    }

    protected String getTarget() {
        return target;
    }

    protected Action getAction() {
        return action;
    }

    protected Exception getError() {
        return error;
    }
}
