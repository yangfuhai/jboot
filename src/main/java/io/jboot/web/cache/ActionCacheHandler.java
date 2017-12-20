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
import io.jboot.Jboot;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.render.JbootRenderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ActionCacheHandler extends Handler {

    private static String[] urlPara = {null};
    private static Log LOG = Log.getLog(ActionCacheHandler.class);

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

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

        ActionCacheEnable actionCache = getActionCache(action);
        if (actionCache == null) {
            next.handle(target, request, response, isHandled);
            return;
        }

        try {
            ActionCacheContext.hold(actionCache);
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
        String[] cacheNames = actionClear.value();
        if (ArrayUtils.isNullOrEmpty(cacheNames)) {
            throw new IllegalArgumentException("ActionCacheClear annotation argument must not be empty " +
                    "in " + action.getControllerClass().getName() + "." + action.getMethodName());
        }

        for (String cacheName : cacheNames) {
            if (StringUtils.isNotBlank(cacheName)) {
                Jboot.me().getCache().removeAll(cacheName);
            }
        }
    }

    public ActionCacheEnable getActionCache(Action action) {
        ActionCacheEnable actionCache = action.getMethod().getAnnotation(ActionCacheEnable.class);
        return actionCache != null ? actionCache : action.getControllerClass().getAnnotation(ActionCacheEnable.class);
    }

    private void exec(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled, Action action, ActionCacheEnable actionCacheEnable) {
        String cacheName = actionCacheEnable.cacheName();
        if (StringUtils.isBlank(cacheName)) {
            throw new IllegalArgumentException("ActionCacheEnable cacheName must not be empty " +
                    "in " + action.getControllerClass().getName() + "." + action.getMethodName());
        }
        String cacheKey = target;

        String queryString = request.getQueryString();
        if (queryString != null) {
            queryString = "?" + queryString;
            cacheKey += queryString;
        }

        ActionCacheContext.holdKey(cacheKey);

        ActionCache actionCache = Jboot.me().getCache().get(cacheName, cacheKey);
        if (actionCache != null) {
            response.setContentType(actionCache.getContentType());
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                writer.write(actionCache.getContent());
                writer.flush();
                isHandled[0] = true;
            } catch (Exception e) {
                LOG.error(e.toString(), e);
                JbootRenderFactory.me().getErrorRender(500).setContext(request, response, action.getViewPath()).render();
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } else {
            next.handle(target, request, response, isHandled);
        }
    }


}
