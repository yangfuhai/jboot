import io.jboot.Jboot;
import io.jboot.core.cache.JbootCache;

import java.util.Arrays;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {

        Jboot.setBootArg("jboot.cache.type","ehredis");
        Jboot.setBootArg("jboot.redis.host","127.0.0.1");
        JbootCache cache = Jboot.me().getCache();

        cache.put("aaa", "aaa", "aaaa");
        cache.put("aaa", "bbb", "bbb");

//        cache.removeAll("aaa");

        List list = cache.getKeys("aaa");

        System.out.println(Arrays.toString(list.toArray()));
    }


}


