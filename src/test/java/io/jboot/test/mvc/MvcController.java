package io.jboot.test.mvc;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/mvc")
public class MvcController extends JbootController {

    public void aaa(){
        System.out.println(">>>>>>>queryString:" + getRequest().getQueryString());
        renderText("aaa");
    }

    public void bbb(){
        System.out.println(">>>>>>>queryString:" + getRequest().getQueryString());
        renderText("bbb");
    }
}
