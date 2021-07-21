package io.jboot.test.aop.inject;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.ConfigValue;
import io.jboot.components.limiter.LimitScope;
import io.jboot.components.limiter.annotation.EnableLimit;
import io.jboot.test.aop.staticconstruct.StaticConstructManager;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/aop")
public class AopController extends JbootController {

    @Inject
    private UserService userService;

    @Inject
    private StaticConstructManager manager;

    @ConfigValue("undertow.host")
    private String host;

    @ConfigValue("undertow.port")
    private int port;

    @ConfigValue(value = "undertow.xxx")
    private int xxx;


    public void index() {
        renderText("text from : " + userService.getName("aaa"));
    }

    @EnableLimit(rate = 1,fallback = "aaa")
    public void config() {
        renderText("host:" + host + "   port:" + port + "  xxx:" + xxx);
    }

    @EnableLimit(rate = 1, fallback = "aaa", scope = LimitScope.CLUSTER)
    public void bbb() {
        renderText("host:" + host + "   port:" + port + "  xxx:" + xxx);
    }

    public void aaa(){
        renderText("aaa");
    }
}
