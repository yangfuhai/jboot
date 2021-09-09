package io.jboot.test.shiro;

import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;
import io.jboot.test.base.JbootTestBase;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.StrUtil;
import org.junit.Test;


import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class ShiroSupportTest extends JbootTestBase {


    @Test
    public void test() {
        // 不需要登录的页面
        assertEquals("index", httpGet("/shiro"));

        // 未登陆，重定向到 /shiro/loing
        assertEquals("login", httpGet("/shiro/usercenter"));

        // 使用 admin 账号登录
        String userCookie = loginWith("admin");

        // 使用 cookie 访问 usercenter
        assertEquals("usercenter", getWithCookie("/shiro/usercenter", userCookie));

        // 访问 admin 相关的页面
        assertEquals("admin", getWithCookie("/shiro/admin", userCookie));
        assertEquals("all", getWithCookie("/shiro/readAll", userCookie));

        // 访问 editor 页面受限
        assertTrue(getWithCookie("/shiro/editor", userCookie).contains("403"));
        assertTrue(getWithCookie("/shiro/readNews", userCookie).contains("403"));

        // 退出登陆
        assertEquals("logouted success", getWithCookie("/shiro/logout", userCookie));

        // 再次访问 usercenter，重定向到 login
        assertEquals("login", getWithCookie("/shiro/usercenter", userCookie));

        // 使用 editor 登陆
        userCookie = loginWith("editor");
        assertEquals("editor", getWithCookie("/shiro/editor", userCookie));
        assertEquals("news", getWithCookie("/shiro/readNews", userCookie));

        assertTrue(getWithCookie("/shiro/admin", userCookie).contains("403"));
        assertTrue(getWithCookie("/shiro/readAll", userCookie).contains("403"));
    }

    /**
     * 使用指定的cookie信息，访问url
     */
    private String getWithCookie(String url, String cookie) {
        JbootHttpRequest req = JbootHttpRequest.create(BASE_URL + url, null, JbootHttpRequest.METHOD_GET);
        req.addHeader("Cookie", cookie);
        JbootHttpResponse rsp = HttpUtil.handle(req);
        return rsp.getContent();
    }

    /**
     * 使用指定用户名登录，返回cookie信息
     */
    private String loginWith(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        JbootHttpRequest req = JbootHttpRequest.create(BASE_URL + "/shiro/doLogin", params, JbootHttpRequest.METHOD_GET);

        JbootHttpResponse rsp = HttpUtil.handle(req);
        assertEquals("logined success", rsp.getContent());
        return StrUtil.join(rsp.getHeaders().get("Set-Cookie"), ";");
    }
}
