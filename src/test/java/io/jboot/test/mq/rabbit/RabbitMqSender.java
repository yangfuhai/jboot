package io.jboot.test.mq.rabbit;

import io.jboot.Jboot;
import io.jboot.app.JbootApplication;

/**
 * 开始之前 先通过通过 docker 把 rabbitmq 运行起来
 * docker run -d  -p 15672:15672 -p 5672:5672 rabbitmq:management
 */
public class RabbitMqSender {


    public static void main(String[] args) throws InterruptedException {

        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "8000");

        //设置 mq 的相关信息
        JbootApplication.setBootArg("jboot.mq.type", "rabbitmq");

        //以下可以不用配置，是默认信息
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.username", "guest");
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.password", "guest");
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.host", "127.0.0.1");
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.port", "5672");
//        JbootApplication.setBootArg("jboot.mq.rabbitmq.virtualHost", "");

        //启动应用程序
        JbootApplication.run(args);

        long index = 0;

        while (true && index < 3) {

            Jboot.getMq().publish("message from RabbitMqSender for channel1", "channel1");
            Jboot.getMq().publish("message from RabbitMqSender for channel2", "channel2");
            Jboot.getMq().publish("message from RabbitMqSender for myChannel", "myChannel");

            Jboot.getMq().enqueue("message from RabbitMqSender by enqueue " + (index++), "channel1");

            Thread.sleep(2000);
            System.out.println("jboot mq publish success... index: " + index);
        }

    }

}
