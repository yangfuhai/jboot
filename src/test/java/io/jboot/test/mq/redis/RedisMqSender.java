package io.jboot.test.mq.redis;


import io.jboot.Jboot;
import io.jboot.app.JbootApplication;

public class RedisMqSender {

    public static void main(String[] args) throws InterruptedException {

        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "8659");

        //设置 mq 的相关信息
        JbootApplication.setBootArg("jboot.mq.type", "redis");
        JbootApplication.setBootArg("jboot.mq.redis.host", "127.0.0.1");
        JbootApplication.setBootArg("jboot.mq.redis.port", 6379);
        JbootApplication.setBootArg("jboot.mq.redis.database", 9);
        JbootApplication.setBootArg("jboot.mq.redis.password", "");

        JbootApplication.setBootArg("jboot.mq.other1.type", "redis");
        JbootApplication.setBootArg("jboot.mq.other1.typeName", "test");
        //JbootApplication.setBootArg("jboot.mq.other1.channel", "channel1,channel2,myChannel");
        JbootApplication.setBootArg("jboot.mq.redis.test.host", "127.0.0.1");
        JbootApplication.setBootArg("jboot.mq.redis.test.port", 6379);
        JbootApplication.setBootArg("jboot.mq.redis.test.database", 10);
        JbootApplication.setBootArg("jboot.mq.redis.test.password", "");

        //启动应用程序
        JbootApplication.run(args);

        int count = 10;
        for (int i = 0; i < count; i++) {
            Jboot.getMq().publish("message from RedisMqSender", "channel1");
            Jboot.getMq().publish("message from RedisMqSender", "channel2");
            Jboot.getMq().publish("message from RedisMqSender", "myChannel");

            Jboot.getMq().enqueue("message " + i, "channel1");

            Thread.sleep(1000);
            System.out.println("jboot mq publish success...");
        }

        for (int i = 0; i < count; i++) {
            Jboot.getMq("test").publish("message from RedisMqSender", "channel1");
            Jboot.getMq("test").publish("message from RedisMqSender", "channel2");
            Jboot.getMq("test").publish("message from RedisMqSender", "myChannel");

            Jboot.getMq("test").enqueue("message " + i, "channel1");

            Thread.sleep(1000);
            System.out.println("jboot mq publish success...");
        }
    }


}
