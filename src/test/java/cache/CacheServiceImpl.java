package cache;

import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.Cacheable;

import java.util.Map;

@Bean
public class CacheServiceImpl implements CacheService {

    @Override
    @Cacheable(name = "test")
    public String cacheKeyTest(String key, Map<String, String> map) {
        return key + map.get("abc");
    }
}
