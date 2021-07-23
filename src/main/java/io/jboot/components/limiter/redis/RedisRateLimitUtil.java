package io.jboot.components.limiter.redis;

import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.support.redis.JbootRedis;
import io.jboot.support.redis.JbootRedisManager;

/**
 * 通过lua脚本来进行限次
 */
public class RedisRateLimitUtil {

    private static final String RATE_LIMIT_SCRIPT = "local c" +
            "\nc = redis.call('get',KEYS[1])" +
            // 调用量已经超过最大值，直接返回
            "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
            "\nreturn tonumber(c);" +
            "\nend" +
            // 自增
            "\nc = redis.call('incr',KEYS[1])" +
            "\nif tonumber(c) == 1 then" +
            // 从第一次调用开始限流，设置对应键值的过期
            "\nredis.call('expire',KEYS[1],ARGV[2])" +
            "\nend" +
            "\nreturn c;";

    private static JbootRedis redis;

    /**
     * 限制时长默认为1秒
     */
    public static boolean tryAcquire(String resource, int rate) {
        return tryAcquire(resource, rate, 1);
    }

    /**
     * 尝试是否能正常执行
     *
     * @param resource      资源名
     * @param rate          限制次数
     * @param periodSeconds 限制时长，单位为秒
     * @return true 可以执行
     * false 限次，禁止
     */
    public static boolean tryAcquire(String resource, int rate, int periodSeconds) {
        if (redis == null) {
            redis = JbootRedisManager.me().getRedis();
            if (redis == null) {
                throw new JbootIllegalConfigException("Redis config not well, can not use LimitScope.CLUSTER in @EnableLimit() ");
            }
        }
        Long count = (Long) redis.eval(RATE_LIMIT_SCRIPT, 1, resource, String.valueOf(rate), String.valueOf(periodSeconds));
        return count <= rate;
    }
}
