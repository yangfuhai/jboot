package io.jboot.test.aop.inject;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;

@Bean
public class UserServiceImpl implements UserService {

    @Inject
    private OtherService otherService;

    @Override
    public String getName(){
        return "UserServiceImpl";
    }
}
