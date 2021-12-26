package io.jboot.components.mq.local;

import io.jboot.components.mq.MessageContext;
import io.jboot.components.mq.Jbootmq;

public class LocalmqMessageContext extends MessageContext {


    public LocalmqMessageContext(Jbootmq mq) {
        super(mq);
    }

}
