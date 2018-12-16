package io.jboot.test;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.inject.Inject;


@RequestMapping("/")
public class IndexController extends JbootController {

    @Inject
    private UserService userService;


    public void index(){

        renderText("index ..." + userService.getName());
    }
}
