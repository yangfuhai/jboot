/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.aop.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.exception.JbootException;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;


public class JFinalBeforeInvocation extends Invocation {


    private Interceptor[] inters;
    private MethodInvocation methodInvocation;
    private Object[] args;

    private int index = 0;


    public JFinalBeforeInvocation(MethodInvocation methodInvocation, Interceptor[] inters, Object[] args) {
        this.methodInvocation = methodInvocation;
        this.inters = inters;
        this.args = args;
    }


    @Override
    public void invoke() {
        if (index < inters.length) {
            inters[index++].intercept(this);
        } else if (index++ == inters.length) {    // index++ ensure invoke action only one time
            try {
                setReturnValue(methodInvocation.proceed());
            } catch (Throwable throwable) {
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                } else {
                    throw new JbootException(throwable.getMessage(), throwable);
                }
            }
        }
    }


    @Override
    public Method getMethod() {
        return methodInvocation.getMethod();
    }

    @Override
    public String getMethodName() {
        return getMethod().getName();
    }

    @Override
    public <T> T getTarget() {
        return (T) methodInvocation.getThis();
    }

    @Override
    public Object getArg(int index) {
        if (index >= args.length)
            throw new ArrayIndexOutOfBoundsException();
        return args[index];
    }

    @Override
    public void setArg(int index, Object value) {
        if (index >= args.length)
            throw new ArrayIndexOutOfBoundsException();
        args[index] = value;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }
}
