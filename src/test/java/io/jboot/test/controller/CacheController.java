package io.jboot.test.controller;

import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@RequestMapping(value = "/cache")
public class CacheController extends JbootController {


    @Cacheable(name = "aaa", liveSeconds = 10)
    public void index() {
        System.out.println("index() invoked!!!!!!!!!");
        renderText("index");
    }


    @Cacheable(name = "aaa", liveSeconds = 10)
    public void json() {
        System.out.println("json() invoked!!!!!!!!!");
        Map<String, Object> data = new HashMap<>();
        data.put("age", 1);
        data.put("name", "张三");
        data.put("sex", 1);
        renderJson(data);
    }


    @Cacheable(name = "aaa", liveSeconds = 10)
    public void html() {
        System.out.println("html() invoked!!!!!!!!!");
        renderText("/index.html");
    }


    @Cacheable(name = "aaa", liveSeconds = 10)
    public void xml() {
        System.out.println("xml() invoked!!!!!!!!!");
        String xml = "<xml>\n" +
                "  <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[fromUser]]></FromUserName>\n" +
                "  <CreateTime>1348831860</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[this is a test]]></Content>\n" +
                "  <MsgId>1234567890123456</MsgId>\n" +
                "</xml>";

        renderText(xml,"xml");
    }


    @CacheEvict(name = "aaa")
    public void removeAll(){
        renderText("ok");
    }


}
