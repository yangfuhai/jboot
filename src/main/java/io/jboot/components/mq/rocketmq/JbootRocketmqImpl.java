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
package io.jboot.components.mq.rocketmq;

import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.components.mq.Jbootmq;
import io.jboot.components.mq.JbootmqBase;
import io.jboot.components.mq.JbootmqConfig;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ConfigUtil;
import io.jboot.utils.StrUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.Map;


public class JbootRocketmqImpl extends JbootmqBase implements Jbootmq {

    private static final Log LOG = Log.getLog(JbootRocketmqImpl.class);
    private JbootRocketmqConfig rocketmqConfig;
    private MQProducer mqProducer;

    public JbootRocketmqImpl(JbootmqConfig config) {
        super(config);

        String typeName = config.getTypeName();
        if (StrUtil.isNotBlank(typeName)) {
            Map<String, JbootRocketmqConfig> configModels = ConfigUtil.getConfigModels(JbootRocketmqConfig.class);
            if (!configModels.containsKey(typeName)) {
                throw new JbootIllegalConfigException("Please config \"jboot.mq.rocket." + typeName + ".namesrvAddr\" in your jboot.properties.");
            }
            rocketmqConfig = configModels.get(typeName);
        } else {
            rocketmqConfig = Jboot.config(JbootRocketmqConfig.class);
        }
    }

    @Override
    protected void onStartListening() {
        try {
            startQueueConsumer();
            startBroadcastConsumer();
        } catch (MQClientException e) {
            LOG.error(e.toString(), e);
        }
    }


    public void startQueueConsumer() throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketmqConfig.getConsumerGroup());

        // 设置NameServer的地址
        consumer.setNamesrvAddr(rocketmqConfig.getNamesrvAddr());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        if (StrUtil.isNotBlank(rocketmqConfig.getNamespace())) {
            consumer.setNamespace(rocketmqConfig.getNamespace());
        }

        if (rocketmqConfig.getConsumeMessageBatchMaxSize() != null) {
            consumer.setConsumeMessageBatchMaxSize(rocketmqConfig.getConsumeMessageBatchMaxSize());
        }

        for (String channel : channels) {
            consumer.subscribe(channel, rocketmqConfig.getSubscribeSubExpression());
        }

        // 注册回调实现类来处理从broker拉取回来的消息
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            RokectmqMessageContext msgContext = new RokectmqMessageContext(this, msgs, context);
            if (msgs != null && !msgs.isEmpty()) {
                for (MessageExt messageExt : msgs) {
                    notifyListeners(messageExt.getTopic(), getSerializer().deserialize(messageExt.getBody()), msgContext);
                }
            }

            return msgContext.getReturnStatus();
        });


        consumer.start();

    }


    public void startBroadcastConsumer() throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketmqConfig.getBroadcastChannelPrefix() + rocketmqConfig.getConsumerGroup());

        // 设置NameServer的地址
        consumer.setNamesrvAddr(rocketmqConfig.getNamesrvAddr());

        if (StrUtil.isNotBlank(rocketmqConfig.getNamespace())) {
            consumer.setNamespace(rocketmqConfig.getNamespace());
        }

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.BROADCASTING);

        if (rocketmqConfig.getConsumeMessageBatchMaxSize() != null) {
            consumer.setConsumeMessageBatchMaxSize(rocketmqConfig.getConsumeMessageBatchMaxSize());
        }

        for (String channel : channels) {
            consumer.subscribe(rocketmqConfig.getBroadcastChannelPrefix() + channel, rocketmqConfig.getSubscribeSubExpression());
        }

        final int len = rocketmqConfig.getBroadcastChannelPrefix().length();
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            RokectmqMessageContext rokectMqMessageInfo = new RokectmqMessageContext(this, msgs, context);
            if (msgs != null && !msgs.isEmpty()) {
                for (MessageExt messageExt : msgs) {
                    String topic = messageExt.getTopic();
                    notifyListeners(topic.substring(len), getSerializer().deserialize(messageExt.getBody()), rokectMqMessageInfo);
                }
            }
            return rokectMqMessageInfo.getReturnStatus();
        });

        consumer.start();
    }


    @Override
    public void enqueue(Object message, String toChannel) {
        doSendMessage(message, toChannel);
    }


    @Override
    public void publish(Object message, String toChannel) {
        doSendMessage(message, rocketmqConfig.getBroadcastChannelPrefix() + toChannel);
    }


    public void doSendMessage(Object message, String topic) {
        try {
            Message sendMsg = null;
            if (message instanceof Message) {
                sendMsg = (Message) message;
            } else {
                sendMsg = new Message(topic, getSerializer().serialize(message));
            }

            SendResult result = getMQProducer().send(sendMsg);
            // if (result.getSendStatus() != SendStatus.SEND_OK) {
            // 只要不等于 null 就是发送成功
            if (result == null) {
                LOG.warn("Rockect mq send message fail!!!");
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }


    public MQProducer getMQProducer() throws MQClientException {
        if (mqProducer == null) {
            synchronized (this) {
                if (mqProducer == null) {
                    createMqProducer();
                }
            }
        }
        return mqProducer;
    }


    public void createMqProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(rocketmqConfig.getProducerGroup());
        producer.setNamesrvAddr(rocketmqConfig.getNamesrvAddr());

        if (StrUtil.isNotBlank(rocketmqConfig.getNamespace())) {
            producer.setNamespace(rocketmqConfig.getNamespace());
        }

        if (StrUtil.isNotBlank(rocketmqConfig.getInstanceName())) {
            producer.setInstanceName(rocketmqConfig.getInstanceName());
        }

        if (StrUtil.isNotBlank(rocketmqConfig.getClientIP())) {
            producer.setClientIP(rocketmqConfig.getClientIP());
        }

        if (StrUtil.isNotBlank(rocketmqConfig.getCreateTopicKey())) {
            producer.setCreateTopicKey(rocketmqConfig.getCreateTopicKey());
        }

        if (rocketmqConfig.getUseTLS() != null) {
            producer.setUseTLS(rocketmqConfig.getUseTLS());
        }

        if (rocketmqConfig.getSendLatencyFaultEnable() != null) {
            producer.setSendLatencyFaultEnable(rocketmqConfig.getSendLatencyFaultEnable());
        }

        if (rocketmqConfig.getSendMessageWithVIPChannel() != null) {
            producer.setSendMessageWithVIPChannel(rocketmqConfig.getSendMessageWithVIPChannel());
        }

        if (rocketmqConfig.getSendMsgTimeout() != null) {
            producer.setSendMsgTimeout(rocketmqConfig.getSendMsgTimeout());
        }

        if (rocketmqConfig.getRetryAnotherBrokerWhenNotStoreOK() != null) {
            producer.setRetryAnotherBrokerWhenNotStoreOK(rocketmqConfig.getRetryAnotherBrokerWhenNotStoreOK());
        }

        if (rocketmqConfig.getRetryTimesWhenSendAsyncFailed() != null) {
            producer.setRetryTimesWhenSendAsyncFailed(rocketmqConfig.getRetryTimesWhenSendAsyncFailed());
        }

        if (rocketmqConfig.getRetryTimesWhenSendFailed() != null) {
            producer.setRetryTimesWhenSendFailed(rocketmqConfig.getRetryTimesWhenSendFailed());
        }

        mqProducer = producer;
        producer.start();
    }
}


