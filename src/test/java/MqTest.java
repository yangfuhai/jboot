import io.jboot.Jboot;
import io.jboot.core.mq.JbootmqMessageListener;
import org.junit.Test;

public class MqTest {

    @Test
    public void testRedismqSend() {
        /**
         * jboot.properties
         *
         * jboot.mq.type= redis
         * jboot.mq.redis.host= 127.0.0.1
         */

        Jboot.setBootArg("jboot.mq.type", "redis");
        Jboot.setBootArg("jboot.mq.redis.host", "127.0.0.1");
//        Jboot.setBootArg("jboot.mq.redis.password", "123456");
        Jboot.setBootArg("jboot.mq.redis.channel", "myChannel,myChannel1,myChannel2");

        doTest();

    }


    @Test
    public void testRabbitmqSend() {

        Jboot.setBootArg("jboot.mq.type", "rabbitmq");
        Jboot.setBootArg("jboot.mq.rabbitmq.host", "127.0.0.1");
        Jboot.setBootArg("jboot.mq.rabbitmq.username", "guest");
        Jboot.setBootArg("jboot.mq.rabbitmq.password", "guest");
        Jboot.setBootArg("jboot.mq.rabbitmq.channel", "myChannel,myChannel1");

        doTest();

    }

    private void doTest() {
        Jboot.me().getMq().addMessageListener(new JbootmqMessageListener() {
            @Override
            public void onMessage(String channel, Object message) {
                System.out.println("listener:" + message + "     channel:" + channel);
            }
        });

//        Jboot.me().getMq().publish("hello" , "myChannel1");
        Jboot.me().getMq().enqueue("hello", "myChannel1");


        sleep(1000 * 3);
    }


    private void sleep(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
