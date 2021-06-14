package io.jboot.test;

import io.jboot.aop.InterceptorCache;
import io.jboot.aop.cglib.JbootCglibCallback;
import io.jboot.utils.ClassUtil;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class MockMethodInterceptor extends JbootCglibCallback {

    private static final Map<InterceptorCache.MethodKey, MockMethodInfo> methods = new HashMap();

    public static void addMethod(MockMethodInfo value) {
        InterceptorCache.MethodKey methodKey = InterceptorCache.getMethodKey(value.getTargetClass(), value.getTargetMethod());
        methods.put(methodKey, value);
    }


    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        Class<?> targetClass = ClassUtil.getUsefulClass(target.getClass());
        InterceptorCache.MethodKey methodKey = InterceptorCache.getMethodKey(targetClass, method);

        if (!methods.containsKey(methodKey)) {
            return super.intercept(target, method, args, methodProxy);
        }

        MockMethodInfo methodInfo = methods.get(methodKey);
        return methodInfo.invokeMock(target, args);
    }
}
