package io.jboot.components.cache.support;

import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import io.jboot.Jboot;

public class JbootAccessTokenCache implements IAccessTokenCache {

    static final String CACHE_NAME = "__jboot_wechat_access_tokens";

    @Override
    public String get(String key) {
        return Jboot.getCache().get(CACHE_NAME, key);
    }

    @Override
    public void set(String key, String value) {
        Jboot.getCache().put(CACHE_NAME, key, value);
    }

    @Override
    public void remove(String key) {
        Jboot.getCache().remove(CACHE_NAME, key);
    }
}
