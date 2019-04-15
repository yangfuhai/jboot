package io.jboot.test.rpc.local;


import com.jfinal.core.Controller;
import io.jboot.app.JbootApplication;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.test.rpc.commons.BlogService;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("local")
public class LocalClient extends Controller {

    public static void main(String[] args) {


        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "8888");

        //RPC配置
        JbootApplication.setBootArg("jboot.rpc.type", "local");


        //开启 @RPCBean 自动暴露功能，默认情况下是自动暴露的，但是 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);

        JbootApplication.run(args);
    }


    @RPCInject
    private BlogService blogService;


    public void index() {
        System.out.println("blogService:" + blogService);
        renderText("blogId : " + blogService.findById());
    }

}
