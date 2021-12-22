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
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.components.mq.Jbootmq;
import io.jboot.components.mq.JbootmqBase;
import io.jboot.components.mq.JbootmqConfig;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ConfigUtil;
import io.jboot.utils.StrUtil;

import java.util.Map;
import java.util.Properties;


public class JbootAliyunmqImpl extends JbootmqBase implements Jbootmq {
    private static final Log LOG = Log.getLog(JbootAliyunmqImpl.class);

    private Producer producer;
    private Consumer consumer;
    private JbootAliyunmqConfig aliyunmqConfig;

    public JbootAliyunmqImpl(JbootmqConfig config) {
        super(config);
        String typeName = config.getTypeName();
        if (StrUtil.isNotBlank(typeName)) {
            Map<String, JbootAliyunmqConfig> configModels = ConfigUtil.getConfigModels(JbootAliyunmqConfig.class);
            if (!configModels.containsKey(typeName)) {
                throw new JbootIllegalConfigException("Please config \"jboot.mq.aliyun." + typeName + ".addr\" in your jboot.properties.");
            }
            aliyunmqConfig = configModels.get(typeName);
        } else {
            aliyunmqConfig = Jboot.config(JbootAliyunmqConfig.class);
        }
    }


    @Override
    protected void onStartListening() {
        startQueueConsumer();
        startBroadCastConsumer();
    }


    public void startQueueConsumer() {
        Properties properties = createProperties();
        consumer = ONSFactory.createConsumer(properties);
        for (String channel : channels) {
            consumer.subscribe(aliyunmqConfig.getBroadcastChannelPrefix() + channel, aliyunmqConfig.getSubscribeSubExpression(), (message, consumeContext) -> {
                AliyunmqMessageInfo aliyunmqMessageInfo = new AliyunmqMessageInfo(this, message, consumeContext);
                notifyListeners(channel, getSerializer().deserialize(message.getBody())
                        , aliyunmqMessageInfo);
                return aliyunmqMessageInfo.getReturnAction();
            });
        }
        consumer.start();
    }


    public void startBroadCastConsumer() {
        Properties properties = createProperties();
        properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.BROADCASTING);
        consumer = ONSFactory.createConsumer(properties);
        for (String channel : channels) {
            consumer.subscribe(channel, aliyunmqConfig.getSubscribeSubExpression(), (message, consumeContext) -> {
                AliyunmqMessageInfo aliyunmqMessageInfo = new AliyunmqMessageInfo(this, message, consumeContext);
                notifyListeners(channel, getSerializer().deserialize(message.getBody())
                        , aliyunmqMessageInfo);
                return aliyunmqMessageInfo.getReturnAction();
            });
        }
        consumer.start();
    }


    @Override
    public void enqueue(Object message, String toChannel) {
        Message sendMsg = null;
        if (message instanceof Message) {
            sendMsg = (Message) message;
        } else {
            byte[] bytes = getSerializer().serialize(message);
            sendMsg = new Message(toChannel, "*", bytes);
        }
        SendResult result = getProducer().send(sendMsg);
        if (result == null) {
            LOG.warn("Rockect mq send message fail!!!");
        }
    }


    @Override
    public void publish(Object message, String toChannel) {
        Message sendMsg = null;
        if (message instanceof Message) {
            sendMsg = (Message) message;
        } else {
            byte[] bytes = getSerializer().serialize(message);
            sendMsg = new Message(aliyunmqConfig.getBroadcastChannelPrefix() + toChannel, "*", bytes);
        }
        SendResult result = getProducer().send(sendMsg);
        if (result == null) {
            LOG.warn("Rockect mq send message fail!!!");
        }
    }


    public Producer getProducer() {
        if (producer == null) {
            synchronized (this) {
                if (producer == null) {
                    createProducer();
                }
            }
        }
        return producer;
    }


    public void createProducer() {
        Properties properties = createProperties();
        producer = ONSFactory.createProducer(properties);
        producer.start();
    }


    public Properties createProperties() {

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, aliyunmqConfig.getAccessKey());//AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, aliyunmqConfig.getSecretKey());//SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.ProducerId, aliyunmqConfig.getProducerId());//您在控制台创建的Producer ID
        properties.put(PropertyKeyConst.NAMESRV_ADDR, aliyunmqConfig.getAddr());
        properties.put(PropertyKeyConst.InstanceName, aliyunmqConfig.getInstanceName());
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, aliyunmqConfig.getSendMsgTimeoutMillis());//设置发送超时时间，单位毫秒


        return properties;
    }
}
