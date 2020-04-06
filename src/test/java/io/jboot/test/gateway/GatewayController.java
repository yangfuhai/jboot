package io.jboot.test.gateway;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/4/5
 */
@RequestMapping("/gateway")
public class GatewayController extends JbootController {

    public void index(){

    }


    public void render(){
        Map map = new HashMap();
        map.putAll(getParas());
        renderJson(map);
    }
}
