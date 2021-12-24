/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.handler;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import io.jboot.aop.InterceptorBuilderManager;
import io.jboot.aop.InterceptorCache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class JbootActionInvocation extends Invocation {

    protected Action action;
    protected Object target;
    protected Object[] args;

    protected Interceptor[] inters;
    protected int index = 0;

    protected Object returnValue;


    protected static InterceptorBuilderManager builderManager = InterceptorBuilderManager.me();

    public JbootActionInvocation(Action action, Controller controller) {

        this.action = action;
        this.inters = buildInterceptors(action);
        this.target = controller;

        this.args = action.getParameterGetter().get(action, controller);
    }


    protected Interceptor[] buildInterceptors(Action action) {

        InterceptorCache.MethodKey key = InterceptorCache.getMethodKey(action.getControllerClass(), action.getMethod());
        Interceptor[] inters = InterceptorCache.get(key);
        if (inters == null) {
            inters = action.getInterceptors();
            inters = builderManager.build(action.getControllerClass(), action.getMethod(), inters);
            InterceptorCache.put(key, inters);
        }

        return inters;
    }


    @Override
    public void invoke() {
        if (index < inters.length) {
            inters[index++].intercept(this);
        } else if (index++ == inters.length) {    // index++ ensure invoke action only one time
            try {
                // Invoke the action
                returnValue = action.getMethod().invoke(target, args);
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                if (t == null) {
                    t = e;
                }
                throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }


    @Override
    public Object getArg(int index) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return args[index];
    }


    @Override
    public void setArg(int index, Object value) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        args[index] = value;
    }


    @Override
    public Object[] getArgs() {
        return args;
    }


    @Override
    public <T> T getTarget() {
        return (T) target;
    }

    /**
     * Return the method of this action.
     * <p>
     * You can getMethod.getAnnotations() to get annotation on action method to do more things
     */
    @Override
    public Method getMethod() {
        return action.getMethod();
    }

    /**
     * Return the method name of this action's method.
     */
    @Override
    public String getMethodName() {
        return action.getMethodName();
    }

    /**
     * Get the return value of the target method
     */
    @Override
    public <T> T getReturnValue() {
        return (T) returnValue;
    }


    @Override
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
    // ---------

    /**
     * Return the controller of this action.
     */
    @Override
    public Controller getController() {
        return (Controller) target;
    }

    /**
     * Return the action key.
     * actionKey = controllerPath + methodName
     */
    @Override
    public String getActionKey() {
        return action.getActionKey();
    }

    /**
     * Return the controller path.
     */
    @Override
    public String getControllerPath() {
        return action.getControllerPath();
    }

    /**
     * 该方法已改名为 getControllerPath()
     */
    @Override
    @Deprecated
    public String getControllerKey() {
        return getControllerPath();
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
