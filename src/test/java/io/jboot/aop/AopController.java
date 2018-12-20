package io.jboot.aop;

import com.jfinal.aop.Inject;
import io.jboot.app.config.annotation.ConfigInject;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;



@RequestMapping("/aop")
public class AopController extends JbootController {

    @Inject
    private UserService userService;

    @javax.inject.Inject
    private UserService javaxInjectUserService;

    @ConfigInject("undertow.host")
    private String host;

    @ConfigInject("undertow.port")
    private int port;


    public void index(){
        renderText("text from : " + userService.getName());
    }

    public void javax(){
        renderText("text from : " + javaxInjectUserService.getName());
    }

    public void config(){
        renderText("host:" + host +"   port:" + port);
    }
}
