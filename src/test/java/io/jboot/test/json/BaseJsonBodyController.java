package io.jboot.test.json;

import io.jboot.web.controller.JbootController;
import io.jboot.web.json.JsonBody;


public class BaseJsonBodyController<K,T> extends JbootController {


    public void update(@JsonBody() T t) {
        renderJson("update--->" + t);
    }

//    public void update(@JsonBody() K t) {
//        renderJson("update--->" + t.getClass().toString());
//    }
//
//    public void test33(Map map){}

}
