package io.jboot.web.controller.interceptor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * Guice容器对controller的自动注入
 */
public class GuiceInterceptor implements Interceptor {

    private Injector injector;

    public GuiceInterceptor() {
//        injector = Guice.createInjector(new Module() {
//            @Override
//            public void configure(Binder binder) {
//                binder..bind(JbootService.class).asEagerSingleton();
//            }
//        });

        injector = Guice.createInjector();
    }


    @Override
    public void intercept(Invocation inv) {
        injector.injectMembers(inv.getController());
        inv.invoke();
    }


}
