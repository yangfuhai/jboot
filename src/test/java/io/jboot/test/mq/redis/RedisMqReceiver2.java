package io.jboot.test.mq.redis;


import io.jboot.Jboot;
import io.jboot.app.JbootApplication;

public class RedisMqReceiver2 {

    public static void main(String[] args) {

        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "8002");

        //设置 mq 的相关信息
        JbootApplication.setBootArg("jboot.mq.type", "redis");
        JbootApplication.setBootArg("jboot.mq.channel", "channel1,channel2,myChannel");
        JbootApplication.setBootArg("jboot.mq.redis.host", "127.0.0.1");

        //启动应用程序
        JbootApplication.run(args);

        //添加监听
        Jboot.getMq().addMessageListener((channel, message) -> {
            System.out.println("listener1 receive msg : " + message + ", from channel : " + channel);
        });

        // 只监听 myChannel 这个通道
        Jboot.getMq().addMessageListener((channel, message) -> {
            System.out.println("listener2 receive msg : " + message + ", from channel : " + channel);
        },"myChannel");

        Jboot.getMq().startListening();

        System.out.println("RedisMqReceiver1 started.");
    }
}
