package io.jboot.test.fescar.dubbo;

import io.jboot.app.JbootApplication;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.test.fescar.business.BusinessServiceProvider;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dubbofescar")
public class DubboBusinessClientZookeeper extends JbootController {

    public static void main(String[] args) {

        //jboot端口号配置
        JbootApplication.setBootArg("undertow.port", "8888");
        JbootApplication.setBootArg("jboot.datasource.factory", "FescarDataSourceProxyFactory");
        //RPC配置
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
        JbootApplication.setBootArg("jboot.rpc.registryType", "zookeeper");//注册中心的类型：zookeeper
        JbootApplication.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:2181");//注册中心，即zookeeper的地址

        JbootApplication.run(args);
    }


    @RPCInject
    private BusinessServiceProvider accoutServiceProvider;

    public void index() {

        System.out.println("DubboBusinessClientZookeeper.index()");

        accoutServiceProvider.deposit(1);
        renderText("ok");
    }
}
