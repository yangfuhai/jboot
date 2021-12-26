package io.jboot.components.mq;

public class MessageContext {

    final Jbootmq mq;

    public MessageContext(Jbootmq mq) {
        this.mq = mq;
    }

    public Jbootmq getMq() {
        return mq;
    }
}
