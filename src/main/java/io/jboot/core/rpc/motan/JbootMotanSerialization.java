package io.jboot.core.rpc.motan;

import com.weibo.api.motan.codec.Serialization;
import com.weibo.api.motan.core.extension.SpiMeta;
import io.jboot.Jboot;

import java.io.IOException;


@SpiMeta(name = "jboot")
public class JbootMotanSerialization implements Serialization {
    @Override
    public byte[] serialize(Object obj) throws IOException {
        return Jboot.getSerializer().serialize(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
        return (T) Jboot.getSerializer().deserialize(bytes);
    }
}
