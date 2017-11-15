package cache;

import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.Cacheable;

import java.util.Map;

public interface CacheService {

    String cacheKeyTest(String key, Map<String, String> map);
}
