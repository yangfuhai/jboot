package io.jboot.test.controller;

import com.jfinal.kit.PathKit;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.GetRequest;
import io.jboot.web.controller.annotation.PostRequest;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/")
public class IndexController extends JbootController {

    public void index() {
        render("/index.html");
    }

    public void classPath() {
        renderText(PathKit.getRootClassPath());
    }

    public void error500(){

    }

    public void csv(){
        String text = "1,0\n" +
                "2,15000\n" +
                "3,20000\n" +
                "4,30000";

        renderText(text);
    }

    public String ping(){
        return "ping:" + getPara("ping");
    }

    @PostRequest
    public void post(){
        renderText("post ok");
    }


    @GetRequest
    public void get(){
        renderText("get ok");
    }

    @GetRequest
    @PostRequest
    public void getpost(){
        renderText("get or post ok");
    }



}
