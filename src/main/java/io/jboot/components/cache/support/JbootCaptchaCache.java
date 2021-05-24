package io.jboot.components.cache.support;

import com.jfinal.captcha.Captcha;
import com.jfinal.captcha.ICaptchaCache;
import io.jboot.Jboot;

public class JbootCaptchaCache implements ICaptchaCache {

    public static final String CACHE_NAME = "__jboot_captcha";

    @Override
    public void put(Captcha captcha) {
        Jboot.getCache().put(CACHE_NAME, captcha.getKey(), captcha, (int) ((captcha.getExpireAt() - System.currentTimeMillis()) / 1000));
    }

    @Override
    public Captcha get(String key) {
        return Jboot.getCache().get(CACHE_NAME, key);
    }

    @Override
    public void remove(String key) {
        Jboot.getCache().remove(CACHE_NAME, key);
    }

    @Override
    public void removeAll() {
        Jboot.getCache().removeAll(CACHE_NAME);
    }

}
