package io.jboot.test.gateway;

import com.jfinal.kit.Ret;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/4/5
 */
@RequestMapping("/gateway")
public class GatewayController extends JbootController {

    public void index(){
        renderText("index");
    }



    public void render(){
//        Map map = new HashMap();
//        map.putAll(getParas());
//        renderJson(Ret.ok().put("data",map));

        renderJson(Ret.ok());

//        renderFile(new File("/Users/michael/Desktop/NewFile.txt"));

//        renderCaptcha();
    }
}
