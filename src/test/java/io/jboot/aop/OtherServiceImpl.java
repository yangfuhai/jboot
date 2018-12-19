package io.jboot.aop;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;

@Bean
public class OtherServiceImpl implements OtherService {

    @Inject
    UserService userService;
}
