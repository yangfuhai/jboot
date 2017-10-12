package io.jboot.aop.web;

import com.google.inject.Injector;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.aop.JbootInjectManager;

/**
 * 用于对controller的自动注入
 * 注意：如果 Controller通过 @Clear 来把此 拦截器给清空，那么此方法（action）注入将会失效
 */
public class ControllerInjectInterceptor implements Interceptor {

    private Injector injector;

    public ControllerInjectInterceptor() {
        injector = JbootInjectManager.me().getInjector();
    }


    @Override
    public void intercept(Invocation inv) {
        injector.injectMembers(inv.getController());
        inv.invoke();
    }


}
