package actioncache;

import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.utils.StringUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package actioncache
 */
public class ActionCacheService {

    @Cacheable(name = "test1",key = "#(phone)")
    public String getByPhone(String phone){
        return StringUtils.uuid()+ phone;
    }
}
