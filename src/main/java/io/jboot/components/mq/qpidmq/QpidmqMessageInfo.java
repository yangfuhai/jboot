package io.jboot.components.mq.qpidmq;

import io.jboot.components.mq.JbootMqMessageInfo;
import io.jboot.components.mq.Jbootmq;

import javax.jms.Message;

public class QpidmqMessageInfo extends JbootMqMessageInfo {


    final Message orignalMessage;

    public QpidmqMessageInfo(Jbootmq mq, Message orignalMessage) {
        super(mq);
        this.orignalMessage = orignalMessage;
    }
}
