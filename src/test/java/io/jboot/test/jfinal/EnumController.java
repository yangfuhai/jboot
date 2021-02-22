package io.jboot.test.jfinal;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/enum")
public class EnumController extends JbootController {

    public void index(){
        render("/enum.html");
    }

}
