import io.jboot.Jboot;
import io.jboot.core.http.JbootHttpRequest;
import io.jboot.core.http.JbootHttpResponse;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class HttpTest {

    @Test
    public void testHttp() {
        JbootHttpResponse response = Jboot.me().getHttp().handle(JbootHttpRequest.create("https://www.baidu.com"));

        System.out.println(response.getContentType());
        System.out.println(response.getHeaders());

    }

    @Test
    public void testHttpGet() {
        String html = Jboot.httpGet("https://www.baidu.com");
        System.out.println(html);
    }

    @Test
    public void testHttpPost() {
        Map<String, Object> params = new HashMap<>();
        params.put("key1", "value1");
        params.put("key2", "value2");


        String html = Jboot.httpPost("http://www.oschina.net/", params);
        System.out.println(html);
    }

    @Test
    public void testHttpDownload() {

        String url = "http://www.xxx.com/abc.zip";

        File downloadToFile = new File("/xxx/abc.zip");


        JbootHttpRequest request = JbootHttpRequest.create(url, null, JbootHttpRequest.METHOD_GET);
        request.setDownloadFile(downloadToFile);


        JbootHttpResponse response = Jboot.me().getHttp().handle(request);

        if (response.isError()){
            downloadToFile.delete();
        }

        System.out.println(downloadToFile.length());
    }


}
