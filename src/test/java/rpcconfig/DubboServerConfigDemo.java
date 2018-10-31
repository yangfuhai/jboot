package rpcconfig;

import io.jboot.Jboot;
import io.jboot.core.rpc.JbootrpcServiceConfig;

/**
 * Created by Administrator on 2018/11/1.
 */
public class DubboServerConfigDemo {

        public static void main(String[] args) throws InterruptedException {

                //jboot端口号配置
                Jboot.setBootArg("jboot.server.port", "8081");

                Jboot.setBootArg("jboot.rpc.type", "dubbo");
                Jboot.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
                Jboot.setBootArg("jboot.rpc.registryType", "zookeeper");//注册中心的类型：zookeeper
                Jboot.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:2181");//注册中心，即zookeeper的地址
                //自定义配置
                Jboot.setBootArg("jboot.rpc.proxy","javassist");
                Jboot.setBootArg("jboot.rpc.filter","cache");


                Jboot.run(args);

                //get rpc config
                JbootrpcServiceConfig serviceConfig = new JbootrpcServiceConfig();
                System.out.println(String.format("proxy is %s",serviceConfig.getProxy()));
                System.out.println(String.format("filter is %s",serviceConfig.getFilter()));




        }
}
