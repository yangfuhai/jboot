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

import io.jboot.web.limitation.annotation.EnableConcurrencyLimit;
import io.jboot.web.limitation.annotation.EnablePerIpLimit;
import io.jboot.web.limitation.annotation.EnableRequestLimit;
import io.jboot.web.limitation.annotation.EnablePerUserLimit;

import java.io.Serializable;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.limitation
 */
public class LimitationInfo implements Serializable {

    public static final String TYPE_IP = "ip";
    public static final String TYPE_REQUEST = "request";
    public static final String TYPE_USER = "user";
    public static final String TYPE_CONCURRENCY = "concurrency";

    private String type;

    private double rate; //每秒钟允许通过的次数

    /**
     * 被限流后给用户的反馈操作
     * 支持：json，render，text，redirect
     *
     * @return
     */
    private String renderType;

    /**
     * 被限流后给客户端的响应，响应的内容根据 action 的类型来渲染
     *
     * @return
     */
    private String renderContent;

    private boolean enable = true;

    public LimitationInfo() {
    }

    public LimitationInfo(EnableConcurrencyLimit limit) {
        this.type = TYPE_CONCURRENCY;
        this.rate = limit.rate();
        this.renderType = limit.renderType();
        this.renderContent = limit.renderContent();
    }

    public LimitationInfo(EnablePerIpLimit limit) {
        this.type = TYPE_IP;
        this.rate = limit.rate();
        this.renderType = limit.renderType();
        this.renderContent = limit.renderContent();
    }

    public LimitationInfo(EnableRequestLimit limit) {
        this.type = TYPE_REQUEST;
        this.rate = limit.rate();
        this.renderType = limit.renderType();
        this.renderContent = limit.renderContent();
    }

    public LimitationInfo(EnablePerUserLimit limit) {
        this.type = TYPE_USER;
        this.rate = limit.rate();
        this.renderType = limit.renderType();
        this.renderContent = limit.renderContent();
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getRenderType() {
        return renderType;
    }

    public void setRenderType(String renderType) {
        this.renderType = renderType;
    }

    public String getRenderContent() {
        return renderContent;
    }

    public void setRenderContent(String renderContent) {
        this.renderContent = renderContent;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isIpType() {
        return TYPE_IP.equals(type);
    }

    public boolean isRequestType() {
        return TYPE_REQUEST.equals(type);
    }

    public boolean isUserType() {
        return TYPE_USER.equals(type);
    }

    public boolean isConcurrencyType() {
        return TYPE_CONCURRENCY.equals(type);
    }

}
