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
package io.jboot.components.mq.aliyunmq;

import com.aliyun.openservices.ons.api.*;
import io.jboot.Jboot;
import io.jboot.components.mq.Jbootmq;
import io.jboot.components.mq.JbootmqBase;

import java.util.Properties;


public class JbootAliyunmqImpl extends JbootmqBase implements Jbootmq {

    private Producer producer;
    private Consumer consumer;
    private JbootAliyunmqConfig aliyunmqConfig;

    public JbootAliyunmqImpl() {
        super();
        aliyunmqConfig = Jboot.config(JbootAliyunmqConfig.class);
    }


    @Override
    protected void onStartListening() {
        startQueueConsumer();
        startBroadCastConsumer();
    }


    private void startQueueConsumer() {
        Properties properties = createProperties();
        consumer = ONSFactory.createConsumer(properties);
        for (String channel : channels) {
            consumer.subscribe(aliyunmqConfig.getBroadcastChannelPrefix() + channel, "*", (message, consumeContext) -> {
                notifyListeners(channel, getSerializer().deserialize(message.getBody()));
                return Action.CommitMessage;
            });
        }
        consumer.start();
    }


    private void startBroadCastConsumer() {
        Properties properties = createProperties();
        properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.BROADCASTING);
        consumer = ONSFactory.createConsumer(properties);
        for (String channel : channels) {
            consumer.subscribe(channel, "*", (message, consumeContext) -> {
                notifyListeners(channel, getSerializer().deserialize(message.getBody()));
                return Action.CommitMessage;
            });
        }
        consumer.start();
    }


    @Override
    public void enqueue(Object message, String toChannel) {
        byte[] bytes = getSerializer().serialize(message);
        Message onsMessage = new Message(toChannel, "*", bytes);
        getProducer().send(onsMessage);
    }


    @Override
    public void publish(Object message, String toChannel) {
        byte[] bytes = getSerializer().serialize(message);
        Message onsMessage = new Message(aliyunmqConfig.getBroadcastChannelPrefix() + toChannel, "*", bytes);
        getProducer().send(onsMessage);
    }


    private Producer getProducer() {
        if (producer == null) {
            synchronized (this) {
                if (producer == null) {
                    createProducer();
                }
            }
        }
        return producer;
    }


    private void createProducer() {
        Properties properties = createProperties();
        producer = ONSFactory.createProducer(properties);
        producer.start();
    }


    private Properties createProperties() {


        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, aliyunmqConfig.getAccessKey());//AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, aliyunmqConfig.getSecretKey());//SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.ProducerId, aliyunmqConfig.getProducerId());//您在控制台创建的Producer ID
        properties.put(PropertyKeyConst.ONSAddr, aliyunmqConfig.getAddr());
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, aliyunmqConfig.getSendMsgTimeoutMillis());//设置发送超时时间，单位毫秒
        return properties;
    }
}
