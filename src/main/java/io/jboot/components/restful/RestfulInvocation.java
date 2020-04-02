package io.jboot.components.restful;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;

public class RestfulInvocation extends Invocation  {

    private Action action;


    public RestfulInvocation(Action action, Controller controller, Object[] args) {
        super(controller, action.getMethod(), action.getInterceptors(), new RestfulCallback(action, controller), args);
        this.action = action;
    }


    @Override
    public Controller getController() {
        return super.getTarget();
    }

    /**
     * Return the action key.
     * actionKey = controllerKey + methodName
     */
    @Override
    public String getActionKey() {
        return action.getActionKey();
    }

    /**
     * Return the controller key.
     */
    @Override
    public String getControllerKey() {
        return action.getControllerKey();
    }

    /**
     * Return view path of this controller.
     */
    @Override
    public String getViewPath() {
        return action.getViewPath();
    }

    /**
     * return true if it is action invocation.
     */
    @Override
    public boolean isActionInvocation() {
        return action != null;
    }


}
