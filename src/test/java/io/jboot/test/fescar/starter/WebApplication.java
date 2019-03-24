package io.jboot.test.fescar.starter;

import com.jfinal.aop.Inject;
import io.jboot.app.JbootApplication;
import io.jboot.test.fescar.business.BusinessServiceProvider;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dubbofescar")
public class WebApplication extends JbootController {

    public static void main(String[] args) {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "8888");
        JbootApplication.setBootArg("jboot.datasource.factory", "fescar");
        //RPC配置
        JbootApplication.setBootArg("jboot.rpc.filter", "fescar");
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
        JbootApplication.setBootArg("jboot.rpc.registryType", "zookeeper");//注册中心的类型：zookeeper
        JbootApplication.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:2181");//注册中心，即zookeeper的地址

        JbootApplication.setBootArg("jboot.fescar.enable", true);
        JbootApplication.setBootArg("jboot.fescar.failureHandler", "com.alibaba.fescar.tm.api.DefaultFailureHandlerImpl");
        JbootApplication.setBootArg("jboot.fescar.applicationId", "Dubbo_Fescar_Business_Service");
        JbootApplication.setBootArg("jboot.fescar.txServiceGroup", "dubbo_fescar_tx_group");

        JbootApplication.run(args);
    }


    @Inject
    private BusinessServiceProvider businessServiceProvider;

    public void index() {

        System.out.println("WebApplication.index()");

        businessServiceProvider.deposit(1);
        renderText("ok");
    }
}
