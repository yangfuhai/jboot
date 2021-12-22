package io.jboot.components.mq.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import io.jboot.components.mq.JbootMqMessageInfo;
import io.jboot.components.mq.Jbootmq;

public class RabbitmqMessageInfo extends JbootMqMessageInfo {

    final Channel channel;
    final String consumerTag;
    final Envelope envelope;
    final AMQP.BasicProperties properties;

    public RabbitmqMessageInfo(Jbootmq mq, Channel channel, String consumerTag, Envelope envelope, AMQP.BasicProperties properties) {
        super(mq);
        this.channel = channel;
        this.consumerTag = consumerTag;
        this.envelope = envelope;
        this.properties = properties;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getConsumerTag() {
        return consumerTag;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public AMQP.BasicProperties getProperties() {
        return properties;
    }
}
