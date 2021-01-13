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
package io.jboot.components.mq.rabbitmq;

import com.rabbitmq.client.*;
import io.jboot.Jboot;
import io.jboot.app.JbootApplicationConfig;
import io.jboot.components.mq.Jbootmq;
import io.jboot.components.mq.JbootmqBase;
import io.jboot.exception.JbootException;
import io.jboot.utils.StrUtil;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * doc : http://www.rabbitmq.com/api-guide.html
 */
public class JbootRabbitmqImpl extends JbootmqBase implements Jbootmq {


    private Connection connection;
    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private JbootmqRabbitmqConfig rabbitmqConfig;
    private JbootApplicationConfig appConfig;

    public JbootRabbitmqImpl() {
        super();
        rabbitmqConfig = Jboot.config(JbootmqRabbitmqConfig.class);
        appConfig = Jboot.config(JbootApplicationConfig.class);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqConfig.getHost());
        factory.setPort(rabbitmqConfig.getPort());

        if (StrUtil.isNotBlank(rabbitmqConfig.getVirtualHost())) {
            factory.setVirtualHost(rabbitmqConfig.getVirtualHost());
        }
        if (StrUtil.isNotBlank(rabbitmqConfig.getUsername())) {
            factory.setUsername(rabbitmqConfig.getUsername());
        }

        if (StrUtil.isNotBlank(rabbitmqConfig.getPassword())) {
            factory.setPassword(rabbitmqConfig.getPassword());
        }

        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            throw new JbootException("can not connection rabbitmq server", e);
        }
    }

    @Override
    protected void onStartListening() {
        for (String toChannel : channels) {

            //广播通道
            Channel broadcastChannel = getChannel(toChannel, false);
            bindChannel(broadcastChannel, buildBroadcastChannelName(toChannel), toChannel);


            //队列通道
            final Channel queueChannel = getChannel(toChannel, true);
            bindChannel(queueChannel, toChannel, toChannel);
        }
    }


    private void bindChannel(Channel channel, String name, String orginaChannelName) {
        if (channel != null) {
            try {
                channel.basicConsume(name, true, new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        Object o = getSerializer().deserialize(body);
                        notifyListeners(orginaChannelName, o);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private Channel getChannel(String toChannel, boolean queueMode) {

        Channel channel = channelMap.get(toChannel + queueMode);
        if (channel == null) {
            try {
                channel = connection.createChannel();

                //队列模式，值需要创建定义 队列就可以了，不需要定义交换机
                if (queueMode) {
                    channel.queueDeclare(toChannel, true, false, false, null);
                }

                //广播模式，需要定义交换机，发送者直接把消息发送到交换机里
                else {
                    channel.queueDeclare(buildBroadcastChannelName(toChannel), false, false, true, null);

                    //定义交换机
                    channel.exchangeDeclare(toChannel, BuiltinExchangeType.FANOUT, true);


                    channel.queueBind(buildBroadcastChannelName(toChannel), toChannel, "");
                }

            } catch (IOException e) {
                throw new JbootException("Can not create rabbit mq channel.", e);
            }

            if (channel != null) {
                channelMap.put(toChannel, channel);
            }
        }

        return channel;
    }

    private synchronized String buildBroadcastChannelName(String channel) {
        String prefix = StrUtil.isNotBlank(rabbitmqConfig.getBroadcastQueueNamePrefix()) ? rabbitmqConfig.getBroadcastQueueNamePrefix() : appConfig.getName();
        return prefix + "-" + channel;
    }


    @Override
    public void enqueue(Object message, String toChannel) {
        Channel channel = getChannel(toChannel, true);
        try {
            byte[] bytes = getSerializer().serialize(message);
            channel.basicPublish("", toChannel, MessageProperties.BASIC, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void publish(Object message, String toChannel) {
        Channel channel = getChannel(toChannel, false);
        try {
            byte[] bytes = getSerializer().serialize(message);
            channel.basicPublish(toChannel, "", MessageProperties.BASIC, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
