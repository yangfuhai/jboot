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
import io.jboot.kits.ArrayUtils;
import io.jboot.kits.StrUtils;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.jms.Connection;

import javax.jms.*;

/**
 * @author 徐海峰 （27533892@qq.com）
 * @version V1.0
 * @Package io.jboot.core.mq.qpid
 */
public class JbootQpidmqImpl extends JbootmqBase implements Jbootmq {

    private static final Log LOG = Log.getLog(JbootQpidmqImpl.class);

    private Connection connection = null;
    private boolean serializerEnable = true;

    public JbootQpidmqImpl() {
        super();

        JbootQpidmqConfig qpidConfig = Jboot.config(JbootQpidmqConfig.class);
        serializerEnable = qpidConfig.isSerializerEnable();

        try {
            String url = getConnectionUrl();
            connection = new AMQConnection(url);
            connection.start();

            startReceiveMsgThread();

        } catch (Exception e) {
            throw new JbootException("can not connection qpidmq server", e);
        }
    }

    @Override
    public void enqueue(Object message, String toChannel) {
        String addr = getQueueAddr(toChannel);
        sendMsg(addr, message);
    }

    @Override
    public void publish(Object message, String toChannel) {
        String addr = getTopicAddr(toChannel);
        sendMsg(addr, message);
    }

    public void sendMsg(String addr, Object message) {
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = new AMQAnyDestination(addr.toString());
            MessageProducer producer = session.createProducer(destination);
            producer.setTimeToLive(30000);

            Message m = null;
            if (!serializerEnable) {
                m = session.createTextMessage((String) message);
            } else {
                byte[] data = Jboot.me().getSerializer().serialize(message);
                m = session.createBytesMessage();
                m.setIntProperty("data-len", data.length);
                ((BytesMessage) m).writeBytes(data);
            }

            producer.send(m);

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
            if (StrUtils.isBlank(h)) {
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

    private void startReceiveMsgThread() throws Exception {
        if (ArrayUtils.isNullOrEmpty(this.channels)) {
            return;
        }

        for (String channel : this.channels) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            String queueAddr = getQueueAddr(channel);
            Destination queue = new AMQAnyDestination(queueAddr);
            MessageConsumer queueConsumer = session.createConsumer(queue);
            new Thread(new ReceiveMsgThread(queueConsumer, channel, serializerEnable)).start();

            String topicAddr = getTopicAddr(channel);
            Destination topic = new AMQAnyDestination(topicAddr);
            MessageConsumer topicConsumer = session.createConsumer(topic);
            new Thread(new ReceiveMsgThread(topicConsumer, channel, serializerEnable)).start();
        }
    }

    private class ReceiveMsgThread implements Runnable {
        private MessageConsumer consumer;
        private String channel;
        private boolean serializerEnable;

        public ReceiveMsgThread(MessageConsumer consumer, String channel, boolean serializerEnable) {
            this.consumer = consumer;
            this.channel = channel;
            this.serializerEnable = serializerEnable;
        }

        @Override
        public void run() {
            try {
                for (; ; ) {
                    Message message = consumer.receive();
                    if (message == null) {
                        continue;
                    }

                    Object object = null;
                    if (!serializerEnable) {
                        TextMessage textMessage = (TextMessage) message;
                        object = textMessage.getText();
                    } else {
                        BytesMessage bytesMessage = (BytesMessage) message;
                        int dataLen = bytesMessage.getIntProperty("data-len");
                        byte data[] = new byte[dataLen];
                        if (dataLen != bytesMessage.readBytes(data)) {
                            continue;
                        }
                        object = Jboot.me().getSerializer().deserialize(data);
                    }

                    if (object != null) {
                        notifyListeners(channel, object);
                    }
                }
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
        }
    }
}
