import io.jboot.Jboot;
import io.jboot.component.redis.JbootRedis;
import org.junit.Test;


public class RedisTest {

    @Test
    public void testRedis() {

        Jboot.setBootArg("jboot.redis.host", "127.0.0.1");

        JbootRedis redis = Jboot.getRedis();
        redis.set("mykey","myvalue");


        System.out.println(redis.get("mykey").toString());



    }


}
