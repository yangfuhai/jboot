package io.jboot.test;

import io.jboot.aop.cglib.JbootCglibCallback;
import io.jboot.utils.ReflectUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

class MockMethodInfo extends JbootCglibCallback {

    private Class<?> targetClass;
    private Method targetMethod;

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

                if (Arrays.equals(m.getParameterTypes(), testClassMethodParaTypes)) {
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
        return mockMethod.invoke(obj, args);
    }
}
