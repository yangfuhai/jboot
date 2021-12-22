package io.jboot.components.mq.rocketmq;

import io.jboot.components.mq.JbootMqMessageInfo;
import io.jboot.components.mq.Jbootmq;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class RokectmqMessageInfo extends JbootMqMessageInfo {

    private ConsumeConcurrentlyStatus returnStatus = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    private final List<MessageExt> msgs;
    private final ConsumeConcurrentlyContext context;

    public RokectmqMessageInfo(Jbootmq jbootmq, List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        super(jbootmq);
        this.msgs = msgs;
        this.context = context;
    }

    public ConsumeConcurrentlyStatus getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(ConsumeConcurrentlyStatus returnStatus) {
        this.returnStatus = returnStatus;
    }

    public List<MessageExt> getMsgs() {
        return msgs;
    }

    public ConsumeConcurrentlyContext getContext() {
        return context;
    }
}
