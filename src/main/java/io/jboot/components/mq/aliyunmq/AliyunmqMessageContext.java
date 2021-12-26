package io.jboot.components.mq.aliyunmq;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import io.jboot.components.mq.MessageContext;
import io.jboot.components.mq.Jbootmq;

public class AliyunmqMessageContext extends MessageContext {

    final Message orginalMessage;
    final ConsumeContext context;
    private Action returnAction = Action.CommitMessage;

    public AliyunmqMessageContext(Jbootmq mq, Message orginalMessage, ConsumeContext context) {
        super(mq);
        this.orginalMessage = orginalMessage;
        this.context = context;
    }

    public Message getOrginalMessage() {
        return orginalMessage;
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
