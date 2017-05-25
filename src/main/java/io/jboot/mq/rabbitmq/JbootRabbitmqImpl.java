/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.mq.rabbitmq;

import com.rabbitmq.client.*;
import io.jboot.Jboot;
import io.jboot.cache.ehredis.JbootEhredisCacheImpl;
import io.jboot.exception.JbootException;
import io.jboot.mq.Jbootmq;
import io.jboot.mq.JbootmqBase;
import io.jboot.utils.StringUtils;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;

/**
 * doc : http://www.rabbitmq.com/api-guide.html
 */
public class JbootRabbitmqImpl extends JbootmqBase implements Jbootmq {


    Connection connection;
    static FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

    public JbootRabbitmqImpl() {

        JbootmqRabbitmqConfig config = Jboot.config(JbootmqRabbitmqConfig.class);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.getHost());
        factory.setPort(config.getPortAsInt());

        if (StringUtils.isNotBlank(config.getVirtualHost())) {
            factory.setVirtualHost(config.getVirtualHost());
        }
        if (StringUtils.isNotBlank(config.getUsername())) {
            factory.setUsername(config.getUsername());
        }

        if (StringUtils.isNotBlank(config.getPassword())) {
            factory.setPassword(config.getPassword());
        }

        String channelString = config.getChannel();
        if (StringUtils.isBlank(channelString)) {
            throw new JbootException("jboot.mq.rabbitmq.channel config cannot empty in jboot.properties");
        }

        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            throw new JbootException("can not connection rabbitmq server", e);
        }

        String[] channels = channelString.split(",");
        for (String toChannel : channels) {
            registerListner(getChannel(toChannel));
        }

        /**
         * 阿里云需要提前注册缓存通知使用的通道
         */
        registerListner(getChannel(JbootEhredisCacheImpl.DEFAULT_NOTIFY_CHANNEL));
    }

    private void registerListner(final Channel channel) {
        if (channel == null) {
            return;
        }
        try {
            channel.basicConsume("", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    Object o = fst.asObject(body);
                    notifyListeners(envelope.getExchange(), o);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Channel getChannel(String toChannel) {
        Channel channel = null;
        try {
            channel = connection.createChannel();
            channel.exchangeDeclare(toChannel, BuiltinExchangeType.FANOUT);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, toChannel, "");
        } catch (IOException e) {
            throw new JbootException("can not createChannel", e);
        }

        return channel;
    }

    @Override
    public void publish(Object message, String toChannel) {

        Channel channel = getChannel(toChannel);

        if (channel == null) {
            return;
        }

        byte[] bytes = fst.asByteArray(message);
        try {
            channel.basicPublish(toChannel, "", MessageProperties.BASIC, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
