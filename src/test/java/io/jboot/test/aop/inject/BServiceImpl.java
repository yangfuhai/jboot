package io.jboot.test.aop.inject;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;

@Bean
public class BServiceImpl implements BService {

    @Inject
    CService cService;

    public BServiceImpl() {
        System.out.println("new BServiceImpl...");
    }
}
