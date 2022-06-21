package io.jboot.test.rpc.dubbo3.comsumer.controller;

import com.alibaba.fastjson.JSONArray;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.test.rpc.dubbo3.service.BlogService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dubbo3")
public class DubboClient extends JbootController {

    @RPCInject
    private BlogService blogService;


    public void index() {
        System.out.println(blogService);
        renderText("blogId : " + blogService.findById());
    }


    public void blogList() {
        System.out.println(blogService);
        renderText("blogList : " + JSONArray.toJSONString(blogService.findAll()));
    }
}
