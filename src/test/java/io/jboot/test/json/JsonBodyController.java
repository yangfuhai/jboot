package io.jboot.test.json;

import com.jfinal.kit.JsonKit;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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

    public void bean1(@JsonBody("aaa.bbb") MyBean bean,@JsonBody("aaa.bbb") MyBean bean1,@JsonBody("aaa.bbb.age") int age) {
        System.out.println("bean--->" + JsonKit.toJson(bean));
        System.out.println("bean--->" + JsonKit.toJson(bean1));
        System.out.println("bean--->" + JsonKit.toJson(age));
        renderText("ok");
    }


    public void bean2(@JsonBody() MyBean bean) {
        System.out.println("bean--->" + JsonKit.toJson(bean));
        renderText("ok");
    }

    public void intValue(@JsonBody("aaa.bbb.age") int bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }

    public void intValue1(@JsonBody("aaa.bbb.age") Integer bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }
    public void intValue2(@JsonBody("aaa.bbb.age") String bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }

    public void bigint1(@JsonBody("aaa.bbb.age") BigInteger bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }

    public void bigdec1(@JsonBody("aaa.bbb.age") BigDecimal bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }

    public void float1(@JsonBody("aaa.bbb.age") float bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }

    public void long1(@JsonBody("aaa.bbb.age") long bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }

    public void long2(@JsonBody("aaa.bbb.age") Long bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }

    public void strValue(@JsonBody("aaa.bbb.id") String bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }
    public void date(@JsonBody("aaa.bbb.date") Date bean) {
        System.out.println("bean--->" + bean);
        renderText("ok");
    }



    public void map(@JsonBody("aaa.bbb") HashMap map) {
        System.out.println("map--->" + JsonKit.toJson(map));
        renderText("ok");
    }


    public void mapString(@JsonBody("aaa.bbb") HashMap<String, String> map) {
        System.out.println("map--->" + JsonKit.toJson(map));
        renderText("ok");
    }

    /**
     * {
     * "aaa":{
     * "bbb":[{
     * "id":"abc",
     * "age":17,
     * "amount":123
     * },{
     * "id":"abc",
     * "age":17,
     * "amount":123
     * }]
     * }
     * }
     *
     * @param list
     */
    public void list(@JsonBody("aaa.bbb") List<MyBean> list) {
        System.out.println("list--->" + JsonKit.toJson(list));
        renderText("ok");
    }


    /**
     * {
     * "aaa":{
     * "bbb":[{
     * "id":"abc",
     * "age":17,
     * "amount":123
     * },{
     * "id":"abc",
     * "age":17,
     * "amount":123
     * }]
     * }
     * }
     *
     * @param beans
     */
    public void array(@JsonBody("aaa.bbb") MyBean[] beans) {
        System.out.println("array--->" + JsonKit.toJson(beans));
        renderText("ok");
    }


    /**
     * {
     * "aaa":{
     * "bbb":[1,2,3]
     * }
     * }
     *
     * @param list
     */
    public void list1(@JsonBody("aaa.bbb") List<Integer> list) {
        System.out.println("list1--->" + JsonKit.toJson(list));
        renderText("ok");
    }


    /**
     * {
     * "aaa":{
     * "bbb":[1,2,3]
     * }
     * }
     *
     * @param beans
     */
    public void array1(@JsonBody("aaa.bbb") int[] beans) {
        System.out.println("array1--->" + JsonKit.toJson(beans));
        renderText("ok");
    }


    /**
     * [1,2,3]
     *
     * @param list
     */
    public void list2(@JsonBody() List<Integer> list) {
        System.out.println("list2--->" + JsonKit.toJson(list));
        renderText("ok");
    }


    /**
     * [1,2,3]
     *
     * @param beans
     */
    public void array2(@JsonBody() int[] beans) {
        System.out.println("array2--->" + JsonKit.toJson(beans));
        renderText("ok");
    }


    /**
     * [1,2,3]
     *
     * @param array
     */
    public void array3(@JsonBody() List<String> array, int a) {
        String s = array.get(0);
        System.out.println("array3--->" + JsonKit.toJson(array));
        renderText("ok");
    }

    /**
     * [1,2,3]
     *
     * @param array
     */
    public void array4(@JsonBody() List array, int a) {
        System.out.println("array4--->" + JsonKit.toJson(array));
        renderText("ok");
    }


    /**
     * [1,2,3]
     *
     * @param array
     */
    public void set1(@JsonBody() Set<String> array, int a) {
//        String s = array.g(0);
        System.out.println("set1--->" + JsonKit.toJson(array));
        renderText("ok");
    }

    /**
     * [1,2,3]
     *
     * @param array
     */
    public void set2(@JsonBody() Set array, int a) {
        System.out.println("set2--->" + JsonKit.toJson(array));
        renderText("ok");
    }

    /**
     * [1,2,3]
     *
     * @param array
     */
    public void set3(@JsonBody() HashSet<String> array, int a) {
//        String s = array.g(0);
        System.out.println("set1--->" + JsonKit.toJson(array));
        renderText("ok");
    }

    /**
     * [1,2,3]
     *
     * @param array
     */
    public void set4(@JsonBody() HashSet array, int a) {
        System.out.println("set2--->" + JsonKit.toJson(array));
        renderText("ok");
    }


    /**
     * [1,2,3]
     *
     * @param array
     */
    public void queue1(@JsonBody() Queue<String> array, int a) {
//        String s = array.g(0);
        System.out.println("queue1--->" + JsonKit.toJson(array));
        renderText("ok");
    }

    /**
     * [1,2,3]
     *
     * @param array
     */
    public void queue2(@JsonBody() Queue array, int a) {
        System.out.println("queue2--->" + JsonKit.toJson(array));
        renderText("ok");
    }
    /**
     * [1,2,3]
     *
     * @param array
     */
    public void vector1(@JsonBody() Vector<String> array, int a) {
//        String s = array.g(0);
        System.out.println("vector1--->" + JsonKit.toJson(array));
        renderText("ok");
    }

    /**
     * [1,2,3]
     *
     * @param array
     */
    public void vector2(@JsonBody() Vector array, int a) {
        System.out.println("vector2--->" + JsonKit.toJson(array));
        renderText("ok");
    }


    /**
     * [1,2,3]
     *
     * @param array
     */
    public void stack1(@JsonBody() Stack<String> array, int a) {
//        String s = array.g(0);
        System.out.println("stack1--->" + JsonKit.toJson(array));
        renderText("ok");
    }

    /**
     * [1,2,3]
     *
     * @param array
     */
    public void stack2(@JsonBody() Stack array, int a) {
        System.out.println("stack2--->" + JsonKit.toJson(array));
        renderText("ok");
    }


    /**
     * [1,2,3]
     *
     * @param array
     */
    public void deque1(@JsonBody() Deque<String> array, int a) {
//        String s = array.g(0);
        System.out.println("deque1--->" + JsonKit.toJson(array));
        renderText("ok");
    }

    /**
     * [1,2,3]
     *
     * @param array
     */
    public void deque2(@JsonBody() Deque array, int a) {
        System.out.println("deque2--->" + JsonKit.toJson(array));
        renderText("ok");
    }


}
