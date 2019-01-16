/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.cache;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.jfinal.render.RenderManager;
import io.jboot.Jboot;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.JbootWebConfig;
import io.jboot.web.cache.keygen.ActionKeyGeneratorManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionCacheHandler extends Handler {

    private static String[] urlPara = {null};
    private static Log LOG = Log.getLog(ActionCacheHandler.class);
    private static JbootWebConfig webConfig = Jboot.config(JbootWebConfig.class);

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (!webConfig.isActionCacheEnable()) {
            next.handle(target, request, response, isHandled);
            return;
        }

        Action action = JFinal.me().getAction(target, urlPara);
        if (action == null) {
            next.handle(target, request, response, isHandled);
            return;
        }

        ActionCacheClear actionClear = action.getMethod().getAnnotation(ActionCacheClear.class);
        if (actionClear != null) {
            clearActionCache(action, actionClear);
            next.handle(target, request, response, isHandled);
            return;
        }

        EnableActionCache actionCache = getActionCache(action);
        if (actionCache == null) {
            next.handle(target, request, response, isHandled);
            return;
        }

        try {
            exec(target, request, response, isHandled, action, actionCache);
        } finally {
            ActionCacheContext.release();
        }

    }

    /**
     * 清空 页面缓存
     *
     * @param action
     * @param actionClear
     */
    private void clearActionCache(Action action, ActionCacheClear actionClear) {
        String[] cacheNames = AnnotationUtil.get(actionClear.value());
        if (ArrayUtil.isNullOrEmpty(cacheNames)) {
            throw new IllegalArgumentException("ActionCacheClear annotation argument must not be empty " +
                    "in " + action.getControllerClass().getName() + "." + action.getMethodName());
        }

        for (String cacheName : cacheNames) {
            if (StrUtil.isNotBlank(cacheName)) {
                Jboot.getCache().removeAll(cacheName);
            }
        }
    }

    public EnableActionCache getActionCache(Action action) {
        EnableActionCache actionCache = action.getMethod().getAnnotation(EnableActionCache.class);
        return actionCache != null ? actionCache : action.getControllerClass().getAnnotation(EnableActionCache.class);
    }


    private void exec(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled, Action action, EnableActionCache actionCacheEnable) {

        //缓存名称
        String cacheName = AnnotationUtil.get(actionCacheEnable.group());
        if (StrUtil.isBlank(cacheName)) {
            throw new IllegalArgumentException("EnableActionCache group must not be empty " +
                    "in " + action.getControllerClass().getName() + "." + action.getMethodName());
        }

        if (cacheName.contains("#(") && cacheName.contains(")")) {
            cacheName = regexGetCacheName(cacheName, request);
        }

        //缓存的key
        String cacheKey = ActionKeyGeneratorManager.me().getGenerator().generate(target, request);
        if (StrUtil.isBlank(cacheKey)) {
            next.handle(target, request, response, isHandled);
            return;
        }

        ActionCacheContent actionCache = Jboot.getCache().get(cacheName, cacheKey);
        if (actionCache != null) {
            renderCache(request, response, actionCache, action);
            isHandled[0] = true;
            return;
        }

        ActionCacheInfo info = new ActionCacheInfo();
        info.setGroup(cacheName);
        info.setKey(cacheKey);
        info.setLiveSeconds(actionCacheEnable.liveSeconds());
        ActionCacheContext.hold(info);

        next.handle(target, request, response, isHandled);

    }

    private void renderCache(HttpServletRequest request, HttpServletResponse response, ActionCacheContent actionCache, Action action) {
        response.setContentType(actionCache.getContentType());
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(actionCache.getContent());
            writer.flush();

        } catch (Exception e) {
            LOG.error(e.toString(), e);
            RenderManager.me()
                    .getRenderFactory()
                    .getErrorRender(500).setContext(request, response, action.getViewPath())
                    .render();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static final Pattern pattern = Pattern.compile("#\\(\\S+?\\)");

    private String regexGetCacheName(String cacheName, HttpServletRequest request) {
        Matcher m = pattern.matcher(cacheName);
        while (m.find()) {
            // find 的值 ： #(id)
            String find = m.group(0);
            String parameterName = find.substring(2, find.length() - 1);
            String value = request.getParameter(parameterName);
            if (StrUtil.isBlank(value)) value = "";
            cacheName = cacheName.replace(find, value);
        }

        return cacheName;
    }


}
