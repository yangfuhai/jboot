package io.jboot.test.rpc.dubbo;


import io.jboot.app.JbootApplication;
import io.jboot.app.JbootRpcApplication;

public class DubboServer {

    public static void main(String[] args) throws InterruptedException {


        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");

        // 开启 @RPCBean 自动暴露功能，默认情况下是开启的，无需配置，
        // 但是此测试代码的 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);

        //dubbo 的通信协议配置
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.name", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.port", "28080");


        JbootRpcApplication.run(args);

        System.out.println("DubboServer started...");

    }
}
