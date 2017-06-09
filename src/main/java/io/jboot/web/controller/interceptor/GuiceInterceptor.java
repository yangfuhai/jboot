package io.jboot.web.controller.interceptor;

import com.google.inject.Injector;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;

/**
 * Guice容器对controller的自动注入
 */
public class GuiceInterceptor implements Interceptor {

    private Injector injector;

    public GuiceInterceptor() {
        injector = Jboot.getInjector();
    }


    @Override
    public void intercept(Invocation inv) {
        injector.injectMembers(inv.getController());
        inv.invoke();
    }


}
