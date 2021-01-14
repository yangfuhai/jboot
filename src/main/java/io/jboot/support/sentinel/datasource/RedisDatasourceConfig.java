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

@ConfigModel(prefix = "jboot.sentinel.datasource.redis")
public class RedisDatasourceConfig {

    private String host;
    private int port = 6379;
    private String password;
    private int database = 0;
    private String ruleKey = "jboot-sentinel-rule";
    private String channel = "jboot-sentinel-channel";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getRuleKey() {
        return ruleKey;
    }

    public void setRuleKey(String ruleKey) {
        this.ruleKey = ruleKey;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void assertConfigOk() {
        if (StrUtil.isAnyBlank(host)) {
            throw new JbootIllegalConfigException("jboot.sentinel.datasource.redis not config well in jboot.properties");
        }
    }
}
