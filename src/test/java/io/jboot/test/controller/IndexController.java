package io.jboot.test.controller;

import com.jfinal.kit.PathKit;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/")
public class IndexController extends JbootController {

    public void index() {
        render("/index.html");
    }

    public void classPath() {
        renderText(PathKit.getRootClassPath());
    }
}
