package io.jboot.test.mq.rocketmq;


import io.jboot.Jboot;
import io.jboot.app.JbootApplication;

public class RocketmqSender {

    public static void main(String[] args) throws InterruptedException {

        //Undertow端口号配置
//        JbootApplication.setBootArg("undertow.port", "8000");

        //设置 mq 的相关信息
        JbootApplication.setBootArg("jboot.mq.type", "rocketmq");
        JbootApplication.setBootArg("jboot.mq.rocket.namesrvAddr", "127.0.0.1:9876");

        //启动应用程序
        JbootApplication.run(args);

        int i = 0;

        while (i<100) {

//            Jboot.getMq().publish("message from RocketmqSender", "channel1");
//            Jboot.getMq().publish("message from RocketmqSender", "channel2");
//            Jboot.getMq().publish("message from RocketmqSender", "myChannel");
//
            Jboot.getMq().enqueue("message from RocketmqSender by enqueue : " +i, "channel1");

//            Thread.sleep(2000);
            System.out.println("jboot mq publish success...");
            i++;
        }

    }


}
