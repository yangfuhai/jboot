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
import com.jfinal.core.CPI;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.cache.ActionCache;
import io.jboot.components.cache.AopCache;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.JbootModel;
import io.jboot.exception.JbootException;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.ModelUtil;
import io.jboot.web.cached.CacheSupportResponseProxy;
import io.jboot.web.cached.CachedContent;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存操作的拦截器
 *
 * @author michael yang
 */
public class CacheableInterceptor implements Interceptor {

    private static final String NULL_VALUE = "NULL_VALUE";

    @Override
    public void intercept(Invocation inv) {

        Method method = inv.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if (cacheable == null) {
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

        Class<?> targetClass = inv.getTarget().getClass();
        String cacheName = AnnotationUtil.get(cacheable.name());
        Utils.ensureCacheNameNotBlank(method, cacheName);
        String cacheKey = Utils.buildCacheKey(AnnotationUtil.get(cacheable.key()), targetClass, method, inv.getArgs());

        Controller controller = inv.getController();

        CachedContent cachedContent = ActionCache.get(cacheName, cacheKey);
        if (cachedContent != null) {
            writeCachedContent(controller, cachedContent);
            return;
        }


        CacheSupportResponseProxy responseProxy = new CacheSupportResponseProxy(controller.getResponse());
        responseProxy.setCacheName(cacheName);
        responseProxy.setCacheKey(cacheKey);
        responseProxy.setCacheLiveSeconds(cacheable.liveSeconds());

        //让 Controller 持有缓存的 responseProxy
        CPI._init_(controller, CPI.getAction(controller), controller.getRequest(), responseProxy, controller.getPara());

        inv.invoke();

    }


    private void writeCachedContent(Controller controller, CachedContent cachedContent) {
        HttpServletResponse response = controller.getResponse();
        Map<String, String> headers = cachedContent.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(response::addHeader);
        }
        controller.render(cachedContent.createRender());
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
