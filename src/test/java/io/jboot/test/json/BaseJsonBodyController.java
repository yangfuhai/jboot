package io.jboot.test.json;

import io.jboot.web.controller.JbootController;
import io.jboot.web.json.JsonBody;

import java.util.Map;


public class BaseJsonBodyController<K,T extends Map> extends JbootController {


    public void update(@JsonBody() T t) {
        renderJson("update--->" + t);
    }

//    public void update(@JsonBody() K t) {
//        renderJson("update--->" + t.getClass().toString());
//    }
//
//    public void test33(Map map){}

}
