package io.jboot.test.aop.inject;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;

@Bean
public class DServiceImpl implements DService {

    @Inject
    OtherService otherService;

    public DServiceImpl() {
        System.out.println("new DServiceImpl...");
    }
}
