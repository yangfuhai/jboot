package io.jboot.app;


import com.jfinal.server.undertow.WebBuilder;

public interface JbootWebBuilderConfiger {

    public void onConfig(WebBuilder webBuilder);
}
