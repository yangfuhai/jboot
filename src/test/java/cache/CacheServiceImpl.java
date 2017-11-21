package cache;

import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.CacheEvict;
import io.jboot.core.cache.annotation.CachePut;
import io.jboot.core.cache.annotation.Cacheable;

import java.util.Map;

@Bean
public class CacheServiceImpl implements CacheService {

    @Override
    @Cacheable(name = "#(keyName)")
    public String cacheAble(String keyName, Map<String, String> map) {
        return keyName + map.get("abc");
    }

    @Override
    @Cacheable(name = "#(key)", liveSeconds = 30)
    public String cacheAbleLive(String key, Map<String, String> map) {
        return key + map.get("abc");
    }

    @Override
    @CachePut(name = "#(key)", liveSeconds = 30)
    public String putCache(String key, Map<String, String> map) {
        return key + map.get("abc");
    }

    /**
     * 只能删除指定name，key的缓存
     * @param key
     * @param map
     */
    @Override
    @CacheEvict(name = "test", key = "cache.CacheServiceImpl#cacheAble#-key-b4f6bdd489cde9cf373b66200f086eed-")
    public void cacheEvict(String key, Map<String, String> map) {

    }
}
