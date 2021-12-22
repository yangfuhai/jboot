package io.jboot.components.mq;

public class JbootMqMessageInfo {

    final Jbootmq mq;

    public JbootMqMessageInfo(Jbootmq mq) {
        this.mq = mq;
    }

    public Jbootmq getMq() {
        return mq;
    }
}
