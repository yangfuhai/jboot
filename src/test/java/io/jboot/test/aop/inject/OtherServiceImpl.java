package io.jboot.test.aop.inject;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;

@Bean
public class OtherServiceImpl implements OtherService {

    @Inject
    UserService userService;


    public OtherServiceImpl() {
        System.out.println("new OtherServiceImpl...");
    }
}
