import io.jboot.Jboot;
import io.jboot.component.redis.JbootRedis;
import service.User;


public class RedisTest {

    public static void main(String[] args) {

        User user = new User();
        user.setId(22);
        user.setName("张三历史");

        Jboot.setBootArg("jboot.redis.host", "127.0.0.1");
        JbootRedis redis = Jboot.me().getRedis();

        redis.incr("incr_key");
        Object o = redis.getWithoutSerialize("incr_key");
        System.out.println(o);


        redis.set("mykey", "a");
        System.out.println(redis.get("mykey").toString());


        redis.del("list");
        redis.lpush("list", 1, 2, 3, 4, 5, 6);
        System.out.println(redis.lrange("list", 0, -1).size());
        System.out.println(redis.blpop(10000, "list"));
        System.out.println(redis.blpop(10000, "list"));


        redis.set("user", user);
        User redisUser = redis.get("user");
        System.out.println(redisUser.getId());
        System.out.println(redisUser.getName());


    }


}
