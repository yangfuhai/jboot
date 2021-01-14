/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.sentinel;

import io.jboot.Jboot;
import io.jboot.app.config.annotation.ConfigModel;

import java.util.Map;

@ConfigModel(prefix = "jboot.sentinel")
public class SentinelConfig {

    // 是否启用
    private boolean enable = false;

    // 是否对 http 请求启用限流，启用后还需要去 sentinel 后台配置
    private boolean reqeustEnable = true;

    // 如果 http 被限流后跳转的页面
    private String requestBlockPage;

    // 如果 http 被限流后渲染的 json 数据，requestBlockPage 配置优先于此项
    private Map requestBlockJsonMap;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isReqeustEnable() {
        return reqeustEnable;
    }

    public void setReqeustEnable(boolean reqeustEnable) {
        this.reqeustEnable = reqeustEnable;
    }

    public String getRequestBlockPage() {
        return requestBlockPage;
    }

    public void setRequestBlockPage(String requestBlockPage) {
        this.requestBlockPage = requestBlockPage;
    }

    public Map getRequestBlockJsonMap() {
        return requestBlockJsonMap;
    }

    public void setRequestBlockJsonMap(Map requestBlockJsonMap) {
        this.requestBlockJsonMap = requestBlockJsonMap;
    }

    private static SentinelConfig sentinelConfig;

    public static SentinelConfig get() {
        if (sentinelConfig == null){
            sentinelConfig = Jboot.config(SentinelConfig.class);
        }
        return sentinelConfig;
    }
}
