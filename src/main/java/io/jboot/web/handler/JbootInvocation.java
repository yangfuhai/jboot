/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.core.JFinal;
import io.jboot.aop.InterceptorBuilderManager;
import io.jboot.aop.cglib.JbootCglibProxyFactory;
import io.jboot.utils.ArrayUtil;
import io.jboot.web.fixedinterceptor.FixedInterceptors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class JbootInvocation extends Invocation {

    private Action action;
    private Object target;
    private Object[] args;

    private Interceptor[] inters;
    private int index = 0;

    private Object returnValue;

    private static ThreadLocal<List<Interceptor>> invokedInterceptors = ThreadLocal.withInitial(ArrayList::new);
    private static boolean devMode = JFinal.me().getConstants().getDevMode();

    public JbootInvocation(Action action, Controller controller) {
        super(action, controller);

        this.action = action;
        this.inters = buildInterceptors(action);
        this.target = controller;

        // this.args = NULL_ARGS;
        this.args = action.getParameterGetter().get(action, controller);
    }


    private Interceptor[] buildInterceptors(Action action) {

        JbootCglibProxyFactory.MethodKey key = JbootCglibProxyFactory.IntersCache.getMethodKey(action.getControllerClass(), action.getMethod());
        Interceptor[] inters = JbootCglibProxyFactory.IntersCache.get(key);
        if (inters == null) {

            // jfinal 原生的构建
            inters = ArrayUtil.concat(FixedInterceptors.me().all(), action.getInterceptors());


            // builder 再次构建
            inters = InterceptorBuilderManager.me().build(action.getControllerClass(), action.getMethod(), inters);

            JbootCglibProxyFactory.IntersCache.put(key, inters);
        }

        return inters;
    }


    @Override
    public void invoke() {
        if (index < inters.length) {
            Interceptor interceptor = inters[index++];
            if (devMode) {
                invokedInterceptors.get().add(interceptor);
            }
            interceptor.intercept(this);
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
    public Object getReturnValue() {
        return returnValue;
    }

    @Override
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }


    public static List<Interceptor> getInvokedInterceptor() {
        return invokedInterceptors.get();
    }


    public static void clear() {
        invokedInterceptors.get().clear();
        invokedInterceptors.remove();
    }


}
