package cache;

import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.CacheEvict;
import io.jboot.core.cache.annotation.CachePut;
import io.jboot.core.cache.annotation.Cacheable;

@Bean
public class CacheServiceImpl implements CacheService {

    @Override
    @Cacheable(name = "mycache", key = "#(key)",unless = "#(key =='zhangsan')")
    public String cacheAble(String key) {
        System.out.println("cacheAble invoked!!!!");
        return key + " from CacheServiceImpl.cacheAble";
    }

    @Override
    @Cacheable(name = "mycache", key = "#(key)", liveSeconds = 5)
    public String cacheAbleLive(String key) {
        System.out.println("cacheAbleLive invoked!!!!");
        return key + " from CacheServiceImpl.cacheAbleLive";
    }

    @Override
    @CachePut(name = "mycache", key = "#(key)", liveSeconds = 5)
    public String putCache(String key) {
        return key + " from CacheServiceImpl.putCache";
    }

    /**
     * 只能删除指定name，key的缓存
     *
     * @param key
     */
    @Override
    @CacheEvict(name = "mycache", key = "#(key)")
    public void cacheEvict(String key) {

    }
}
