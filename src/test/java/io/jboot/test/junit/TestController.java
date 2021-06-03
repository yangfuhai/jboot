package io.jboot.test.junit;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/test")
public class TestController extends JbootController {

    public void aaa(){
        System.out.println(">>>>>>>queryString:" + getRequest().getQueryString());
        renderText("aaa");
    }

    public void bbb(){
        System.out.println(">>>>>>>queryString:" + getRequest().getQueryString());
        renderText("bbb");
    }
}
