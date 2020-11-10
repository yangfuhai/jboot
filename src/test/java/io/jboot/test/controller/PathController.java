package io.jboot.test.controller;

import com.jfinal.core.Path;
import com.jfinal.kit.PathKit;
import io.jboot.web.controller.JbootController;

@Path("/path")
public class PathController extends JbootController {

    public void index() {
        render("/index.html");
    }

    public void classPath() {
        renderText(PathKit.getRootClassPath());
    }

    public void error500(){

    }

    public String ping(){
        return "ping:" + getPara("ping");
    }



}
