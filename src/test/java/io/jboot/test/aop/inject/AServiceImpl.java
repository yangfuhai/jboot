package io.jboot.test.aop.inject;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;

@Bean
public class AServiceImpl implements AService {

    @Inject
    BService bService;

    public AServiceImpl() {
        System.out.println("new AServiceImpl...");
    }
}
