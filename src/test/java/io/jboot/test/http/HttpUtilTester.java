package io.jboot.test.http;

import io.jboot.components.http.JbootHttpManager;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class HttpUtilTester {

    public static void main(String[] args) {

        JbootHttpRequest request = JbootHttpRequest.create("https://www.baidu.com", null, JbootHttpRequest.METHOD_GET);
        JbootHttpResponse response = JbootHttpManager.me().getJbootHttp().handle(request);

//        System.out.println(response.getHeaders().get("Location").get(0));
        System.out.println(response);
    }


}