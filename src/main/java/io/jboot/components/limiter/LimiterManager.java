/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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


import com.google.common.util.concurrent.RateLimiter;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LimiterManager {

    private Set<String> configPackageOrTargets = new HashSet<>();
    private Map<String, TypeAndRate> typeAndRateCache = new HashMap<>();


    private Map<String, Semaphore> semaphoreCache = new ConcurrentHashMap<>();
    private Map<String, RateLimiter> rateLimiterCache = new ConcurrentHashMap<>();

    private Boolean enable;
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

            configPackageOrTargets.add(packageOrTarget.trim());
            typeAndRateCache.put(packageOrTarget.trim(), new TypeAndRate(type.trim(), Integer.valueOf(rate.trim())));
        }
    }


    /**
     * 匹配用户配置
     *
     * @param packageOrTarget
     * @return
     */
    public TypeAndRate matchConfig(String packageOrTarget) {

        if (!isEnable() || configPackageOrTargets.isEmpty()) {
            return null;
        }

        for (String configPackageOrTarget : configPackageOrTargets) {
            if (match(packageOrTarget, configPackageOrTarget)) {
                return typeAndRateCache.get(configPackageOrTarget);
            }
        }

        return null;
    }

    public RateLimiter getOrCreateRateLimiter(String resource, int rate) {
        RateLimiter limiter = rateLimiterCache.get(resource);
        if (limiter == null || limiter.getRate() != rate) {
            synchronized (resource.intern()) {
                limiter = rateLimiterCache.get(resource);
                if (limiter == null) {
                    limiter = RateLimiter.create(rate);
                    rateLimiterCache.put(resource, limiter);
                }
            }
        }
        return limiter;
    }

    public Semaphore getOrCreateSemaphore(String resource, int rate) {
        Semaphore semaphore = semaphoreCache.get(resource);
        if (semaphore == null) {
            synchronized (resource.intern()) {
                semaphore = semaphoreCache.get(resource);
                if (semaphore == null) {
                    semaphore = new Semaphore(rate);
                    semaphoreCache.put(resource, semaphore);
                }
            }
        }
        return semaphore;
    }


    private static boolean match(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
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

        if (!LimitType.CONCURRENCY.equals(type) && !LimitType.TOKEN_BUCKET.equals(type)) {
            return false;
        }

        if (!StrUtil.isNumeric(rate)) {
            return false;
        }

        return true;
    }

    public boolean isEnable() {
        if (enable == null) {
            enable = Jboot.config(LimitConfig.class).isEnable();
        }
        return enable;
    }

    public void processFallback(String resource, String fallback, Invocation inv) {
        fallbackProcesser.process(resource, fallback, inv);
    }

    public static class TypeAndRate {
        private String type;
        private int rate;

        public TypeAndRate(String type, int rate) {
            this.type = type;
            this.rate = rate;
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
    }
}
