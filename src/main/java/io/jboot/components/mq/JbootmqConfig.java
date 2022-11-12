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
package io.jboot.components.mq;

import com.google.common.collect.Sets;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.utils.StrUtil;

import java.util.Set;


@ConfigModel(prefix = "jboot.mq")
public class JbootmqConfig {
    public static final String TYPE_REDIS = "redis";
    public static final String TYPE_ACTIVEMQ = "activemq";
    public static final String TYPE_ALIYUNMQ = "aliyunmq";
    public static final String TYPE_RABBITMQ = "rabbitmq";
    public static final String TYPE_ROCKETMQ = "rocketmq";
    public static final String TYPE_QPID = "qpid";
    public static final String TYPE_LOCAL = "local";

    public static final Set<String> TYPES = Sets.newHashSet(TYPE_REDIS, TYPE_ACTIVEMQ, TYPE_ALIYUNMQ, TYPE_RABBITMQ
            , TYPE_ROCKETMQ, TYPE_QPID, TYPE_LOCAL);


    private String name = "default";  // MQ 的名称，可以配置多个 MQ 实例，但是需要名称不能一样
    private String type;  // MQ 的类型： redis、rocketmq 等
    private String typeName; // MQ 相同的类型，可能有多一个实例，比如两个 redis，此时需要配置实例的名称
    private String channel; // 发送的通道，或者是 topic，多个用英文逗号二开
    private String syncRecevieMessageChannel; //可同步接收消息的 channel 配置
    private String serializer; // MQ 默认的序列化方案

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSyncRecevieMessageChannel() {
        return syncRecevieMessageChannel;
    }

    public void setSyncRecevieMessageChannel(String syncRecevieMessageChannel) {
        this.syncRecevieMessageChannel = syncRecevieMessageChannel;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public boolean isConfigOk() {
        return StrUtil.isNotEmpty(type);
    }
}
