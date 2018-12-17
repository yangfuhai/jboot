package io.jboot.test;

import io.jboot.aop.annotation.Bean;

@Bean
public class UserServiceImpl implements UserService {
    @Override
    public String getName(){
        return "name from service";
    }
}
