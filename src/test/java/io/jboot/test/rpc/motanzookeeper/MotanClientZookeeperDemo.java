package io.jboot.test.rpc.motanzookeeper;

import io.jboot.app.JbootApplication;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.test.rpc.commons.BlogService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/motanzk")
public class MotanClientZookeeperDemo extends JbootController {

    public static void main(String[] args) {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "9999");

        JbootApplication.setBootArg("jboot.rpc.type", "motan");

        // motan 的注册中心的协议
        JbootApplication.setBootArg("jboot.rpc.motan.registry.regProtocol", "zookeeper");
        // 注册中心地址，即zookeeper的地址
        JbootApplication.setBootArg("jboot.rpc.motan.registry.address", "127.0.0.1:2181");

        JbootApplication.run(args);
    }


    @RPCInject
    private BlogService blogService;

    public void index() {

        System.out.println("MotanClientZookeeperDemo.index()");

        System.out.println(blogService);

        renderText(blogService.findById());
    }
}
