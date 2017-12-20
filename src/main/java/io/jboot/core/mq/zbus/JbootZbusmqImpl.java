/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.mq.zbus;

import com.google.common.collect.Maps;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.core.mq.Jbootmq;
import io.jboot.core.mq.JbootmqBase;
import io.jboot.utils.StringUtils;
import io.zbus.mq.*;

import java.io.IOException;
import java.util.Map;


public class JbootZbusmqImpl extends JbootmqBase implements Jbootmq, MessageHandler {

    private static final Log LOG = Log.getLog(JbootZbusmqImpl.class);
    private Broker broker;

    public JbootZbusmqImpl() {

        initChannels();

        JbootZbusmqConfig zbusmqConfig = Jboot.config(JbootZbusmqConfig.class);
        broker = new Broker(zbusmqConfig.getBroker());

        for (String channel : channels) {
            ConsumerConfig config = new ConsumerConfig(broker);
            config.setTopic(channel);
            config.setMessageHandler(this);
            ConsumeGroup group = ConsumeGroup.createTempBroadcastGroup();
            config.setConsumeGroup(group);
            Consumer consumer = new Consumer(config);


            try {
                consumer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String queueString = zbusmqConfig.getQueue();
        if (StringUtils.isBlank(queueString)) {
            return;
        }

        String[] queues = queueString.split(",");
        for (String channel : queues) {
            ConsumerConfig config = new ConsumerConfig(broker);
            config.setTopic(channel);
            config.setMessageHandler(this);
            Consumer consumer = new Consumer(config);
            try {
                consumer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void enqueue(Object message, String toChannel) {
        publish(message, toChannel);
    }


    @Override
    public void publish(Object message, String toChannel) {
        ensureChannelExist(toChannel);
        Producer producer = getProducer(toChannel);

        Message msg = new Message();
        msg.setTopic(toChannel);
        msg.setBody(Jboot.me().getSerializer().serialize(message));

        try {
            producer.publish(msg);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }


    private Map<String, Producer> producerMap = Maps.newConcurrentMap();

    public Producer getProducer(String toChannel) {
        Producer producer = producerMap.get(toChannel);
        if (producer == null) {
            producer = new Producer(broker);
            try {
                producer.declareTopic(toChannel);
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
            producerMap.put(toChannel, producer);
        }
        return producer;
    }

    @Override
    public void handle(Message message, MqClient mqClient) throws IOException {
        notifyListeners(message.getTopic(), Jboot.me().getSerializer().deserialize(message.getBody()));
    }
}
