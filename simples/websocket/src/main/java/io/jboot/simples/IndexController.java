package io.jboot.simples;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/")
public class IndexController extends JbootController {

    public void index() {
        render("/index.html");
    }

}