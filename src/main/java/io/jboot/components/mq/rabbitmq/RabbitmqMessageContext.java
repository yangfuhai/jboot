package io.jboot.components.mq.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import io.jboot.components.mq.MessageContext;
import io.jboot.components.mq.Jbootmq;

public class RabbitmqMessageContext extends MessageContext {

    final Channel channel;
    final String consumerTag;
    final Envelope envelope;
    final AMQP.BasicProperties properties;

    public RabbitmqMessageContext(Jbootmq mq, Channel channel, String consumerTag, Envelope envelope, AMQP.BasicProperties properties) {
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
