package io.jboot.test.gateway;

import io.jboot.components.gateway.GatewayInterceptor;
import io.jboot.components.gateway.GatewayInvocation;

public class TestInterceptor implements GatewayInterceptor {

    @Override
    public void intercept(GatewayInvocation inv) {
        System.out.println("TestInterceptor.invoke....");

        inv.getProxy().addHeader("aaa", "bbbb").addHeader("cccc", "eeeee");

        inv.invoke();

        System.out.println(inv.getResponse().getHeaderNames());

        inv.getResponse().addHeader("aaa", "bbb");
    }
}
