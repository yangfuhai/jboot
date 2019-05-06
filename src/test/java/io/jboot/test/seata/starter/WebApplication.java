package io.jboot.test.seata.starter;

import com.jfinal.aop.Inject;
import io.jboot.app.JbootApplication;
import io.jboot.test.seata.business.BusinessServiceProvider;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dubbo/seata")
public class WebApplication extends JbootController {

    public static void main(String[] args) {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "8888");

        //RPC配置
        JbootApplication.setBootArg("jboot.rpc.filter", "seata");
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
        JbootApplication.setBootArg("jboot.rpc.registryType", "zookeeper");//注册中心的类型：zookeeper
        JbootApplication.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:2181");//注册中心，即zookeeper的地址

        JbootApplication.setBootArg("jboot.seata.enable", true);
        JbootApplication.setBootArg("jboot.seata.failureHandler", "com.alibaba.io.seata.tm.api.DefaultFailureHandlerImpl");
        JbootApplication.setBootArg("jboot.seata.applicationId", "Dubbo_Seata_Business_Service");
        JbootApplication.setBootArg("jboot.seata.txServiceGroup", "dubbo_seata_tx_group");

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
