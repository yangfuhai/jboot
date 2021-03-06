package io.jboot.test.xss;

import com.jfinal.core.Path;
import io.jboot.app.JbootApplication;
import io.jboot.web.controller.JbootController;

@Path("/xss")
public class XSSController extends JbootController {

    public void index() {
        System.out.println("------para:" + getPara("para"));
        System.out.println("------paras:" + getParas().get("para"));
        System.out.println("------getParaMap:" + getParaMap().get("para")[0]);
        System.out.println("------originalPara:" + getOriginalPara("para"));

        renderText("ok");
    }

    public static void main(String[] args) {
        JbootApplication.setBootArg("jboot.web.escapeParas", true);
        JbootApplication.run(args);
    }
}

