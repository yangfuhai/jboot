package io.jboot.test.aop;

import com.jfinal.aop.Before;
import io.jboot.aop.annotation.Bean;

@Bean
public class LayzService1Impl implements LayzService1{

    public LayzService1Impl() {
        System.out.println("LayzService1Impl init...");
    }

    @Before(Name1Interceptor.class)
    public void doSth(){
        System.out.println("LayzService1Impl doSth...");
    }
}
