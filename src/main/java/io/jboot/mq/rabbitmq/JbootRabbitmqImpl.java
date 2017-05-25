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
import io.jboot.exception.JbootException;
import io.jboot.mq.Jbootmq;
import io.jboot.mq.JbootmqBase;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JbootRabbitmqImpl extends JbootmqBase implements Jbootmq {


    Connection connection;
    static FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

    public JbootRabbitmqImpl() {

        JbootmqRabbitmqConfig config = Jboot.config(JbootmqRabbitmqConfig.class);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.getHost());
        factory.setVirtualHost(config.getVirtualHost());
        factory.setPort(config.getPortAsInt());
        factory.setUsername(config.getUsername());
        factory.setPassword(config.getPassword());


        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            throw new JbootException("can not connection rabbitmq server", e);
        }

    }

    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private Channel getChannel(String toChannel) {
        Channel channel = channelMap.get(toChannel);
        if (channel == null) {
            try {
                channel = connection.createChannel();
                channel.queueDeclare(toChannel, false, false, false, null);
                channel.basicConsume(toChannel, new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        Object o = fst.asObject(body);
                        notifyListeners(toChannel, o);
                    }
                });
                channelMap.put(toChannel, channel);
            } catch (IOException e) {
                throw new JbootException("can not createChannel", e);
            }
        }

        return channel;
    }

    @Override
    public void publish(Object message, String toChannel) {
        byte[] bytes = fst.asByteArray(message);
        try {
            getChannel(toChannel).basicPublish("", toChannel, null, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
