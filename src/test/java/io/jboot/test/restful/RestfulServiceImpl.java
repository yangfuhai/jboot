package io.jboot.test.restful;

import com.jfinal.kit.StrKit;
import io.jboot.aop.annotation.Bean;

@Bean
public class RestfulServiceImpl implements RestfulService {
    @Override
    public String getRandomKey() {
        return StrKit.getRandomUUID();
    }
}
