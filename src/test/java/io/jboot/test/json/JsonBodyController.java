package io.jboot.test.json;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;

import java.util.Map;

@RequestMapping("/jsonbody")
public class JsonBodyController extends JbootController {

    /**
     * send json :
     * <p>
     * {
     * "aaa":{
     * "bbb":{
     * "id":"abc",
     * "age":17,
     * "amount":123
     * }
     * }
     * }
     *
     * @param bean
     */
    public void test(@JsonBody("aaa.bbb") MyBean bean) {
        System.out.println(bean);
        renderText("ok");
    }


    public void map(@JsonBody("aaa.bbb") Map map) {
        System.out.println(map);
        renderText("ok");
    }
}
