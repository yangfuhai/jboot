/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.mq.aliyunmq;

import com.aliyun.openservices.ons.api.*;
import io.jboot.Jboot;
import io.jboot.core.mq.Jbootmq;
import io.jboot.core.mq.JbootmqBase;
import io.jboot.kits.ArrayKits;

import java.util.Properties;


public class JbootAliyunmqImpl extends JbootmqBase implements Jbootmq, MessageListener {

    private Producer producer;
    private Consumer consumer;

    public JbootAliyunmqImpl() {
        super();

        JbootAliyunmqConfig aliyunmqConfig = Jboot.config(JbootAliyunmqConfig.class);

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, aliyunmqConfig.getAccessKey());//AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, aliyunmqConfig.getSecretKey());//SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.ProducerId, aliyunmqConfig.getProducerId());//您在控制台创建的Producer ID
        properties.put(PropertyKeyConst.ONSAddr, aliyunmqConfig.getAddr());
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, aliyunmqConfig.getSendMsgTimeoutMillis());//设置发送超时时间，单位毫秒

        producer = ONSFactory.createProducer(properties);
        producer.start();

        if (ArrayKits.isNotEmpty(this.channels)) {
            initChannelSubscribe(properties);
        }


    }

    private void initChannelSubscribe(Properties properties) {
        consumer = ONSFactory.createConsumer(properties);
        for (String c : channels) {
            consumer.subscribe(c, "*", this);
        }
        consumer.start();
    }

    @Override
    public void enqueue(Object message, String toChannel) {
        throw new RuntimeException("not finished!");
    }

    @Override
    public void publish(Object message, String toChannel) {
        byte[] bytes = Jboot.getSerializer().serialize(message);
        Message onsMessage = new Message(toChannel, "*", bytes);
        producer.send(onsMessage);
    }

    @Override
    public Action consume(Message message, ConsumeContext context) {
        byte[] bytes = message.getBody();
        Object object = Jboot.getSerializer().deserialize(bytes);
        notifyListeners(message.getTopic(), object);
        return Action.CommitMessage;
    }
}
