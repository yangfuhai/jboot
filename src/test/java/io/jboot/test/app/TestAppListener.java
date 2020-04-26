package io.jboot.test.app;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.core.listener.JbootAppListener;
import io.jboot.web.fixedinterceptor.FixedInterceptors;

public class TestAppListener implements JbootAppListener {
    @Override
    public void onInit() {
        System.out.println("TestAppListener.onInit");
    }

    @Override
    public void onConstantConfig(Constants constants) {
        System.out.println("TestAppListener.onConstantConfig");
    }

    @Override
    public void onRouteConfig(Routes routes) {
        System.out.println("TestAppListener.onRouteConfig");
    }

    @Override
    public void onEngineConfig(Engine engine) {
        System.out.println("TestAppListener.onEngineConfig");
    }

    @Override
    public void onPluginConfig(JfinalPlugins plugins) {
        System.out.println("TestAppListener.onPluginConfig");
    }

    @Override
    public void onInterceptorConfig(Interceptors interceptors) {
        System.out.println("TestAppListener.onInterceptorConfig");
    }

    @Override
    public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors) {
        System.out.println("TestAppListener.onFixedInterceptorConfig");
    }

    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {
        System.out.println("TestAppListener.onHandlerConfig");
    }

    @Override
    public void onStartBefore() {
        System.out.println("TestAppListener.onStartBefore");
    }

    @Override
    public void onStart() {
        System.out.println("TestAppListener.onStart");
    }

    @Override
    public void onStartFinish() {
        System.out.println("TestAppListener.onStartFinish");
    }

    @Override
    public void onStop() {
        System.out.println("TestAppListener.onStop");
    }
}
