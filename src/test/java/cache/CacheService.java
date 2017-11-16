package cache;

import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.Cacheable;

import java.util.Map;

public interface CacheService {

    String cacheAble(String key, Map<String, String> map);

    String cacheAbleLive(String key, Map<String, String> map);

    String putCache(String key, Map<String, String> map);

    void cacheEvict(String key, Map<String, String> map);
}
