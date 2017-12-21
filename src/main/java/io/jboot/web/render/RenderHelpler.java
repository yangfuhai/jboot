package io.jboot.web.render;

import io.jboot.Jboot;
import io.jboot.utils.StringUtils;
import io.jboot.web.cache.ActionCache;
import io.jboot.web.cache.ActionCacheContext;
import io.jboot.web.cache.ActionCacheEnable;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.render
 */
public class RenderHelpler {

    public static void actionCacheExec(String html, String contentType) {
        ActionCacheEnable actionCacheEnable = ActionCacheContext.get();
        if (actionCacheEnable != null) {
            String key = ActionCacheContext.getKey();
            String cacheName = actionCacheEnable.group();
            if (StringUtils.isBlank(cacheName)) {
                throw new IllegalArgumentException("ActionCacheEnable group must not be empty");
            }
            ActionCache actionCache = new ActionCache(contentType, html);
            Jboot.me().getCache().put(cacheName, key, actionCache, actionCacheEnable.liveSeconds());
        }
    }
}
