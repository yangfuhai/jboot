package io.jboot.test.aop;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class NameInterceptor implements Interceptor {
    private String name;

    public NameInterceptor(String name) {
        this.name = name;
    }

    @Override
    public void intercept(Invocation inv) {

    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "NameInterceptor{" +
                "name='" + name + '\'' +
                '}';
    }
}
