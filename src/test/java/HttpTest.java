import io.jboot.Jboot;
import io.jboot.db.model.JbootModel;
import io.jboot.core.http.JbootHttpRequest;
import io.jboot.core.http.JbootHttpResponse;
import io.jboot.db.dao.JbootDaoBase;
import org.junit.Test;

import java.util.EventListener;


public class HttpTest extends JbootDaoBase implements EventListener{

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
