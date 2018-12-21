package io.jboot.test.helloworld;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/helloworld")
public class HelloworldController extends JbootController {

    public void index(){
        renderText("hello world");
    }
}
