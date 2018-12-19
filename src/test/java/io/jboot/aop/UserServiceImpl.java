package io.jboot.aop;

import io.jboot.aop.annotation.Bean;

@Bean
public class UserServiceImpl implements UserService {
    @Override
    public String getName(){
        return "UserServiceImpl";
    }
}
