package io.jboot.test.jwt;

import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/jwt")
public class JwtController  extends JwtBaseController{

    public void index(){
        renderText("jwt ok");
    }
}
