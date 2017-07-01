import com.alibaba.fastjson.JSON;
import org.junit.Test;

public class FastJsonTest {

    @Test
    public void testString() {
        System.out.println(JSON.toJSON("aa"));
    }


    @Test
    public void testString1() {
        System.out.println(JSON.parseObject("[\"aaa\"]", String.class));
    }
}
