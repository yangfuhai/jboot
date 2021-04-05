package io.jboot.test.json;

import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/jsonbody/base")
public class BaseJsonBodyTestController extends BaseJsonBodyController<MyBean,JsonMap>{


    public void update(Object object,String aa) {

    }

//    @Override
//    public void update(Map jsonMap) {
//        renderText("JsonMap ok");
//    }

    @Override
    public void update(JsonMap map) {
        super.update(map);
    }

    //    @Override
//    public void test33(HashMap map) {
//        super.test33(map);
//    }
}
