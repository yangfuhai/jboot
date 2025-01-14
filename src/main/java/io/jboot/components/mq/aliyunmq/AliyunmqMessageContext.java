package io.jboot.components.mq.aliyunmq;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import io.jboot.components.mq.MessageContext;
import io.jboot.components.mq.Jbootmq;

public class AliyunmqMessageContext extends MessageContext {

    final Message originalMessage;
    final ConsumeContext context;
    private Action returnAction = Action.CommitMessage;

    public AliyunmqMessageContext(Jbootmq mq, Message originalMessage, ConsumeContext context) {
        super(mq);
        this.originalMessage = originalMessage;
        this.context = context;
    }

    public Message getOriginalMessage() {
        return originalMessage;
    }

    public ConsumeContext getContext() {
        return context;
    }

    public Action getReturnAction() {
        return returnAction;
    }

    public void setReturnAction(Action returnAction) {
        this.returnAction = returnAction;
    }
}
