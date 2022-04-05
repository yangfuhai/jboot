package io.jboot.test.http;

import com.jfinal.kit.JsonKit;
import io.jboot.components.http.HttpProxyInfo;
import io.jboot.components.http.JbootHttpManager;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.StrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class HttpUtilTester {

    public static void main(String[] args) {

        JbootHttpRequest request = JbootHttpRequest.create("https://www.baidu.com", null, JbootHttpRequest.METHOD_GET);
        request.setHttpProxyInfo(new HttpProxyInfo("127.0.0.1",8080));

        JbootHttpResponse response = JbootHttpManager.me().getJbootHttp().handle(request);

//        System.out.println(response.getHeaders().get("Location").get(0));
        System.out.println(response);


        String queryString = "aaa=ccc&a=123&b=&c=aa&d&";
        System.out.println(JsonKit.toJson(StrUtil.queryStringToMap(queryString)));

        Map<String,Object> map = new HashMap<>();
        map.put("aa",123);
        map.put(null,"ddd");
        map.put("cc",null);
        map.put("你好","xx");
        System.out.println(StrUtil.mapToQueryString(map));


        Map<String, Object> paras = new HashMap<>();
        paras.put("key","value");

        Map<String, String> headers = new HashMap<>();
        headers.put("key","value");

        String postData = "abc";


        String s = HttpUtil.httpPost("https://www.baidu.com", paras, headers, postData);
        System.out.println(s);


    }



}