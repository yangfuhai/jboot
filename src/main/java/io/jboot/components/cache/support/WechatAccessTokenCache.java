package io.jboot.components.cache.support;

import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCacheManager;

public class WechatAccessTokenCache implements IAccessTokenCache {

    static final String CACHE_NAME = "wechat_access_tokens";

    public WechatAccessTokenCache() {
        JbootCacheManager.me().getCache()
                .addThreadCacheNamePrefixIngore(CACHE_NAME);
    }


    @Override
    public String get(String key) {
        return Jboot.getCache().get(CACHE_NAME, key);
    }


    @Override
    public void set(String key, String value) {
        // 微信相关 token 的有效期之多 2 个小时
        // 如果设置为 7200，则有一定几率出现如下错误
        // {"errcode":40001,"errmsg":"invalid credential, access_token is invalid or not latest rid: **"}
        Jboot.getCache().put(CACHE_NAME, key, value,7000);
    }


    @Override
    public void remove(String key) {
        Jboot.getCache().remove(CACHE_NAME, key);
    }
}
