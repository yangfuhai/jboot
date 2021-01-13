package io.jboot.test.mq.rabbit;


import io.jboot.Jboot;
import io.jboot.app.JbootApplication;

/**
 * 开始之前 先通过通过 docker 把 rabbitmq 运行起来
 * docker run -d  -p 15672:15672 -p 5672:5672 rabbitmq:management
 */
public class RabbitMqReceiver2 {

    public static void main(String[] args) {

        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "8002");

        //设置 mq 的相关信息
        JbootApplication.setBootArg("jboot.mq.type", "rabbitmq");
        JbootApplication.setBootArg("jboot.mq.channel", "channel2");


        //以下可以不用配置，是默认信息
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.username", "guest");
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.password", "guest");
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.host", "127.0.0.1");
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.port", "5672");
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.virtualHost", "");

        //启动应用程序
        JbootApplication.run(args);



        // 只监听 myChannel 这个通道
        Jboot.getMq().addMessageListener((channel, message) -> {
            System.out.println("Receive msg: " + message + ", from channel: " + channel);
        },"channel2");


        Jboot.getMq().startListening();

        System.out.println("RabbitMqReceiver2 started.");
    }
}
