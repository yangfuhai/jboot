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
package io.jboot.support.sentinel.datasource;

import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StrUtil;

@ConfigModel(prefix = "jboot.sentinel.datasource.zookeeper")
public class ZookeeperDatasourceConfig {

    private String serverAddress; // 例如： 127.0.0.1:2181
    private String path="/jboot/sentinel/rule/default"; // 例如  /Sentinel-Demo/SYSTEM-CODE-DEMO-FLOW

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void assertConfigOk() {
        if (StrUtil.isAnyBlank(serverAddress)) {
            throw new JbootIllegalConfigException("jboot.sentinel.datasource.zookeeper not config well in jboot.properties");
        }
    }
}