package io.jboot.aop;

import com.jfinal.aop.*;
import io.jboot.aop.annotation.Bean;
import io.jboot.aop.annotation.BeanExclude;
import io.jboot.aop.interceptor.JFinalBeforeInvocation;
import io.jboot.core.mq.JbootmqMessageListener;
import io.jboot.event.JbootEventListener;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassScanner;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class JbootAopFactory extends AopFactory implements Interceptor {

    public JbootAopFactory(){
        initMapping();
    }

    @Override
    protected Object createObject(Class<?> targetClass) throws ReflectiveOperationException {
        return com.jfinal.aop.Enhancer.enhance(targetClass, this);
    }


    @Override
    public void inject(Class<?> targetClass, Object targetObject, int injectDepth) throws ReflectiveOperationException {
        if ((injectDepth--) <= 0) {
            return;
        }

        targetClass = getUsefulClass(targetClass);
        Field[] fields = targetClass.getDeclaredFields();
        if (fields.length == 0) {
            return;
        }

        for (Field field : fields) {
            Inject inject = field.getAnnotation(Inject.class);
            javax.inject.Inject javaxInject = field.getAnnotation(javax.inject.Inject.class);
            if (inject == null && javaxInject == null) {
                continue;
            }

            Class<?> fieldInjectedClass = inject == null ? Void.class : inject.value();
            if (fieldInjectedClass == Void.class) {
                fieldInjectedClass = field.getType();
                fieldInjectedClass = getMappingClass(fieldInjectedClass);
            }

            Singleton si = fieldInjectedClass.getAnnotation(Singleton.class);
            boolean singleton = (si != null ? si.value() : this.singleton);

            Object fieldInjectedObject = getOrCreateObject(fieldInjectedClass, singleton);
            field.setAccessible(true);
            field.set(targetObject, fieldInjectedObject);

            // 递归调用，为当前被注入的对象进行注入
            this.inject(fieldInjectedObject.getClass(), fieldInjectedObject, injectDepth);
        }
    }


    @Override
    public void intercept(Invocation inv) {


        JFinalBeforeInvocation invocation = new JFinalBeforeInvocation(inv);
        invocation.invoke();

    }

    private static Class[] default_excludes = new Class[]{JbootEventListener.class, JbootmqMessageListener.class, Serializable.class};

    private void initMapping() {

        List<Class> classes = ClassScanner.scanClassByAnnotation(Bean.class, true);
        for (Class implClass : classes) {

            Class<?>[] interfaceClasses = implClass.getInterfaces();

            if (interfaceClasses == null || interfaceClasses.length == 0) {
                continue;
            }

            BeanExclude beanExclude = (BeanExclude) implClass.getAnnotation(BeanExclude.class);

            //对某些系统的类 进行排除，例如：Serializable 等
            Class[] excludes = beanExclude == null
                    ? default_excludes
                    : ArrayUtils.concat(default_excludes, beanExclude.value());

            for (Class interfaceClass : interfaceClasses) {
                if (inExcludes(interfaceClass,excludes) == false){
                    Aop.addMapping(interfaceClass, implClass);
                }
            }
        }
    }

    private boolean inExcludes(Class interfaceClass, Class[] excludes) {
        for (Class ex : excludes) {
            if (ex.isAssignableFrom(interfaceClass)) {
               return true;
            }
        }
        return false;
    }
}
