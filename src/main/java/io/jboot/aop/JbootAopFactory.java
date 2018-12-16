package io.jboot.aop;

import com.jfinal.aop.AopFactory;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.aop.interceptor.JFinalBeforeInvocation;

public class JbootAopFactory extends AopFactory implements Interceptor {

    @Override
    protected Object createObject(Class<?> targetClass) throws ReflectiveOperationException {
        return com.jfinal.aop.Enhancer.enhance(targetClass, this);
    }




    @Override
    public void intercept(Invocation inv) {



        JFinalBeforeInvocation invocation = new JFinalBeforeInvocation(inv);
        invocation.invoke();

//        return invocation.getReturnValue();
    }
}
