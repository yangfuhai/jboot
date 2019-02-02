package io.jboot.test.aop.inject;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;

@Bean
public class CServiceImpl implements CService {

    @Inject
    DService dService;
    BService bService;

    public CServiceImpl() {
        System.out.println("new CServiceImpl...");
    }
}
