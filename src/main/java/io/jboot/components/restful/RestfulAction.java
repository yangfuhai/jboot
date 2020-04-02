package io.jboot.components.restful;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;

import java.lang.reflect.Method;

public class RestfulAction extends Action {

    public RestfulAction(String controllerKey, String actionKey, Class<? extends Controller> controllerClass, Method method, String methodName, Interceptor[] interceptors, String viewPath) {
        super(controllerKey, actionKey, controllerClass, method, methodName, interceptors, viewPath);
    }
}
