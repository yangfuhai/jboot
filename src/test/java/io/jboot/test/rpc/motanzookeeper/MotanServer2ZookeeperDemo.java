package io.jboot.test.rpc.motanzookeeper;


import io.jboot.app.JbootApplication;
import io.jboot.app.JbootRpcApplication;

public class MotanServer2ZookeeperDemo {

    public static void main(String[] args)  {


        // 开启 @RPCBean 自动暴露功能，默认情况下是开启的，无需配置，
        // 但是此测试代码的 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);
        JbootApplication.setBootArg("jboot.rpc.type", "motan");



        // motan 的注册中心的协议
        JbootApplication.setBootArg("jboot.rpc.motan.registry.regProtocol", "zookeeper");
        //注册中心地址，即zookeeper的地址
        JbootApplication.setBootArg("jboot.rpc.motan.registry.address", "127.0.0.1:2181");


        //export
        JbootApplication.setBootArg("jboot.rpc.motan.defaultExport", "default:28081");

        JbootRpcApplication.run(args);



        System.out.println("MotanServer2ZookeeperDemo started...");


    }
}
