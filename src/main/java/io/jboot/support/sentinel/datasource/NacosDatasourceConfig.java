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
package io.jboot.support.sentinel.datasource;

import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StrUtil;

@ConfigModel(prefix = "jboot.sentinel.datasource.nacos")
public class NacosDatasourceConfig {

    private String serverAddress; // 例如： http://localhost:8080
    private String groupId;
    private String dataId;

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public void assertConfigOk() {
        if (StrUtil.isAnyBlank(serverAddress, groupId, dataId)) {
            throw new JbootIllegalConfigException("jboot.sentinel.datasource.nacos not config well in jboot.properties");
        }
    }
}
