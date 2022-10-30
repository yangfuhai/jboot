package io.jboot.components.cache.support;

import com.jfinal.captcha.Captcha;
import com.jfinal.captcha.ICaptchaCache;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.utils.StrUtil;

public class JbootCaptchaCache implements ICaptchaCache {

    public static final String CACHE_NAME = "jboot_captchas";

    public JbootCaptchaCache() {
        JbootCacheManager.me().getCache().addThreadCacheNamePrefixIngore(CACHE_NAME);
    }

    @Override
    public void put(Captcha captcha) {
        Jboot.getCache().put(CACHE_NAME, captcha.getKey(), captcha, (int) ((captcha.getExpireAt() - System.currentTimeMillis()) / 1000));
    }

    @Override
    public Captcha get(String key) {
        return StrUtil.isBlank(key) ? null : Jboot.getCache().get(CACHE_NAME, key);
    }

    @Override
    public void remove(String key) {
        if (StrUtil.isNotBlank(key)) {
            Jboot.getCache().remove(CACHE_NAME, key);
        }
    }

    @Override
    public void removeAll() {
        Jboot.getCache().removeAll(CACHE_NAME);
    }

}
