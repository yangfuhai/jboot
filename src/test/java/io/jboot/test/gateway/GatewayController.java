package io.jboot.test.gateway;

import com.jfinal.kit.Ret;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
        List<String> headers= new ArrayList<>();
        Enumeration<String> headerNames = getRequest().getHeaderNames();
        while (headerNames.hasMoreElements()){
            headers.add(headerNames.nextElement());
        }
        System.out.println("headers: "+headers);

        renderJson(Ret.ok());

    }
}
