package io.jboot.test.rpc.dubbo3.provider;

import io.jboot.app.JbootSimpleApplication;

public class ProviderStarter {
    public static void main(String[] args) {

        /*
         * undertow.devMode=true
         * undertow.port=8081
         * jboot.rpc.type = dubbo
         * jboot.rpc.autoExportEnable=true
         * jboot.rpc.dubbo.application.version = 1.0
         * jboot.rpc.dubbo.protocol.name=dubbo
         * jboot.rpc.dubbo.protocol.port=28080
         * jboot.rpc.dubbo.registry.protocol=nacos
         * jboot.rpc.dubbo.registry.address=127.0.0.1:8848
         * jboot.rpc.dubbo.application.name = BlogProvider
         *
         * jboot.rpc.dubbo.registry.registerMode=instance
         */

        JbootSimpleApplication.setBootArg("jboot.rpc.type","dubbo");
        JbootSimpleApplication.setBootArg("jboot.rpc.autoExportEnable","true");

        JbootSimpleApplication.setBootArg("jboot.rpc.dubbo.protocol.name","dubbo");
        JbootSimpleApplication.setBootArg("jboot.rpc.dubbo.protocol.port","28080");
        JbootSimpleApplication.setBootArg("jboot.rpc.dubbo.registry.protocol","nacos");
        JbootSimpleApplication.setBootArg("jboot.rpc.dubbo.registry.address","127.0.0.1:8848");
        JbootSimpleApplication.setBootArg("jboot.rpc.dubbo.application.name","BlogProvider");
        JbootSimpleApplication.setBootArg("jboot.rpc.dubbo.application.version","1.0");

        //dubbo3 主要是添加这个配置
        JbootSimpleApplication.setBootArg("jboot.rpc.dubbo.registry.registerMod","instance");


        JbootSimpleApplication.run(args);

        System.out.println("Provider Started");
    }
}