/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.cache.interceptor;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.cache.AopCache;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.JbootModel;
import io.jboot.exception.JbootException;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.ModelUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 缓存操作的拦截器
 *
 * @author michael yang
 */
public class CacheableInterceptor implements Interceptor {

    private static final String NULL_VALUE = "NULL_VALUE";
    public static final String IGNORE_CACHED_ATTRS = "__ignore_cached_attrs";

    //是否开启 Controller 的 Action 缓存
    //可用在 dev 模式下关闭，生产环境开启的场景，方便调试数据
    private static boolean actionCacheEnable = true;

    public static boolean isActionCacheEnable() {
        return actionCacheEnable;
    }

    public static void setActionCacheEnable(boolean actionCacheEnable) {
        CacheableInterceptor.actionCacheEnable = actionCacheEnable;
    }

    @Override
    public void intercept(Invocation inv) {

        Method method = inv.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if (cacheable == null || (inv.isActionInvocation() && !actionCacheEnable)) {
            inv.invoke();
            return;
        }

        if (inv.isActionInvocation()) {
            forController(inv, method, cacheable);
        } else {
            forService(inv, method, cacheable);
        }
    }


    private void forController(Invocation inv, Method method, Cacheable cacheable) {
        String unlessString = AnnotationUtil.get(cacheable.unless());
        if (Utils.isUnless(unlessString, method, inv.getArgs())) {
            inv.invoke();
            return;
        }

        Class<?> targetClass = inv.getTarget().getClass();
        String cacheName = AnnotationUtil.get(cacheable.name());
        Utils.ensureCacheNameNotBlank(method, cacheName);
        String cacheKey = Utils.buildCacheKey(AnnotationUtil.get(cacheable.key()), targetClass, method, inv.getArgs());

        Controller controller = inv.getController();

        ActionCachedContent actionCachedContent = AopCache.get(cacheName, cacheKey);
        if (actionCachedContent != null) {
            renderActionCachedContent(controller, actionCachedContent);
            return;
        }

        inv.invoke();
        cacheActionContent(cacheName, cacheKey, cacheable.liveSeconds(), controller);
    }


    /**
     * 对 action 内容进行缓存
     *
     * @param cacheName
     * @param cacheKey
     * @param liveSeconds
     * @param controller
     */
    public static void cacheActionContent(String cacheName, String cacheKey, int liveSeconds, Controller controller) {

        ActionCachedContent cachedContent = new ActionCachedContent(controller.getRender());

        // 忽略的缓存配置
        Set<String> ignoreCachedAttrs = controller.getAttr(IGNORE_CACHED_ATTRS);
        if (ignoreCachedAttrs != null) {
            ignoreCachedAttrs.add(IGNORE_CACHED_ATTRS);
        }

        HttpServletRequest request = controller.getRequest();
        for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            if (ignoreCachedAttrs == null || !ignoreCachedAttrs.contains(name)) {
                cachedContent.addAttr(name, request.getAttribute(name));
            }
        }

        HttpServletResponse response = controller.getResponse();
        Collection<String> headerNames = response.getHeaderNames();
        headerNames.forEach(name -> cachedContent.addHeader(name, response.getHeader(name)));

        AopCache.putDataToCache(cacheName, cacheKey, cachedContent, liveSeconds);
    }


    /**
     * 渲染缓存的 ActionCachedContent
     *
     * @param controller
     * @param actionCachedContent
     */
    private void renderActionCachedContent(Controller controller, ActionCachedContent actionCachedContent) {
        Map<String, Object> cachedAttrs = actionCachedContent.getAttrs();
        if (cachedAttrs != null) {
            HttpServletRequest request = controller.getRequest();
            Set<String> existAttrNames = getRequestAttrNames(request);
            cachedAttrs.forEach((cachedAttrName, value) -> {
                if (!existAttrNames.contains(cachedAttrName)) {
                    request.setAttribute(cachedAttrName, value);
                }
            });
        }


        Map<String, String> headers = actionCachedContent.getHeaders();
        if (headers != null) {
            HttpServletResponse response = controller.getResponse();
            headers.forEach((name, value) -> {
                String existHeaderValue = response.getHeader(name);
                if (existHeaderValue != null) {
                    value = existHeaderValue;
                }
                response.setHeader(name, value);
            });
        }

        controller.render(actionCachedContent.createRender());
    }


    private Set<String> getRequestAttrNames(HttpServletRequest request) {
        Set<String> ret = new HashSet<>();
        for (Enumeration<String> attrNames = request.getAttributeNames(); attrNames.hasMoreElements(); ) {
            ret.add(attrNames.nextElement());
        }
        return ret;
    }

    /**
     * Service 层的 Cacheable 使用
     *
     * @param inv
     * @param method
     * @param cacheable
     */
    private void forService(Invocation inv, Method method, Cacheable cacheable) {
        String unlessString = AnnotationUtil.get(cacheable.unless());
        if (Utils.isUnless(unlessString, method, inv.getArgs())) {
            inv.invoke();
            return;
        }

        Class<?> targetClass = inv.getTarget().getClass();
        String cacheName = AnnotationUtil.get(cacheable.name());
        Utils.ensureCacheNameNotBlank(method, cacheName);
        String cacheKey = Utils.buildCacheKey(AnnotationUtil.get(cacheable.key()), targetClass, method, inv.getArgs());

        Object data = AopCache.get(cacheName, cacheKey);
        if (data != null) {
            if (NULL_VALUE.equals(data)) {
                inv.setReturnValue(null);
            } else if (cacheable.returnCopyEnable()) {
                inv.setReturnValue(getCopyObject(inv, data));
            } else {
                inv.setReturnValue(data);
            }
        } else {
            inv.invoke();
            data = inv.getReturnValue();
            if (data != null) {

                AopCache.putDataToCache(cacheName, cacheKey, data, cacheable.liveSeconds());

                //当启用返回 copy 值的时候，返回的内容应该是一个进行copy之后的值
                if (cacheable.returnCopyEnable()) {
                    inv.setReturnValue(getCopyObject(inv, data));
                }

            } else if (cacheable.nullCacheEnable()) {
                AopCache.putDataToCache(cacheName, cacheKey, NULL_VALUE, cacheable.liveSeconds());
            }
        }
    }


    private <M extends JbootModel> Object getCopyObject(Invocation inv, Object data) {
        if (data instanceof List) {
            return ModelUtil.copy((List<? extends JbootModel>) data);
        } else if (data instanceof Set) {
            return ModelUtil.copy((Set<? extends JbootModel>) data);
        } else if (data instanceof Page) {
            return ModelUtil.copy((Page<? extends JbootModel>) data);
        } else if (data instanceof JbootModel) {
            return ModelUtil.copy((JbootModel) data);
        } else if (data.getClass().isArray()
                && JbootModel.class.isAssignableFrom(data.getClass().getComponentType())) {
            return ModelUtil.copy((M[]) data);
        } else {
            throw newException(null, inv, data);
        }
    }


    private JbootException newException(Exception ex, Invocation inv, Object data) {
        String msg = "Can not copy data for type [" + data.getClass().getName() + "] in method :"
                + ClassUtil.buildMethodString(inv.getMethod())
                + " , can not use @Cacheable(returnCopyEnable=true) annotation";

        return ex == null ? new JbootException(msg) : new JbootException(msg, ex);
    }


}
