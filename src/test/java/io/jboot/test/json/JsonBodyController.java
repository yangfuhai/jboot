package io.jboot.test.json;

import com.jfinal.kit.JsonKit;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;

import java.util.List;
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
    public void bean(@JsonBody("aaa.bbb") MyBean bean) {
        System.out.println("bean--->" + JsonKit.toJson(bean));
        renderText("ok");
    }


    public void map(@JsonBody("aaa.bbb") Map map) {
        System.out.println("map--->" + JsonKit.toJson(map));
        renderText("ok");
    }

    /**
     * {
     *     "aaa":{
     *         "bbb":[{
     *             "id":"abc",
     *             "age":17,
     *             "amount":123
     *         },{
     *             "id":"abc",
     *             "age":17,
     *             "amount":123
     *         }]
     *     }
     * }
     * @param list
     */
    public void list(@JsonBody("aaa.bbb") List<MyBean> list) {
        System.out.println("list--->" + JsonKit.toJson(list));
        renderText("ok");
    }


    /**
     * {
     *     "aaa":{
     *         "bbb":[{
     *             "id":"abc",
     *             "age":17,
     *             "amount":123
     *         },{
     *             "id":"abc",
     *             "age":17,
     *             "amount":123
     *         }]
     *     }
     * }
     * @param beans
     */
    public void array(@JsonBody("aaa.bbb") MyBean[] beans) {
        System.out.println("array--->" + JsonKit.toJson(beans));
        renderText("ok");
    }


    /**
     * {
     *     "aaa":{
     *         "bbb":[1,2,3]
     *     }
     * }
     * @param list
     */
    public void list1(@JsonBody("aaa.bbb") List<Integer> list) {
        System.out.println("list1--->" + JsonKit.toJson(list));
        renderText("ok");
    }


    /**
     * {
     *     "aaa":{
     *         "bbb":[1,2,3]
     *     }
     * }
     * @param beans
     */
    public void array1(@JsonBody("aaa.bbb") int[] beans) {
        System.out.println("array1--->" + JsonKit.toJson(beans));
        renderText("ok");
    }




    /**
     * [1,2,3]
     * @param list
     */
    public void list2(@JsonBody() List<Integer> list) {
        System.out.println("list2--->" + JsonKit.toJson(list));
        renderText("ok");
    }


    /**
     * [1,2,3]
     * @param beans
     */
    public void array2(@JsonBody() int[] beans) {
        System.out.println("array2--->" + JsonKit.toJson(beans));
        renderText("ok");
    }
}
