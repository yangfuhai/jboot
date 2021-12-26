package io.jboot.components.mq.qpidmq;

import io.jboot.components.mq.MessageContext;
import io.jboot.components.mq.Jbootmq;

import javax.jms.Message;

public class QpidmqMessageContext extends MessageContext {


    final Message orignalMessage;

    public QpidmqMessageContext(Jbootmq mq, Message orignalMessage) {
        super(mq);
        this.orignalMessage = orignalMessage;
    }
}
