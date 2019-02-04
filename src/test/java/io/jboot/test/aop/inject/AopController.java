package io.jboot.test.aop.inject;

import com.jfinal.aop.Inject;
import io.jboot.app.config.annotation.InjectConfigValue;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/aop")
public class AopController extends JbootController {

    @Inject
    private UserService userService;

    @InjectConfigValue("undertow.host")
    private String host;

    @InjectConfigValue("undertow.port")
    private int port;

    @InjectConfigValue("undertow.xxx:123")
    private int xxx;


    public void index() {
        renderText("text from : " + userService.getName());
    }


    public void config() {
        renderText("host:" + host + "   port:" + port + "  xxx:" + xxx);
    }
}
