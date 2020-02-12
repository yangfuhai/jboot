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

import io.jboot.app.config.annotation.ConfigModel;

/**
 * 限流的规则 （rule 的值）：
 * 对于Controller的限流：/user/xxx/*:cc:50,/user/xxx/*:tb:50
 * 对于Service方法的限流：
 * 1）*.*(*): 所有包下的所有方法的所有参数类型
 * 2）com.xxx.*.aaa*(int,User): com.xxx包下的所有aaa开头的方法，参数类型为(int,User)
 */
@ConfigModel(prefix = "jboot.limit")
public class LimitConfig {

    /**
     * 是否开启限流配置，这个的开启或关闭对注解的限流配置不影响
     */
    private boolean enable = false;

    /**
     * 限流规则，多个规则用英文逗号隔开
     */
    private String rule;

    /**
     * 默认的降级处理器（被限流后的处理器）
     */
    private String fallbackProcesser;

    /**
     * 被限流后，默认的http code
     */
    private int defaultHttpCode = 200;

    /**
     * 被限流后，当ajax请求的时候，返回默认的json传
     */
    private String defaultAjaxContent;

    /**
     * 被限流后，当http请求的时候，默认渲染的html文件
     */
    private String defaultHtmlView;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getFallbackProcesser() {
        return fallbackProcesser;
    }

    public void setFallbackProcesser(String fallbackProcesser) {
        this.fallbackProcesser = fallbackProcesser;
    }

    public int getDefaultHttpCode() {
        return defaultHttpCode;
    }

    public void setDefaultHttpCode(int defaultHttpCode) {
        this.defaultHttpCode = defaultHttpCode;
    }

    public String getDefaultAjaxContent() {
        return defaultAjaxContent;
    }

    public void setDefaultAjaxContent(String defaultAjaxContent) {
        this.defaultAjaxContent = defaultAjaxContent;
    }

    public String getDefaultHtmlView() {
        return defaultHtmlView;
    }

    public void setDefaultHtmlView(String defaultHtmlView) {
        this.defaultHtmlView = defaultHtmlView;
    }
}
