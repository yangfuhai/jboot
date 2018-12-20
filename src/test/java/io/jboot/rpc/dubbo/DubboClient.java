package io.jboot.rpc.dubbo;


import io.jboot.app.JbootApplication;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.rpc.commons.BlogService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dubbo")
public class DubboClient extends JbootController{

    public static void main(String[] args)  {


        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "8888");

        //RPC配置
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.closeAutoExport", "true");
        JbootApplication.setBootArg("jboot.rpc.callMode", "redirect");//直连模式，默认为注册中心
        JbootApplication.setBootArg("jboot.rpc.directUrl", "127.0.0.1:8000");//直连模式的url地址

        JbootApplication.run(args);
    }


    @RPCInject
    private  BlogService blogService;

    public void index() {
        System.out.println(blogService);
        renderText("blogId : " + blogService.findById());
    }


}
