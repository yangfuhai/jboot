package io.jboot.web.render;

import io.jboot.Jboot;
import io.jboot.web.cache.ActionCacheContent;
import io.jboot.web.cache.ActionCacheContext;
import io.jboot.web.cache.ActionCacheInfo;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.render
 */
public class RenderHelpler {

    public static void actionCacheExec(String html, String contentType) {
        ActionCacheInfo info = ActionCacheContext.get();
        if (info != null) {
            ActionCacheContent actionCache = new ActionCacheContent(contentType, html);
            Jboot.me().getCache().put(info.getGroup(), info.getKey(), actionCache, info.getLiveSeconds());
        }
    }
}
