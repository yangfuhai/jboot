import io.jboot.Jboot;
import org.junit.Test;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class CacheTest {


    @Test
    public void testSetAndGet() {

        Jboot.setBootArg("jboot.cache.type", "ehcache");

        Jboot.me().getCache().put("test", "mykey", "abc");

        System.out.println((String) Jboot.me().getCache().get("test", "mykey"));
    }



    @Test
    public void testSetInTimeAndGet() {
        Jboot.setBootArg("jboot.cache.type", "ehredis");
        Jboot.setBootArg("jboot.redis.host","127.0.0.1");
        Jboot.me().getCache().put("test", "mykey", "abc", 2);

        for (int i = 0; i < 4; i++) {
            System.out.println(i + " : " +  Jboot.me().getCache().get("test", "mykey"));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
