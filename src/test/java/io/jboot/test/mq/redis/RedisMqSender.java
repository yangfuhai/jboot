package io.jboot.test.mq.redis;


import io.jboot.Jboot;
import io.jboot.app.JbootApplication;

import java.util.UUID;

public class RedisMqSender {

    public static void main(String[] args) throws InterruptedException {

        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "8659");

        //设置 mq 的相关信息
        JbootApplication.setBootArg("jboot.mq.type", "redis");
        JbootApplication.setBootArg("jboot.mq.redis.host", "127.0.0.1");

//        JbootApplication.setBootArg("jboot.mq.type", "redis");
//        JbootApplication.setBootArg("jboot.mq.redis.host", "127.0.0.1");

        JbootApplication.setBootArg("jboot.mq.other1.type", "rocketmq");
        JbootApplication.setBootArg("jboot.mq.other1.channel", "channel1,channel2,myChannel");
        JbootApplication.setBootArg("jboot.mq.other1.typeName", "rktmq");
        JbootApplication.setBootArg("jboot.mq.rocket.rktmq.namesrvAddr", "127.0.0.1:9876");

        //启动应用程序
        JbootApplication.run(args);

//        while (true) {

            Jboot.getMq().publish("message from RedisMqSender", "channel1");
            Jboot.getMq().publish("message from RedisMqSender", "channel2");
            Jboot.getMq().publish("message from RedisMqSender", "myChannel");

            Jboot.getMq("other1").enqueue("message from RedisMqSender by enqueue : " + UUID.randomUUID(), "channel1");

            Thread.sleep(2000);
            System.out.println("jboot mq publish success...");
//        }

    }


}
