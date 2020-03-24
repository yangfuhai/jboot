package io.jboot.test.rpc.dubbonacos;


import io.jboot.app.JbootApplication;

public class DubboServer1NacosDemo {

    public static void main(String[] args) {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "9997");

        // 开启 @RPCBean 自动暴露功能，默认情况下是开启的，无需配置，
        // 但是此测试代码的 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");



        // dubbo 的注册中心的协议，支持的类型有 dubbo, multicast, zookeeper, redis, consul(2.7.1), sofa(2.7.2), etcd(2.7.2), nacos(2.7.2)
        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.protocol", "nacos");
        //注册中心地址，即zookeeper的地址
        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.address", "127.0.0.1:8848");


        //dubbo 的通信协议配置，name 可以不用配置，默认值为 dubbo
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.name", "dubbo");
        //dubbo 的通信协议配置，如果port配置为-1，则会分配一个没有被占用的端口。
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.port", "28080");



        JbootApplication.run(args);


        System.out.println("DubboServer1NacosDemo started...");


    }
}
