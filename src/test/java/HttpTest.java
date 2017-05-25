import io.jboot.Jboot;
import io.jboot.http.JbootHttpRequest;
import io.jboot.http.JbootHttpResponse;
import org.junit.Test;

/**
 * Created by michael on 2017/5/12.
 */
public class HttpTest {

    @Test
    public void testHttp() {
        JbootHttpResponse response = Jboot.getHttp().handle(JbootHttpRequest.create("https://www.baidu.com"));

        System.out.println(response.getContentType());
        System.out.println(response.getHeaders());
//        System.out.println(response.getContent());
    }
}
