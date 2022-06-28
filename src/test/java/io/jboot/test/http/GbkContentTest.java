package io.jboot.test.http;

import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;
import io.jboot.utils.HttpUtil;

public class GbkContentTest {

    public static void main(String[] args) {
        JbootHttpRequest request = new JbootHttpRequest("http://www.**.com.cn/20.asp");
        request.setMethod(JbootHttpRequest.METHOD_GET);
        request.setCharset("GBK");

        JbootHttpResponse response = HttpUtil.handle(request);
        System.out.println(response.getContent());
    }
}
