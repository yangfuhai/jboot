package io.jboot.test.base;


import com.jfinal.json.Json;
import io.jboot.app.JbootApplication;
import io.jboot.utils.HttpUtil;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试基类，从随机端口启动jboot
 */
@Ignore
@RunWith(JbootJunit4TestRunner.class)
public class JbootTestBase {

    public static int PORT = 0;
    public static String BASE_URL = String.format("http://localhost:%s", PORT);

    @BeforeClass
    public synchronized static void startApp() {
        if (PORT != 0) {
            return;
        }

        PORT = getAvailablePort();
        BASE_URL = String.format("http://localhost:%s", PORT);
        JbootApplication.setBootArg("jboot.app.mode", "test");
        JbootApplication.setBootArg("undertow.port", PORT);
        JbootApplication.setBootArg("undertow.ioThreads", 2);

        // 禁用undertow的dev模式，避免JbootTestRunner.createTest出现问题
        JbootApplication.setBootArg("undertow.devMode", false);

        JbootApplication.run(null);
    }


    private static Integer getAvailablePort() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
            return serverSocket.getLocalPort();
        } catch (IOException e) {
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static String httpGet(String url) {
        return HttpUtil.httpGet(BASE_URL + url);
    }

    public static String httpGet(String url, Map<String, Object> paras) {
        return HttpUtil.httpGet(BASE_URL + url, paras);
    }

    public static String httpGet(String url, Map<String, Object> paras, Map<String, String> headers) {
        return HttpUtil.httpGet(BASE_URL + url, paras, headers);
    }

    public static String httpPost(String url) {
        return HttpUtil.httpPost(BASE_URL + url, null, null, null);
    }

    public static String httpPost(String url, String postData) {
        return HttpUtil.httpPost(BASE_URL + url, null, null, postData);
    }

    public static String httpPost(String url, Map<String, Object> paras) {
        return HttpUtil.httpPost(BASE_URL + url, paras, null, null);
    }

    public static String httpPost(String url, Map<String, Object> paras, String postData) {
        return HttpUtil.httpPost(BASE_URL + url, paras, null, postData);
    }

    public static String httpPost(String url, Map<String, Object> paras, Map<String, String> headers, String postData) {
        return HttpUtil.httpPost(BASE_URL + url, paras, headers, postData);
    }

    public static String httpPostJson(String url, Object body) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json; charset=utf-8");

        return HttpUtil.httpPost(BASE_URL + url, null, header, Json.getJson().toJson(body));
    }

}
