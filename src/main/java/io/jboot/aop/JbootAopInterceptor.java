package io.jboot.aop;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class JbootAopInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        JbootAopInvocation invocation = new JbootAopInvocation(inv);
        invocation.invoke();
    }

}
