package io.jboot.test.rpc.dubbo3.comsumer;

import io.jboot.app.JbootApplication;

public class ConsumerStarter {
    public static void main(String[] args) {

        /*
         * undertow.devMode=true
         * undertow.port=8082
         * jboot.rpc.type = dubbo
         * jboot.rpc.autoExportEnable=true
         * jboot.rpc.application.service-discovery.migration=FORCE_APPLICATION
         * jboot.rpc.dubbo.protocol.name=dubbo
         * jboot.rpc.dubbo.protocol.port=28080
         * jboot.rpc.dubbo.registry.protocol=nacos
         * jboot.rpc.dubbo.registry.address=127.0.0.1:8848
         * jboot.rpc.dubbo.application.name = BlogConsumer
         * jboot.rpc.dubbo.application.version = 1.0
         */

        JbootApplication.setBootArg("undertow.devMode","true");
        JbootApplication.setBootArg("undertow.port","8082");

        JbootApplication.setBootArg("jboot.rpc.type","dubbo");
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable","true");

        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.name","dubbo");
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.port","28080");
        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.protocol","nacos");
        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.address","127.0.0.1:8848");
        JbootApplication.setBootArg("jboot.rpc.dubbo.application.name","BlogConsumer");
        JbootApplication.setBootArg("jboot.rpc.dubbo.application.version","1.0");

        //dubbo3 主要是增加这个配置
        JbootApplication.setBootArg("jboot.rpc.application.service-discovery.migration","FORCE_APPLICATION");



        JbootApplication.run(args);
        System.out.println("Consumer Started");
    }
}