package io.jboot.components.mq.redismq;

import io.jboot.components.mq.MessageContext;
import io.jboot.components.mq.Jbootmq;

public class RedismqMessageContext extends MessageContext {


    public RedismqMessageContext(Jbootmq mq) {
        super(mq);
    }

}
