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
package io.jboot.test;

import com.jfinal.kit.LogKit;
import io.jboot.utils.ReflectUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

class MockMethodInfo {

    private Class<?> targetClass;
    private Method targetMethod;
    private boolean firstArgIsTarget;

    private Class<?> mockClass;
    private Method mockMethod;

    public MockMethodInfo(Class<?> testClass, Method testClassMethod, MockMethod mockMethod) {
        this.targetClass = mockMethod.targetClass();

        String targetMethodName = StrUtil.isNotBlank(mockMethod.targetMethod()) ? mockMethod.targetMethod() : testClassMethod.getName();
        this.targetMethod = ReflectUtil.searchMethod(targetClass, m -> {
            if (!m.getName().equals(targetMethodName)) {
                return false;
            }

            Class<?>[] testClassMethodParaTypes = testClassMethod.getParameterTypes();

            if (Arrays.equals(m.getParameterTypes(), testClassMethodParaTypes)) {
                return true;
            }

            if (testClassMethodParaTypes.length > 0 && testClassMethodParaTypes[0] == mockMethod.targetClass()) {
                Class<?>[] newClassMethodParaTypes = new Class[testClassMethodParaTypes.length - 1];
                System.arraycopy(testClassMethodParaTypes, 1, newClassMethodParaTypes, 0, newClassMethodParaTypes.length);

                if (Arrays.equals(m.getParameterTypes(), newClassMethodParaTypes)) {
                    this.firstArgIsTarget = true;
                    return true;
                }
            }
            return false;
        });

        if (targetMethod == null) {
            throw new IllegalStateException("Can not mock the method: \"" + targetMethodName + "\" in class: " + mockMethod.targetClass());
        }

        this.mockClass = testClass;
        this.mockMethod = testClassMethod;

    }


    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

    public Class<?> getMockClass() {
        return mockClass;
    }

    public void setMockClass(Class<?> mockClass) {
        this.mockClass = mockClass;
    }

    public Method getMockMethod() {
        return mockMethod;
    }

    public void setMockMethod(Method mockMethod) {
        this.mockMethod = mockMethod;
    }

    public Object invokeMock(Object obj, Object... args) throws InvocationTargetException, IllegalAccessException {
        if (firstArgIsTarget) {
            Object[] newArgs = new Object[args.length + 1];
            newArgs[0] = obj;
            System.arraycopy(args, 0, newArgs, 1, args.length);
            args = newArgs;
        }

        Object testInstance = MockApp.getInstance().getTestInstance();
        if (testInstance == null) {
            testInstance = newInstance(this.mockClass);
        }

        if (testInstance == null) {
            return null;
        }

        return mockMethod.invoke(testInstance, args);
    }


    private static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            LogKit.logNothing(e);
        }
        return null;
    }

}
