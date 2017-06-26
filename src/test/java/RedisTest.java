import io.jboot.Jboot;
import io.jboot.component.redis.JbootRedis;
import io.jboot.core.mq.Jbootmq;
import io.jboot.core.mq.JbootmqMessageListener;
import org.junit.Test;


public class RedisTest {

    @Test
    public void testRedis() {

        JbootRedis redis = Jboot.getRedis();
        redis.set("mykey","myvalue");


        // 发布消息？
        redis.publish("myChannel","myData...");


        Jbootmq mq = Jboot.getMq();

        mq.addMessageListener(new JbootmqMessageListener() {
            @Override
            public void onMessage(String channel, Object message) {
                System.out.println("get message :  " + message);
            }
        });

        mq.publish("mesage data...","mqChannel");



    }


}
