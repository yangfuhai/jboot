package io.jboot.test.seata.starter;


import io.jboot.app.JbootApplication;

public class AccountApplicaiton {

    public static void main(String[] args)  {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "8082");
        JbootApplication.setBootArg("jboot.rpc.filter", "seata");
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
        JbootApplication.setBootArg("jboot.rpc.registryType", "zookeeper");//注册中心的类型：zookeeper
        JbootApplication.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:2181");//注册中心，即zookeeper的地址

        //开启 @RPCBean 自动暴露功能，默认情况下是自动暴露的，但是 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);

        JbootApplication.setBootArg("jboot.seata.enable", true);
        JbootApplication.setBootArg("jboot.seata.failureHandler", "io.seata.tm.api.DefaultFailureHandlerImpl");
        JbootApplication.setBootArg("jboot.seata.applicationId", "Dubbo_Seata_Account_Service");
        JbootApplication.setBootArg("jboot.seata.txServiceGroup", "dubbo_seata_tx_group");
        JbootApplication.setBootArg("jboot.datasource.type", "mysql");
        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/mini?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "zhang123");
        JbootApplication.setBootArg("jboot.model.unscanPackage", "*");
        JbootApplication.setBootArg("jboot.model.scanPackage", "io.jboot.test.seata.commons");

        JbootApplication.run(args);



        System.out.println("AccountApplicaiton started...");


    }
}
