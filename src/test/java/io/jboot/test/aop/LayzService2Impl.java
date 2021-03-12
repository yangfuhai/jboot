package io.jboot.test.aop;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;

@Bean
public class LayzService2Impl implements LayzService2 {

    @Inject
    private LayzService1 service1;

    public LayzService2Impl() {
        System.out.println("LayzService2Impl init...");
//        doSth();
    }


    public void doSth(){
        System.out.println("LayzService2Impl doSth...");
        service1.doSth();

    }
}
