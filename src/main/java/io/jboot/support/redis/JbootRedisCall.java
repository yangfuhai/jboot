package io.jboot.support.redis;

public interface JbootRedisCall {

    <T> T call(JbootRedis redis);

}
