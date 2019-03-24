/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.fixedinterceptor;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import java.lang.reflect.Method;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.handler
 */
public class FixedInvocation extends Invocation {

    private Invocation origin;


    private FixedInterceptor[] inters = FixedInterceptors.me().all();
    private int index = 0;


    public FixedInvocation(Invocation invocation) {
        this.origin = invocation;
    }


    @Override
    public void invoke() {
        if (index < inters.length) {
            inters[index++].intercept(this);
        } else if (index++ == inters.length) {    // index++ ensure invoke action only one time
            origin.invoke();
        }
    }


    @Override
    public Method getMethod() {
        return origin.getMethod();
    }

    @Override
    public Controller getController() {
        return origin.getController();
    }

    @Override
    public String getActionKey() {
        return origin.getActionKey();
    }

    @Override
    public String getControllerKey() {
        return origin.getControllerKey();
    }

    @Override
    public String getMethodName() {
        return origin.getMethodName();
    }

    @Override
    public boolean isActionInvocation() {
        return true;
    }

    @Override
    public Object getArg(int index) {
        return origin.getArg(index);
    }

    @Override
    public void setArg(int index, Object value) {
        origin.setArg(index, value);
    }

    @Override
    public Object[] getArgs() {
        return origin.getArgs();
    }

    @Override
    public <T> T getTarget() {
        return origin.getTarget();
    }

    @Override
    public <T> T getReturnValue() {
        return origin.getReturnValue();
    }

    @Override
    public void setReturnValue(Object returnValue) {
        origin.setReturnValue(returnValue);
    }

    @Override
    public String getViewPath() {
        return origin.getViewPath();
    }

    public Invocation getOrigin() {
        return origin;
    }

}
