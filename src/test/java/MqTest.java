import io.jboot.Jboot;
import io.jboot.core.mq.JbootmqMessageListener;

public class MqTest {

//    @Test
    public void testRedismqSend() {
        /**
         * jboot.properties
         *
         * jboot.mq.type= redis
         * jboot.mq.redis.host= 127.0.0.1
         */

        Jboot.setBootArg("jboot.mq.type", "redis");
        Jboot.setBootArg("jboot.mq.channel", "myChannel,myChannel1,myChannel2");

        Jboot.setBootArg("jboot.mq.redis.host", "127.0.0.1");

//
        doTest();

    }


//    @Test
//    public void testRabbitmqSend() {
//
//        Jboot.setBootArg("jboot.mq.type", "rabbitmq");
//        Jboot.setBootArg("jboot.mq.channel", "myChannel,myChannel1");
//
//        Jboot.setBootArg("jboot.mq.rabbitmq.host", "127.0.0.1");
//        Jboot.setBootArg("jboot.mq.rabbitmq.username", "guest");
//        Jboot.setBootArg("jboot.mq.rabbitmq.password", "guest");
//
//
//        doTest();
//
//    }

    private void doTest() {
        Jboot.me().getMq().addMessageListener(new JbootmqMessageListener() {
            @Override
            public void onMessage(String channel, Object message) {
                System.out.println("listener:" + message + "     channel:" + channel);
            }
        },"myChannel2");

        Jboot.me().getMq().publish("hello" , "myChannel2");
//        Jboot.me().getMq().enqueue("hello", "myChannel1");


        sleep(1000 * 5);
    }


    private void sleep(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
