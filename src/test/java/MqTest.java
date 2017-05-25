import io.jboot.Jboot;
import io.jboot.mq.JbootmqMessageListener;
import org.junit.Test;

public class MqTest {

    @Test
    public void testRedismqSend() {
        /**
         * jboot.properties
         *
         * jboot.mq.type= redis
         * jboot.mq.redis.address= 127.0.0.1:6379
         */

        Jboot.setBootArg("jboot.mq.type","redis");
        Jboot.setBootArg("jboot.mq.redis.address","127.0.0.1:6379");

        Jboot.getMq().addMessageListener(new JbootmqMessageListener() {
            @Override
            public void onMessage(String channel, Object message) {
                System.out.println("message:" + message + "     channel:" + channel);
            }
        });

        for (int i = 0; i < 10; i++) {
            Jboot.getMq().publish("hello" + i, "myChannel");
        }


        sleep(2000);

    }


    private void sleep(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
