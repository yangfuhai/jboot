package io.jboot.test.controller;

import com.jfinal.kit.PathKit;
import io.jboot.utils.RequestUtil;
import io.jboot.web.ResponseEntity;
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

    public void baseUrl(){
        renderText(RequestUtil.getBaseUrl());
    }


    public void currentUrl(){
        renderText(RequestUtil.getCurrentUrl());
    }

    public String r1(){
        return "text....";
    }

    public String r2(){
        return "error:404";
    }


    public String r3(){
        return "error  : 404";
    }

    public String r4(){
        return "error:500";
    }


    public String r5(){
        return "error  : 500";
    }


    public String r6(){
        return "index.html";
    }

    public ResponseEntity r7(){
        return ResponseEntity.ok().body("aaa");
    }



    public String r8(){
        return "redirect  : r1";
    }


    public String r9(){
        return "redirect:r2";
    }



    public String r10(){
        return "forward  : r3";
    }


    public String r11(){
        return "forward:r4";
    }

    public String r12(){
        return "forward:./r4";
    }
    public String r13(){
        return "forward: ";
    }

    public String r14(){
        return "forward: classPath";
    }
    public String r15(){
        return "redirect: classPath";
    }
}
