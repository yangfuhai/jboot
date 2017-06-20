package io.jboot.aop.interceptor;


import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Interceptor;
import io.jboot.Jboot;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JFinalInterceptorManager.
 * 1：管理业务层全局拦截器
 * 2：缓存业务层 Class 级拦截器数组。在业务层仅有 injectInters、methodInters 数组未被整体缓存
 * <p>
 * 参考至：InterceptorManager
 */
public class JFinalInterceptorManager {

    public static final Interceptor[] NULL_INTERS = new Interceptor[0];

    // 单例拦截器
    private final ConcurrentHashMap<Class<? extends Interceptor>, Interceptor> singletonMap = new ConcurrentHashMap<Class<? extends Interceptor>, Interceptor>();

    // 业务层 Class 级别拦截器缓存
    private final ConcurrentHashMap<Class<?>, Interceptor[]> serviceClassInters = new ConcurrentHashMap<Class<?>, Interceptor[]>();

    private static final JFinalInterceptorManager me = new JFinalInterceptorManager();

    private JFinalInterceptorManager() {
    }

    public static JFinalInterceptorManager me() {
        return me;
    }


    // 缓存业务层 Class 级拦截器
    public Interceptor[] createServiceInterceptor(Class<?> serviceClass) {
        Interceptor[] result = serviceClassInters.get(serviceClass);
        if (result == null) {
            result = createInterceptor(serviceClass.getAnnotation(Before.class));
            serviceClassInters.put(serviceClass, result);
        }
        return result;
    }

    public Interceptor[] buildServiceMethodInterceptor(Interceptor[] injectInters, Class<?> serviceClass, Method method) {
        return doBuild(injectInters, createServiceInterceptor(serviceClass), serviceClass, method);
    }

    private Interceptor[] doBuild(Interceptor[] injectInters, Interceptor[] classInters, Class<?> targetClass, Method method) {
        Interceptor[] methodInters = createInterceptor(method.getAnnotation(Before.class));

        Class<? extends Interceptor>[] clearIntersOnMethod;
        Clear clearOnMethod = method.getAnnotation(Clear.class);
        if (clearOnMethod != null) {
            clearIntersOnMethod = clearOnMethod.value();
            if (clearIntersOnMethod.length == 0) {    // method 级 @Clear 且不带参
                return methodInters;
            }
        } else {
            clearIntersOnMethod = null;
        }

        Class<? extends Interceptor>[] clearIntersOnClass;
        Clear clearOnClass = targetClass.getAnnotation(Clear.class);
        if (clearOnClass != null) {
            clearIntersOnClass = clearOnClass.value();
            if (clearIntersOnClass.length == 0) {    // class 级 @clear 且不带参
                injectInters = NULL_INTERS;
            }
        } else {
            clearIntersOnClass = null;
        }

        ArrayList<Interceptor> result = new ArrayList<Interceptor>(injectInters.length + classInters.length + methodInters.length);
        for (Interceptor inter : injectInters) {
            result.add(inter);
        }
        if (clearIntersOnClass != null && clearIntersOnClass.length > 0) {
            removeInterceptor(result, clearIntersOnClass);
        }
        for (Interceptor inter : classInters) {
            result.add(inter);
        }
        if (clearIntersOnMethod != null && clearIntersOnMethod.length > 0) {
            removeInterceptor(result, clearIntersOnMethod);
        }
        for (Interceptor inter : methodInters) {
            result.add(inter);
        }
        return result.toArray(new Interceptor[result.size()]);
    }

    private void removeInterceptor(ArrayList<Interceptor> target, Class<? extends Interceptor>[] clearInters) {
        for (Iterator<Interceptor> it = target.iterator(); it.hasNext(); ) {
            Interceptor curInter = it.next();
            if (curInter != null) {
                Class<? extends Interceptor> curInterClass = curInter.getClass();
                for (Class<? extends Interceptor> ci : clearInters) {
                    if (curInterClass == ci) {
                        it.remove();
                        break;
                    }
                }
            } else {
                it.remove();
            }
        }
    }

    public Interceptor[] createInterceptor(Before beforeAnnotation) {
        if (beforeAnnotation == null) {
            return NULL_INTERS;
        }
        return createInterceptor(beforeAnnotation.value());
    }

    public Interceptor[] createInterceptor(Class<? extends Interceptor>[] interceptorClasses) {
        if (interceptorClasses == null || interceptorClasses.length == 0) {
            return NULL_INTERS;
        }

        Interceptor[] result = new Interceptor[interceptorClasses.length];
        try {
            for (int i = 0; i < result.length; i++) {
                result[i] = singletonMap.get(interceptorClasses[i]);
                if (result[i] == null) {
                    result[i] = Jboot.getInjector().getInstance(interceptorClasses[i]);
                    singletonMap.put(interceptorClasses[i], result[i]);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}



