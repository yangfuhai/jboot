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
package io.jboot.components.mq.rocketmq;

import io.jboot.app.config.annotation.ConfigModel;

import java.io.Serializable;


@ConfigModel(prefix = "jboot.mq.rocket")
public class JbootRocketmqConfig implements Serializable {

    private String namesrvAddr;
    private String namespace;
    private String consumerGroup = "jboot_default_consumer_group";
    private Integer consumeMessageBatchMaxSize;
    private String broadcastChannelPrefix = "broadcast-";
    private String subscribeSubExpression = "*";

    private String producerGroup = "jboot_default_producer_group";
    private String instanceName;
    private String clientIP;
    private String createTopicKey;
    private Boolean useTLS;

    private Boolean sendLatencyFaultEnable;
    private Boolean sendMessageWithVIPChannel;
    private Integer sendMsgTimeout;

    private Boolean retryAnotherBrokerWhenNotStoreOK;
    private Integer retryTimesWhenSendAsyncFailed;
    private Integer retryTimesWhenSendFailed;

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Integer getConsumeMessageBatchMaxSize() {
        return consumeMessageBatchMaxSize;
    }

    public void setConsumeMessageBatchMaxSize(Integer consumeMessageBatchMaxSize) {
        this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
    }

    public String getBroadcastChannelPrefix() {
        return broadcastChannelPrefix;
    }

    public void setBroadcastChannelPrefix(String broadcastChannelPrefix) {
        this.broadcastChannelPrefix = broadcastChannelPrefix;
    }

    public String getSubscribeSubExpression() {
        return subscribeSubExpression;
    }

    public void setSubscribeSubExpression(String subscribeSubExpression) {
        this.subscribeSubExpression = subscribeSubExpression;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getCreateTopicKey() {
        return createTopicKey;
    }

    public void setCreateTopicKey(String createTopicKey) {
        this.createTopicKey = createTopicKey;
    }

    public Boolean getUseTLS() {
        return useTLS;
    }

    public void setUseTLS(Boolean useTLS) {
        this.useTLS = useTLS;
    }

    public Boolean getSendLatencyFaultEnable() {
        return sendLatencyFaultEnable;
    }

    public void setSendLatencyFaultEnable(Boolean sendLatencyFaultEnable) {
        this.sendLatencyFaultEnable = sendLatencyFaultEnable;
    }

    public Boolean getSendMessageWithVIPChannel() {
        return sendMessageWithVIPChannel;
    }

    public void setSendMessageWithVIPChannel(Boolean sendMessageWithVIPChannel) {
        this.sendMessageWithVIPChannel = sendMessageWithVIPChannel;
    }

    public Integer getSendMsgTimeout() {
        return sendMsgTimeout;
    }

    public void setSendMsgTimeout(Integer sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }

    public Boolean getRetryAnotherBrokerWhenNotStoreOK() {
        return retryAnotherBrokerWhenNotStoreOK;
    }

    public void setRetryAnotherBrokerWhenNotStoreOK(Boolean retryAnotherBrokerWhenNotStoreOK) {
        this.retryAnotherBrokerWhenNotStoreOK = retryAnotherBrokerWhenNotStoreOK;
    }

    public Integer getRetryTimesWhenSendAsyncFailed() {
        return retryTimesWhenSendAsyncFailed;
    }

    public void setRetryTimesWhenSendAsyncFailed(Integer retryTimesWhenSendAsyncFailed) {
        this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
    }

    public Integer getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }

    public void setRetryTimesWhenSendFailed(Integer retryTimesWhenSendFailed) {
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
    }
}
