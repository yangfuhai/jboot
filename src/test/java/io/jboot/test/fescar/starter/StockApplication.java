package io.jboot.test.fescar.starter;


import io.jboot.app.JbootApplication;

public class StockApplication {

    public static void main(String[] args) {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "8081");
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
        JbootApplication.setBootArg("jboot.rpc.registryType", "zookeeper");//注册中心的类型：zookeeper
        JbootApplication.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:2181");//注册中心，即zookeeper的地址

        //开启 @RPCBean 自动暴露功能，默认情况下是自动暴露的，但是 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);
        JbootApplication.setBootArg("jboot.rpc.filter", "fescar");
        JbootApplication.setBootArg("jboot.fescar.enable", true);
        JbootApplication.setBootArg("jboot.fescar.failureHandler", "com.alibaba.fescar.tm.api.DefaultFailureHandlerImpl");
        JbootApplication.setBootArg("jboot.fescar.applicationId", "Dubbo_Fescar_Stock_Service");
        JbootApplication.setBootArg("jboot.fescar.txServiceGroup", "dubbo_fescar_tx_group");

        JbootApplication.setBootArg("jboot.datasource.type", "mysql");
        JbootApplication.setBootArg("jboot.datasource.factory", "fescar");
        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://139.127.108.187:9527/ilife-mall2.0?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "xjs123456789");
        
        JbootApplication.setBootArg("jboot.model.unscanPackage", "*");
        JbootApplication.setBootArg("jboot.model.scanPackage", "io.jboot.test.fescar.commons");

        JbootApplication.run(args);


        System.out.println("StockApplication started...");


    }
}
