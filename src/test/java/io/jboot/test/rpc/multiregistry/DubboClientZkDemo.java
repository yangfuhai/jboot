package io.jboot.test.rpc.multiregistry;

import io.jboot.app.JbootApplication;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.test.rpc.commons.BlogService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dubbo/zk")
public class DubboClientZkDemo extends JbootController {

    public static void main(String[] args) {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "9003");

        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");


        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.zk.protocol", "zookeeper");
        JbootApplication.setBootArg("jboot.rpc.dubbo.registry.zk.address", "127.0.0.1:2181");


        JbootApplication.run(args);
    }


    @RPCInject
    private BlogService blogService;

    public void index() {

        System.out.println("DubboClientZkDemo.index()");

        System.out.println(blogService);
        renderText(blogService.findById());
    }
}
