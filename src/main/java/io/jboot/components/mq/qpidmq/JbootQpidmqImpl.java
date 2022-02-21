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
package io.jboot.components.mq.qpidmq;

import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.utils.ConfigUtil;
import io.jboot.components.mq.Jbootmq;
import io.jboot.components.mq.JbootmqBase;
import io.jboot.components.mq.JbootmqConfig;
import io.jboot.exception.JbootException;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.jms.Connection;

import javax.jms.*;
import java.util.Map;

/**
 * @author 徐海峰 （27533892@qq.com）
 * @version V1.0
 */
public class JbootQpidmqImpl extends JbootmqBase implements Jbootmq {

    private static final Log LOG = Log.getLog(JbootQpidmqImpl.class);

    private Connection connection = null;
    private boolean serializerEnable = true;
    private JbootQpidmqConfig qpidConfig = null;

    private Thread queueThread;
    private Thread topicThread;

    public JbootQpidmqImpl(JbootmqConfig config) {
        super(config);

        String typeName = config.getTypeName();
        if (StrUtil.isNotBlank(typeName)) {
            Map<String, JbootQpidmqConfig> configModels = ConfigUtil.getConfigModels(JbootQpidmqConfig.class);
            if (!configModels.containsKey(typeName)) {
                throw new JbootIllegalConfigException("Please config \"jboot.mq.qpid." + typeName + ".host\" in your jboot.properties.");
            }
            qpidConfig = configModels.get(typeName);
        } else {
            qpidConfig = Jboot.config(JbootQpidmqConfig.class);
        }


        serializerEnable = qpidConfig.isSerializerEnable();

        try {
            String url = getConnectionUrl();
            connection = new AMQConnection(url);
            connection.start();

        } catch (Exception e) {
            throw new JbootException("can not connection qpidmq server", e);
        }
    }

    @Override
    protected void onStartListening() {
        try {
            startReceiveMsgThread();
        } catch (Exception e) {
            throw new JbootException(e.toString(), e);
        }
    }

    @Override
    protected void onStopListening() {
        queueThread.interrupt();
        topicThread.interrupt();
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

            Message sendMsg = null;
            if (message instanceof Message) {
                sendMsg = (Message) message;
            } else if (!serializerEnable) {
                sendMsg = session.createTextMessage((String) message);
            } else {
                byte[] data = getSerializer().serialize(message);
                sendMsg = session.createBytesMessage();
                sendMsg.setIntProperty("data-len", data.length);
                ((BytesMessage) sendMsg).writeBytes(data);
            }

            producer.send(sendMsg);

        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }

    public String getConnectionUrl() {
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
            if (StrUtil.isBlank(h)) {
                continue;
            }
            url.append("tcp://" + h + ";");
        }

        url.append("'");

        return url.toString();
    }

    public String getQueueAddr(String channel) {
        StringBuffer addr = new StringBuffer();
        addr.append("ADDR:");
        addr.append(channel);
        addr.append(";{create:always}");

        return addr.toString();
    }

    public String getTopicAddr(String channel) {
        StringBuffer addr = new StringBuffer();
        addr.append("ADDR:amq.topic/");
        addr.append(channel);

        return addr.toString();
    }

    public void startReceiveMsgThread() throws Exception {
        if (ArrayUtil.isNullOrEmpty(this.channels)) {
            return;
        }

        for (String channel : this.channels) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            String queueAddr = getQueueAddr(channel);
            Destination queue = new AMQAnyDestination(queueAddr);
            MessageConsumer queueConsumer = session.createConsumer(queue);
            queueThread = new Thread(new ReceiveMsgThread(queueConsumer, channel, serializerEnable));
            queueThread.start();

            String topicAddr = getTopicAddr(channel);
            Destination topic = new AMQAnyDestination(topicAddr);
            MessageConsumer topicConsumer = session.createConsumer(topic);
            topicThread = new Thread(new ReceiveMsgThread(topicConsumer, channel, serializerEnable));
            topicThread.start();
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
                while (isStarted) {
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
                        byte[] data = new byte[dataLen];
                        if (dataLen != bytesMessage.readBytes(data)) {
                            continue;
                        }
                        object = getSerializer().deserialize(data);
                    }

                    if (object != null) {
                        notifyListeners(channel, object, new QpidmqMessageContext(JbootQpidmqImpl.this, message));
                    }
                }
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
        }
    }
}
