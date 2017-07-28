import io.jboot.Jboot;
import io.jboot.component.redis.JbootRedis;
import org.junit.Test;


public class RedisTest {

    @Test
    public void testRedis() {

        Jboot.setBootArg("jboot.redis.host", "127.0.0.1");
//        Jboot.setBootArg("jboot.redis.password", "123456");

        JbootRedis redis = Jboot.me().getRedis();
        redis.set("mykey", "myvalue");

        redis.lpush("list", 1,2,3,4,5);

        System.out.println(redis.get("mykey").toString());
        System.out.println(redis.lrange("list", 0, -1));

        System.out.println(redis.blpop(10000, "list"));


    }


}
