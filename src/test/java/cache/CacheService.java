package cache;

public interface CacheService {

    String cacheAble(String key);

    String cacheAbleLive(String key);

    String putCache(String key);

    void cacheEvict(String key);
}
