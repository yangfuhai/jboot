package io.jboot.test.json;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;

import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/jsonbody")
public class JsonBodyController extends JbootController {

    /**
     * send json :
     *
     * {
     *     "aaa":{
     *         "bbb":{
     *             "id":"abc",
     *             "age":17,
     *             "amount":123
     *         }
     *     }
     * }
     *
     *
     * @param bean
     */
    public void test(@JsonBody("aaa.bbb") ConcurrentHashMap bean){
        System.out.println(bean);
        renderText("ok");
    }
}
