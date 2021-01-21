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

import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.components.mq.Jbootmq;
import io.jboot.components.mq.JbootmqBase;
import io.jboot.utils.StrUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;


public class JbootRocketmqImpl extends JbootmqBase implements Jbootmq {

    private static final Log LOG = Log.getLog(JbootRocketmqImpl.class);
    private JbootRocketmqConfig rocketmqConfig;
    private MQProducer mqProducer;

    public JbootRocketmqImpl() {
        super();
        rocketmqConfig = Jboot.config(JbootRocketmqConfig.class);
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


    private void startQueueConsumer() throws MQClientException {
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
            consumer.subscribe(channel, "*");
        }

        // 注册回调实现类来处理从broker拉取回来的消息
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            if (msgs != null) {
                for (MessageExt messageExt : msgs) {
                    notifyListeners(messageExt.getTopic(), getSerializer().deserialize(messageExt.getBody()));
                }
            }
            // 标记该消息已经被成功消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });


        consumer.start();

    }


    private void startBroadcastConsumer() throws MQClientException {
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
            consumer.subscribe(rocketmqConfig.getBroadcastChannelPrefix() + channel, "*");
        }

        final int len = rocketmqConfig.getBroadcastChannelPrefix().length();
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            if (msgs != null) {
                for (MessageExt messageExt : msgs) {
                    String topic = messageExt.getTopic();
                    notifyListeners(topic.substring(len), getSerializer().deserialize(messageExt.getBody()));
                }
            }
            // 标记该消息已经被成功消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
    }


    @Override
    public void enqueue(Object message, String toChannel) {
        trySendMessage(message, toChannel, 1);
    }


    @Override
    public void publish(Object message, String toChannel) {
        trySendMessage(message, rocketmqConfig.getBroadcastChannelPrefix() + toChannel, 1);
    }


    private void trySendMessage(Object message, String topic, int tryTimes) {
        if (tryTimes < 3) {
            try {
                Message rocketMessage = new Message(topic, getSerializer().serialize(message));
                SendResult result = getMQProducer().send(rocketMessage);
                // if (result.getSendStatus() != SendStatus.SEND_OK) {
                // 只要不等于 null 就是发送成功
                if (result == null) {
                    trySendMessage(message, topic, ++tryTimes);
                }
            } catch (Exception e) {
                trySendMessage(message, topic, ++tryTimes);
                LOG.error(e.toString(), e);
            }
        } else {
            LOG.error("Rocketmq publish not success!");
        }
    }


    private MQProducer getMQProducer() throws MQClientException {
        if (mqProducer == null) {
            synchronized (this) {
                if (mqProducer == null) {
                    createMqProducer();
                }
            }
        }
        return mqProducer;
    }


    private void createMqProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(rocketmqConfig.getProducerGroup());
        producer.setNamesrvAddr(rocketmqConfig.getNamesrvAddr());

        if (StrUtil.isNotBlank(rocketmqConfig.getNamespace())) {
            producer.setNamespace(rocketmqConfig.getNamespace());
        }

        producer.start();
        mqProducer = producer;
    }
}


