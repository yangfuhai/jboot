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
package io.jboot.components.mq.rabbitmq;

import io.jboot.app.config.annotation.ConfigModel;


@ConfigModel(prefix = "jboot.mq.rabbitmq")
public class JbootRabbitmqConfig {


    /**
     * 默认 username 为  guest
     */
    private String username;

    /**
     * 默认密码为 guest
     */
    private String password;

    private String host = "127.0.0.1";
    private int port = 5672;
    private String virtualHost;

    private String broadcastChannelPrefix = "broadcast-";
    private String broadcastChannelRoutingKey = "";

    //若配置为 false，则需要在 OnMessage 里，调用 MessageContext.getChannel().baseAck（或者baseNack）进行消费（或者标识消费失败）
    private boolean autoAck = true;

    private boolean queueDeclareDurable = false;
    private boolean queueDeclareExclusive = false;
    private boolean queueDeclareAutoDelete = false;


    private String broadcastExchangeDeclareExchangeType = "fanout";
    private boolean broadcastExchangeDeclareDurable = false;

    private boolean broadcastQueueDeclareDurable = false;
    private boolean broadcastQueueDeclareExclusive = false;
    private boolean broadcastQueueDeclareAutoDelete = true;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getBroadcastChannelPrefix() {
        return broadcastChannelPrefix;
    }

    public void setBroadcastChannelPrefix(String broadcastChannelPrefix) {
        this.broadcastChannelPrefix = broadcastChannelPrefix;
    }

    public String getBroadcastChannelRoutingKey() {
        return broadcastChannelRoutingKey;
    }

    public void setBroadcastChannelRoutingKey(String broadcastChannelRoutingKey) {
        this.broadcastChannelRoutingKey = broadcastChannelRoutingKey;
    }

    public boolean isAutoAck() {
        return autoAck;
    }

    public void setAutoAck(boolean autoAck) {
        this.autoAck = autoAck;
    }

    public boolean isQueueDeclareDurable() {
        return queueDeclareDurable;
    }

    public void setQueueDeclareDurable(boolean queueDeclareDurable) {
        this.queueDeclareDurable = queueDeclareDurable;
    }

    public boolean isQueueDeclareExclusive() {
        return queueDeclareExclusive;
    }

    public void setQueueDeclareExclusive(boolean queueDeclareExclusive) {
        this.queueDeclareExclusive = queueDeclareExclusive;
    }

    public boolean isQueueDeclareAutoDelete() {
        return queueDeclareAutoDelete;
    }

    public void setQueueDeclareAutoDelete(boolean queueDeclareAutoDelete) {
        this.queueDeclareAutoDelete = queueDeclareAutoDelete;
    }

    public String getBroadcastExchangeDeclareExchangeType() {
        return broadcastExchangeDeclareExchangeType;
    }

    public void setBroadcastExchangeDeclareExchangeType(String broadcastExchangeDeclareExchangeType) {
        this.broadcastExchangeDeclareExchangeType = broadcastExchangeDeclareExchangeType;
    }

    public boolean isBroadcastExchangeDeclareDurable() {
        return broadcastExchangeDeclareDurable;
    }

    public void setBroadcastExchangeDeclareDurable(boolean broadcastExchangeDeclareDurable) {
        this.broadcastExchangeDeclareDurable = broadcastExchangeDeclareDurable;
    }

    public boolean isBroadcastQueueDeclareDurable() {
        return broadcastQueueDeclareDurable;
    }

    public void setBroadcastQueueDeclareDurable(boolean broadcastQueueDeclareDurable) {
        this.broadcastQueueDeclareDurable = broadcastQueueDeclareDurable;
    }

    public boolean isBroadcastQueueDeclareExclusive() {
        return broadcastQueueDeclareExclusive;
    }

    public void setBroadcastQueueDeclareExclusive(boolean broadcastQueueDeclareExclusive) {
        this.broadcastQueueDeclareExclusive = broadcastQueueDeclareExclusive;
    }

    public boolean isBroadcastQueueDeclareAutoDelete() {
        return broadcastQueueDeclareAutoDelete;
    }

    public void setBroadcastQueueDeclareAutoDelete(boolean broadcastQueueDeclareAutoDelete) {
        this.broadcastQueueDeclareAutoDelete = broadcastQueueDeclareAutoDelete;
    }
}
