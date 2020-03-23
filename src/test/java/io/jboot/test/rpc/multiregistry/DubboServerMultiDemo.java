package io.jboot.test.rpc.multiregistry;


import io.jboot.app.JbootApplication;

public class DubboServerMultiDemo {

    public static void main(String[] args)  {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "9002");

        // 开启 @RPCBean 自动暴露功能，默认情况下是开启的，无需配置，
        // 但是此测试代码的 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");




        //两个注册中心，id 分别为 default、zk，默认情况下，如果 service 没指定注册中心，service 会向所有的注册中心发布自己的服务
        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.protocol", "nacos");
        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.address", "127.0.0.1:8848");

        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.zk.protocol", "zookeeper");
        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.zk.address", "127.0.0.1:2181");

        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.zk2.protocol", "zookeeper");
        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.zk2.address", "127.0.0.1:2181");




        //dubbo 的通信协议配置，name 可以不用配置，默认值为 dubbo
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.name", "dubbo");
        //dubbo 的通信协议配置，如果port配置为-1，则会分配一个没有被占用的端口。
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.port", "-1");

        JbootApplication.run(args);



        System.out.println("DubboServer2NacosDemo started...");


    }
}
