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
        Jboot.setBootArg("jboot.mq.redis.channel", "myChannel,myChannel1,myChannel2");

        doTest();

    }


    @Test
    public void testRabbitmqSend() {

        Jboot.setBootArg("jboot.mq.type", "rabbitmq");
        Jboot.setBootArg("jboot.mq.rabbitmq.host", "127.0.0.1");
        Jboot.setBootArg("jboot.mq.rabbitmq.username", "guest");
        Jboot.setBootArg("jboot.mq.rabbitmq.password", "guest");
        Jboot.setBootArg("jboot.mq.rabbitmq.channel", "myChannel");

        doTest();

    }

    private void doTest() {
        Jboot.getMq().addMessageListener(new JbootmqMessageListener() {
            @Override
            public void onMessage(String channel, Object message) {
                System.out.println("message:" + message + "     channel:" + channel);
            }
        });

        for (int i = 0; i < 10; i++) {
            Jboot.getMq().publish("hello" + i, "myChannel1");
        }


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
