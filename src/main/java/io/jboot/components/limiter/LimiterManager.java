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
package io.jboot.components.limiter;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.RateLimiter;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class LimiterManager {

    private HashSet<LimitConfigBean> limitConfigBeans = new HashSet<>();
    private HashSet<String> ipWhitelist = new HashSet<>();

    private LimitConfig limitConfig = Jboot.config(LimitConfig.class);


    private Cache<String, Semaphore> semaphoreCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();


    private Cache<String, RateLimiter> rateLimiterCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();


    private LimitFallbackProcesser fallbackProcesser;

    private static LimiterManager me = new LimiterManager();

    private LimiterManager() {
    }

    public static LimiterManager me() {
        return me;
    }

    public void init() {
        doInitFallbackProcesser();
        doParseConfig();

        Set<String> ips = StrUtil.splitToSetByComma(limitConfig.getIpWhitelist());
        if (ips != null) {
            ipWhitelist.addAll(ips);
        }
    }

    private void doInitFallbackProcesser() {
        LimitConfig config = Jboot.config(LimitConfig.class);
        if (StrUtil.isBlank(config.getFallbackProcesser())) {
            this.fallbackProcesser = new LimitFallbackProcesserDefault();
        } else {
            this.fallbackProcesser = Objects.requireNonNull(ClassUtil.newInstance(config.getFallbackProcesser()),
                    "can not newInstance class for " + config.getFallbackProcesser());
        }
    }

    /**
     * 解析用户配置
     */
    private void doParseConfig() {

        LimitConfig config = Jboot.config(LimitConfig.class);

        if (!isEnable()) {
            return;
        }

        String rule = config.getRule();
        if (StrUtil.isBlank(rule)) {
            return;
        }

        String[] rules = rule.split(",");
        for (String r : rules) {
            String[] confs = r.split(":");
            if (confs == null || confs.length != 3) {
                continue;
            }

            String packageOrTarget = confs[0];
            String type = confs[1];
            String rate = confs[2];

            if (!ensureLegal(packageOrTarget, type, rate.trim())) {
                continue;
            }

            packageOrTarget = packageOrTarget.replace(".", "\\.")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("*", ".*");

            limitConfigBeans.add(new LimitConfigBean(packageOrTarget.trim(),type.trim(), Integer.valueOf(rate.trim())));
        }
    }


    /**
     * 匹配用户配置
     *
     * @param packageOrTarget
     * @return
     */
    public LimitConfigBean matchConfig(String packageOrTarget) {

        if (!isEnable() || limitConfigBeans.isEmpty()) {
            return null;
        }

        for (LimitConfigBean value : limitConfigBeans) {
            if (value.isMatched(packageOrTarget)){
                return value;
            }
        }

        return null;
    }


    public boolean isInIpWhitelist(String ip){
        return !ipWhitelist.isEmpty() && ipWhitelist.contains(ip);
    }

    public RateLimiter getOrCreateRateLimiter(String resKey, int rate) {
        return rateLimiterCache.get(resKey, s -> RateLimiter.create(rate));
    }


    public Semaphore getOrCreateSemaphore(String resKey, int rate) {
        return semaphoreCache.get(resKey, s -> new Semaphore(rate));
    }

    public HashSet<LimitConfigBean> getLimitConfigBeans() {
        return limitConfigBeans;
    }

    /**
     * 确保配置合法
     *
     * @param packageOrTarget
     * @param type
     * @param rate
     * @return
     */
    private boolean ensureLegal(String packageOrTarget, String type, String rate) {
        if (StrUtil.isBlank(packageOrTarget)) {
            return false;
        }

        if (!LimitType.types.contains(type)) {
            return false;
        }

        if (!StrUtil.isNumeric(rate)) {
            return false;
        }

        return true;
    }

    public boolean isEnable() {
        return limitConfig.isEnable();
    }

    public void processFallback(String resource, String fallback, Invocation inv) {
        fallbackProcesser.process(resource, fallback, inv);
    }

    public static class LimitConfigBean {

        private String packageOrTarget;
        private String type;
        private int rate;
        private Pattern pattern;


        public LimitConfigBean(String packageOrTarget, String type, int rate) {
            this.packageOrTarget = packageOrTarget;
            this.type = type;
            this.rate = rate;
            this.pattern = Pattern.compile(packageOrTarget);
        }

        public String getPackageOrTarget() {
            return packageOrTarget;
        }

        public void setPackageOrTarget(String packageOrTarget) {
            this.packageOrTarget = packageOrTarget;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getRate() {
            return rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }

        public boolean isMatched(String packageOrTarget){
            return pattern.matcher(packageOrTarget).matches();
        }
    }
}
