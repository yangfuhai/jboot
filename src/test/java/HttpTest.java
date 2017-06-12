import io.jboot.Jboot;
import io.jboot.db.model.JbootModel;
import io.jboot.http.JbootHttpRequest;
import io.jboot.http.JbootHttpResponse;
import io.jboot.service.JbootService;
import org.junit.Test;

import java.util.EventListener;

/**
 * Created by michael on 2017/5/12.
 */
public class HttpTest extends JbootService implements EventListener{

    @Test
    public void testHttp() {
        JbootHttpResponse response = Jboot.getHttp().handle(JbootHttpRequest.create("https://www.baidu.com"));

        System.out.println(response.getContentType());
        System.out.println(response.getHeaders());

    }



    @Override
    public JbootModel findById(Object id) {
        return null;
    }

    @Override
    public boolean deleteById(Object id) {
        return false;
    }
}
