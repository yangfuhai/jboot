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
import com.jfinal.core.Action;
import com.jfinal.core.Controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class JbootActionReporterInvocation extends JbootActionInvocation {


    protected static ThreadLocal<List<Interceptor>> invokedInterceptors = ThreadLocal.withInitial(ArrayList::new);
    protected static ThreadLocal<Boolean> controllerInvokeFlag = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public JbootActionReporterInvocation(Action action, Controller controller) {
        super(action, controller);
    }


    @Override
    public void invoke() {
        if (index < inters.length) {
            Interceptor interceptor = inters[index++];
            invokedInterceptors.get().add(interceptor);
            interceptor.intercept(this);
        } else if (index++ == inters.length) {    // index++ ensure invoke action only one time
            try {
                // Invoke the action
                controllerInvokeFlag.set(true);
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


    public static List<Interceptor> getInvokedInterceptor() {
        return invokedInterceptors.get();
    }

    public static boolean isControllerInvoked() {
        return controllerInvokeFlag.get();
    }


    public static void clear() {
        invokedInterceptors.get().clear();
        invokedInterceptors.remove();
        controllerInvokeFlag.remove();
    }

    public Interceptor[] getInters() {
        return inters;
    }


}
