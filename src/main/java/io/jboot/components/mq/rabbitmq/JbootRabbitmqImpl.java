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

import com.rabbitmq.client.*;
import io.jboot.Jboot;
import io.jboot.app.JbootApplicationConfig;
import io.jboot.utils.ConfigUtil;
import io.jboot.components.mq.Jbootmq;
import io.jboot.components.mq.JbootmqBase;
import io.jboot.components.mq.JbootmqConfig;
import io.jboot.exception.JbootException;
import io.jboot.exception.JbootIllegalConfigException;
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

    private JbootRabbitmqConfig rabbitmqConfig;
    private JbootApplicationConfig appConfig;


    public JbootRabbitmqImpl(JbootmqConfig config) {
        super(config);

        String typeName = config.getTypeName();
        if (StrUtil.isNotBlank(typeName)) {
            Map<String, JbootRabbitmqConfig> configModels = ConfigUtil.getConfigModels(JbootRabbitmqConfig.class);
            if (!configModels.containsKey(typeName)) {
                throw new JbootIllegalConfigException("Please config \"jboot.mq.rabbitmq." + typeName + ".host\" in your jboot.properties.");
            }
            rabbitmqConfig = configModels.get(typeName);
        } else {
            rabbitmqConfig = Jboot.config(JbootRabbitmqConfig.class);
        }

        try {
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

            connection = factory.newConnection();

        } catch (Exception e) {
            throw new JbootException("Can not connection rabbitmq server", e);
        }
    }


    @Override
    protected void onStartListening() {
        for (String toChannel : channels) {
            if(rabbitmqConfig.useBroadcast()) {
                //广播通道
                Channel broadcastChannel = getChannel(toChannel, false);
                bindChannel(broadcastChannel, buildBroadcastChannelName(toChannel), toChannel);
                System.out.println("广播通道");
            }
            if(rabbitmqConfig.useQueue()) {
                //队列通道
                final Channel queueChannel = getChannel(toChannel, true);
                bindChannel(queueChannel, toChannel, toChannel);
                System.out.println("队列通道");
            }
        }
    }

    @Override
    protected void onStopListening() {
        connection.abort();
    }


    public void bindChannel(Channel channel, String name, String orginaChannelName) {
        if (channel != null) {
            try {
                channel.basicConsume(name, rabbitmqConfig.isAutoAck(), new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        Object o = getSerializer().deserialize(body);
                        notifyListeners(orginaChannelName, o, new RabbitmqMessageContext(JbootRabbitmqImpl.this, channel, consumerTag, envelope, properties));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized Channel getChannel(String toChannel, boolean queueMode) {
        Channel channel = channelMap.get(toChannel + queueMode);
        if (channel == null) {
            try {
                channel = connection.createChannel();

                //队列模式，只需要创建 队列就可以了，不需要定义交换机
                if (queueMode) {
                    channel.queueDeclare(toChannel
                            , rabbitmqConfig.isQueueDeclareDurable()
                            , rabbitmqConfig.isQueueDeclareExclusive()
                            , rabbitmqConfig.isQueueDeclareAutoDelete()
                            , null);
                }

                //广播模式，需要定义交换机，发送者直接把消息发送到交换机里
                else {
                    channel.queueDeclare(buildBroadcastChannelName(toChannel)
                            , rabbitmqConfig.isBroadcastQueueDeclareDurable()
                            , rabbitmqConfig.isBroadcastQueueDeclareExclusive()
                            , rabbitmqConfig.isBroadcastQueueDeclareAutoDelete()
                            , null);


                    BuiltinExchangeType exchangeType = BuiltinExchangeType.FANOUT;
                    for (BuiltinExchangeType type : BuiltinExchangeType.values()) {
                        if (type.getType().equals(rabbitmqConfig.getBroadcastExchangeDeclareExchangeType())) {
                            exchangeType = type;
                        }
                    }
                    channel.exchangeDeclare(toChannel, exchangeType, rabbitmqConfig.isBroadcastExchangeDeclareDurable());
                    channel.queueBind(buildBroadcastChannelName(toChannel), toChannel, rabbitmqConfig.getBroadcastChannelRoutingKey());
                }

            } catch (Exception ex) {
                throw new JbootException("Can not create rabbit mq channel.", ex);
            }

            channelMap.put(toChannel + queueMode, channel);
        }

        return channel;
    }

    public String buildBroadcastChannelName(String channel) {
        return rabbitmqConfig.getBroadcastChannelPrefix() + channel;
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
