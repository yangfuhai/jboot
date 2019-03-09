package io.jboot.test.rpc.dubbozookeeper;

import io.jboot.app.JbootApplication;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.test.rpc.commons.BlogService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dubbozk")
public class DubboClientZookeeperDemo extends JbootController {

    public static void main(String[] args) {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "8888");

        //RPC配置
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
        JbootApplication.setBootArg("jboot.rpc.registryType", "zookeeper");//注册中心的类型：zookeeper
        JbootApplication.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:2181");//注册中心，即zookeeper的地址

        JbootApplication.run(args);
    }


    @RPCInject
    private BlogService blogService;

    public void index() {

        System.out.println("DubboClientZookeeperDemo.index()");

        System.out.println(blogService);
        System.out.println(blogService.findById());
        renderText("ok");
    }
}
