package io.jboot.rpc.dubbo;


import io.jboot.app.JbootApplication;

public class DubboServer {

    public static void main(String[] args) throws InterruptedException {


        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.callMode", "redirect");//直连模式，默认为注册中心
        JbootApplication.setBootArg("jboot.rpc.directUrl", "127.0.0.1:8000");//直连模式的url地址


        JbootApplication.run(args);

        System.out.println("DubboServer started...");


    }
}
