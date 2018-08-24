package rabbitmq;

import io.jboot.Jboot;
import io.jboot.core.mq.JbootmqMessageListener;

public class RabbitmqTest {



//    @Test
    public void testRabbitmqSend() {

        Jboot.setBootArg("jboot.mq.type", "rabbitmq");
        Jboot.setBootArg("jboot.mq.channel", "myChannel,myChannel1");

        Jboot.setBootArg("jboot.mq.rabbitmq.host", "127.0.0.1");
        Jboot.setBootArg("jboot.mq.rabbitmq.username", "guest");
        Jboot.setBootArg("jboot.mq.rabbitmq.password", "guest");


        doTest();

    }

    private void doTest() {
        Jboot.me().getMq().addMessageListener(new JbootmqMessageListener() {
            @Override
            public void onMessage(String channel, Object message) {
                System.out.println("listener:" + message + "     channel:" + channel);
            }
        });

        Jboot.me().getMq().publish("hello" , "myChannel1");

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
