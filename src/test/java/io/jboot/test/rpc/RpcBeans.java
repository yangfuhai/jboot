package io.jboot.test.rpc;

import io.jboot.aop.annotation.Bean;
import io.jboot.aop.annotation.Configuration;

@Configuration
public class RpcBeans {

    @Bean(name = "callback")
    public CallBack createCallback(){
        System.out.println("createCallback...");
        return new CallBack();
    }
}
