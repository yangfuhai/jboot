import io.jboot.Jboot;
import io.jboot.core.http.JbootHttpRequest;
import io.jboot.core.http.JbootHttpResponse;
import org.junit.Test;


public class HttpTest {

    @Test
    public void testHttp() {
        JbootHttpResponse response = Jboot.getHttp().handle(JbootHttpRequest.create("https://www.baidu.com"));

        System.out.println(response.getContentType());
        System.out.println(response.getHeaders());

    }


}
