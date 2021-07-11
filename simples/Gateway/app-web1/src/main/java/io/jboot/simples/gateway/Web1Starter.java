package io.jboot.simples.gateway;

import com.jfinal.core.Controller;
import io.jboot.app.JbootApplication;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/web")
public class Web1Starter extends Controller {

    public static void main(String[] args) {
        JbootApplication.run(args);
    }

    public void index(){
        renderText("render text form web1");
    }
}
