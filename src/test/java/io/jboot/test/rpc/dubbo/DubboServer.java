package io.jboot.test.rpc.dubbo;


import io.jboot.app.JbootApplication;

public class DubboServer {

    public static void main(String[] args) throws InterruptedException {


        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");

        //开启 @RPCBean 自动暴露功能，默认情况下是自动暴露的，但是 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);

        //设置直连模式，方便调试，默认为注册中心
        JbootApplication.setBootArg("jboot.rpc.callMode", "direct");

        //直连模式的url地址
        JbootApplication.setBootArg("jboot.rpc.directUrl", "127.0.0.1:8000");

        JbootApplication.run(args);

        System.out.println("DubboServer started...");

    }
}
