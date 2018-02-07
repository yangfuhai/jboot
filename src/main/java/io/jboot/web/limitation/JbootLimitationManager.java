/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.limitation;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import io.jboot.Jboot;
import io.jboot.utils.ArrayUtils;
import io.jboot.web.limitation.annotation.EnableConcurrencyLimit;
import io.jboot.web.limitation.annotation.EnablePerIpLimit;
import io.jboot.web.limitation.annotation.EnableRequestLimit;
import io.jboot.web.limitation.annotation.EnablePerUserLimit;
import io.jboot.web.utils.ControllerUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.limitation
 */
public class JbootLimitationManager {

    private static final JbootLimitationManager me = new JbootLimitationManager();

    public static JbootLimitationManager me() {
        return me;
    }

    private Map<String, Semaphore> concurrencyRateLimiterMap = new ConcurrentHashMap<>();
    private Map<String, RateLimiter> requestRateLimiterMap = new ConcurrentHashMap<>();
    private Map<String, Object> ajaxJsonMap = new HashMap();
    private String limitView;


    /**
     * 用户请求记录
     */
    private LoadingCache<String, Long> userRequestRecord = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(key -> System.currentTimeMillis());

    /**
     * IP 请求记录
     */
    private LoadingCache<String, Long> ipRequestRecord = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(key -> System.currentTimeMillis());


    private Map<String, LimitationInfo> concurrencyRates = new ConcurrentHashMap<>();
    private Map<String, LimitationInfo> ipRates = new ConcurrentHashMap<>();
    private Map<String, LimitationInfo> requestRates = new ConcurrentHashMap<>();
    private Map<String, LimitationInfo> userRates = new ConcurrentHashMap<>();


    public void init(List<Routes.Route> routes) {
        initRates(routes);
    }

    /**
     * 初始化 invokers 变量
     */
    private void initRates(List<Routes.Route> routes) {
        Set<String> excludedMethodName = ControllerUtils.buildExcludedMethodName();

        for (Routes.Route route : routes) {
            Class<? extends Controller> controllerClass = route.getControllerClass();

            String controllerKey = route.getControllerKey();

            Annotation[] controllerAnnotations = controllerClass.getAnnotations();

            Method[] methods = controllerClass.getMethods();
            for (Method method : methods) {
                if (excludedMethodName.contains(method.getName()) || method.getParameterTypes().length != 0) {
                    continue;
                }


                Annotation[] methodAnnotations = method.getAnnotations();
                Annotation[] allAnnotations = ArrayUtils.concat(controllerAnnotations, methodAnnotations);

                String actionKey = ControllerUtils.createActionKey(controllerClass, method, controllerKey);

                for (Annotation annotation : allAnnotations) {
                    if (annotation.annotationType() == EnableConcurrencyLimit.class) {
                        concurrencyRates.put(actionKey, new LimitationInfo((EnableConcurrencyLimit) annotation));
                    } else if (annotation.annotationType() == EnablePerIpLimit.class) {
                        ipRates.put(actionKey, new LimitationInfo((EnablePerIpLimit) annotation));
                    } else if (annotation.annotationType() == EnableRequestLimit.class) {
                        requestRates.put(actionKey, new LimitationInfo((EnableRequestLimit) annotation));
                    } else if (annotation.annotationType() == EnablePerUserLimit.class) {
                        userRates.put(actionKey, new LimitationInfo((EnablePerUserLimit) annotation));
                    }
                }
            }
        }
    }

    public LimitationInfo getLimitationInfo(String actionKey){
        LimitationInfo info = concurrencyRates.get(actionKey);

        if(info != null){
            return info;
        }

        info = requestRates.get(actionKey);

        if(info != null){
            return info;
        }

        info = ipRates.get(actionKey);

        if(info != null){
            return info;
        }

        return userRates.get(actionKey);
    }


    private JbootLimitationManager() {
        LimitationConfig config = Jboot.config(LimitationConfig.class);
        ajaxJsonMap.put("code", config.getLimitAjaxCode());
        ajaxJsonMap.put("message", config.getLimitAjaxMessage());
        this.limitView = config.getLimitView();
    }


    public RateLimiter initRateLimiter(String target, double rate) {
        RateLimiter limiter = requestRateLimiterMap.get(target);
        if (limiter == null) {
            limiter = RateLimiter.create(rate);
            requestRateLimiterMap.put(target, limiter);
            return limiter;
        }

        if (limiter.getRate() == rate) {
            return limiter;
        }

        limiter.setRate(rate);
        requestRateLimiterMap.put(target, limiter);

        return limiter;
    }


    public RateLimiter getLimiter(String target) {
        return requestRateLimiterMap.get(target);
    }

    public Semaphore initSemaphore(String target, double rate) {
        Semaphore semaphore = new Semaphore((int)rate);
        concurrencyRateLimiterMap.put(target, semaphore);
        return semaphore;
    }


    public Semaphore getSemaphore(String target) {
        return concurrencyRateLimiterMap.get(target);
    }


    /**
     * 标识用户当前请求时间
     *
     * @param sessionId
     */
    public void flagUserRequest(String sessionId) {
        userRequestRecord.put(sessionId, System.currentTimeMillis());
    }

    public long getUserflag(String sessionId) {
        return userRequestRecord.get(sessionId);
    }


    /**
     * 标识IP地址当前的请求时间
     *
     * @param ip
     */
    public void flagIpRequest(String ip) {
        ipRequestRecord.put(ip, System.currentTimeMillis());
    }

    public long getIpflag(String sessionId) {
        return ipRequestRecord.get(sessionId);
    }


    public Map<String, Object> getAjaxJsonMap() {
        return ajaxJsonMap;
    }

    public String getLimitView() {
        return limitView;
    }
}
