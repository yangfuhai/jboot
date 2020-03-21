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
package io.jboot.components.rpc;

import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.utils.StrUtil;

import java.util.Map;


@ConfigModel(prefix = "jboot.rpc")
public class JbootrpcConfig {

    public static final String TYPE_DUBBO = "dubbo";
    public static final String TYPE_MOTAN = "motan";
    public static final String TYPE_LOCAL = "local";

    private String type;

    //用于直连时的配置，直连一般只用于测试环境
    //com.service.AAAService:127.0.0.1:8080,com.service.XXXService:127.0.0.1:8080
    private Map<String,String> urls;

    //本地自动暴露 @RPCBean 的 service
    private boolean autoExportEnable = true;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }

    public boolean isAutoExportEnable() {
        return autoExportEnable;
    }

    public void setAutoExportEnable(boolean autoExportEnable) {
        this.autoExportEnable = autoExportEnable;
    }

    public boolean isConfigOk() {
        return StrUtil.isNotBlank(getType());
    }
}
