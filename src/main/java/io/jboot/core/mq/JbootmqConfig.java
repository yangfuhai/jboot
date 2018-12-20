/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.mq;

import io.jboot.app.config.annotation.ConfigModel;


@ConfigModel(prefix = "jboot.mq")
public class JbootmqConfig {
    public static final String TYPE_REDIS = "redis";
    public static final String TYPE_ACTIVEMQ = "activemq";
    public static final String TYPE_ALIYUNMQ = "aliyunmq";
    public static final String TYPE_RABBITMQ = "rabbitmq";
    public static final String TYPE_ZBUS = "zbus";
    public static final String TYPE_QPID = "qpid";

    private String type = TYPE_REDIS;
    private String channel;
    private String syncRecevieMessageChannel; //可同步接收消息的channel配置
    private String serializer;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public String getSyncRecevieMessageChannel() {
        return syncRecevieMessageChannel;
    }

    public void setSyncRecevieMessageChannel(String syncRecevieMessageChannel) {
        this.syncRecevieMessageChannel = syncRecevieMessageChannel;
    }
}
