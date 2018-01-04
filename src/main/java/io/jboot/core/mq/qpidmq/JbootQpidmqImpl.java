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
package io.jboot.core.mq.qpidmq;

import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.core.mq.Jbootmq;
import io.jboot.core.mq.JbootmqBase;
import io.jboot.exception.JbootException;
import io.jboot.utils.StringUtils;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.jms.Connection;

import javax.jms.*;

public class JbootQpidmqImpl extends JbootmqBase implements Jbootmq {

    private static final Log LOG = Log.getLog(JbootQpidmqImpl.class);

    private Connection connection = null;

    public JbootQpidmqImpl() {
        initChannels();

        try {
            String url = getConnectionUrl();
            connection = new AMQConnection(url);

            startReceiveMsgThread();

        } catch (Exception e) {
            throw new JbootException("qpidmq server error", e);
        }
    }

    @Override
    public void enqueue(Object message, String toChannel) {
        ensureChannelExist(toChannel);

        String addr = getQueueAddr(toChannel);
        sendMsg(addr, message);
    }

    @Override
    public void publish(Object message, String toChannel) {
        ensureChannelExist(toChannel);

        String addr = getTopicAddr(toChannel);
        sendMsg(addr, message);
    }

    public void  sendMsg(String addr, Object message) {
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = new AMQAnyDestination(addr.toString());
            MessageProducer producer = session.createProducer(destination);

            byte[] data = Jboot.me().getSerializer().serialize(message);
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.setIntProperty("data-len", data.length);
            bytesMessage.writeBytes(data);

            producer.setTimeToLive(30000);
            producer.send(bytesMessage);

        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }

    private String getConnectionUrl() {
        JbootQpidmqConfig qpidConfig = Jboot.config(JbootQpidmqConfig.class);

        StringBuffer url = new StringBuffer();
        url.append("amqp://");
        url.append(qpidConfig.getUsername());
        url.append(":");
        url.append(qpidConfig.getPassword());
        url.append("@");
        url.append("/");
        url.append(qpidConfig.getVirtualHost());
        url.append("?failover='roundrobin'");
        url.append("&brokerlist='");

        String host = qpidConfig.getHost();
        String[] hosts = host.split(",");
        for (String h : hosts) {
            if (StringUtils.isBlank(h)) {
                continue;
            }
            url.append("tcp://" + h + ";");
        }

        url.append("'");

        return url.toString();
    }

    private String getQueueAddr(String channel) {
        StringBuffer addr = new StringBuffer();
        addr.append("ADDR:");
        addr.append(channel);
        addr.append(";{create:always}");

        return addr.toString();
    }

    private String getTopicAddr(String channel) {
        StringBuffer addr = new StringBuffer();
        addr.append("ADDR:amq.topic/");
        addr.append(channel);

        return addr.toString();
    }

    private void startReceiveMsgThread() throws Exception{
        connection.start();

        for (String channel : this.channels) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            String queueAddr = getQueueAddr(channel);
            Destination queue = new AMQAnyDestination(queueAddr);
            MessageConsumer queueConsumer = session.createConsumer(queue);
            new Thread(new ReceiveMsgThread(queueConsumer, channel)).start();

            String topicAddr = getTopicAddr(channel);
            Destination topic = new AMQAnyDestination(topicAddr);
            MessageConsumer topicConsumer = session.createConsumer(topic);
            new Thread(new ReceiveMsgThread(topicConsumer, channel)).start();
        }
    }

    private class ReceiveMsgThread implements Runnable {
        private MessageConsumer consumer;
        private String channel;

        public ReceiveMsgThread(MessageConsumer consumer, String channel) {
            this.consumer = consumer;
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                for (; ; ) {
                    BytesMessage bytesMessage = (BytesMessage) consumer.receive();
                    if (bytesMessage == null) {
                        continue;
                    }

                    int dataLen = bytesMessage.getIntProperty("data-len");
                    byte data[] = new byte[dataLen];
                    if (dataLen != bytesMessage.readBytes(data)) {
                        continue;
                    }

                    Object message = Jboot.me().getSerializer().deserialize(data);
                    if (message != null) {
                        System.out.println("channel:" + channel + ",message:" + message.toString());
                        notifyListeners(channel, message);
                    }
                }
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
        }
    }

    public static void main(String args[]) {
        Jboot.me().start();
        JbootQpidmqImpl jbootmq = new JbootQpidmqImpl();
        jbootmq.enqueue("aaa", "qpid");
        jbootmq.publish("bbb", "qpid");
        jbootmq.publish("cccc", "qpid");
    }
}
