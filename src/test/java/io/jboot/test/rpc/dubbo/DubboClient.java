package io.jboot.test.rpc.dubbo;


import io.jboot.app.JbootApplication;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.test.rpc.commons.BlogService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dubbo")
public class DubboClient extends JbootController {

    public static void main(String[] args) {


        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "9999");

        //RPC配置
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");

        //设置直连模式，方便调试，默认为注册中心
        JbootApplication.setBootArg("jboot.rpc.urls", "io.jboot.test.rpc.commons.BlogService:127.0.0.1:28080");


        JbootApplication.run(args);
    }


    @RPCInject
    private BlogService blogService;

    public void index() {

        System.out.println("blogService:" + blogService);

        renderText("blogId : " + blogService.findById());
    }


}
