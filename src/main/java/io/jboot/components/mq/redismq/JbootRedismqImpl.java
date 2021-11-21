/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.components.mq.redismq;

import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.utils.ConfigUtil;
import io.jboot.components.mq.Jbootmq;
import io.jboot.components.mq.JbootmqBase;
import io.jboot.components.mq.JbootmqConfig;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.support.redis.JbootRedis;
import io.jboot.support.redis.JbootRedisManager;
import io.jboot.utils.StrUtil;
import redis.clients.jedis.BinaryJedisPubSub;

import java.util.Map;


public class JbootRedismqImpl extends JbootmqBase implements Jbootmq, Runnable {

    private static final Log LOG = Log.getLog(JbootRedismqImpl.class);

    private JbootRedis redis;
    private Thread dequeueThread;

    public JbootRedismqImpl(JbootmqConfig config) {
        super(config);

        JbootRedismqConfig redisConfig = null;
        String typeName = config.getTypeName();
        if (StrUtil.isNotBlank(typeName)) {
            Map<String, JbootRedismqConfig> configModels = ConfigUtil.getConfigModels(JbootRedismqConfig.class);
            if (!configModels.containsKey(typeName)) {
                throw new JbootIllegalConfigException("Please config \"jboot.mq.redis." + typeName + ".host\" in your jboot.properties.");
            }
            redisConfig = configModels.get(typeName);
        } else {
            redisConfig = Jboot.config(JbootRedismqConfig.class);
        }

        if (redisConfig.isConfigOk()) {
            redis = JbootRedisManager.me().getRedis(redisConfig);
        } else {
            redis = Jboot.getRedis();
        }

        if (redis == null) {
            throw new JbootIllegalConfigException("can not use redis mq (redis mq is default), " +
                    "please config jboot.redis.host=your-host , or use other mq component. ");
        }
    }

    @Override
    protected void onStartListening() {

        String[] channels = this.channels.toArray(new String[]{});

        redis.subscribe(new BinaryJedisPubSub() {
            @Override
            public void onMessage(byte[] channel, byte[] message) {
                notifyListeners(redis.bytesToKey(channel), getSerializer().deserialize(message));
            }
        }, redis.keysToBytesArray(channels));

        dequeueThread = new Thread(this, "redis-dequeue-thread");
        dequeueThread.start();
    }


    @Override
    public void enqueue(Object message, String toChannel) {
        redis.lpush(toChannel, message);
    }


    @Override
    public void publish(Object message, String toChannel) {
        redis.publish(redis.keyToBytes(toChannel), getSerializer().serialize(message));
    }


    @Override
    public void run() {
        for (; ; ) {
            try {
                doExecuteDequeue();
                Thread.sleep(1);
            } catch (Throwable ex) {
                LOG.error(ex.toString(), ex);
            }
        }
    }

    private void doExecuteDequeue() {
        for (String channel : this.channels) {
            Object data = redis.lpop(channel);
            if (data != null) {
                notifyListeners(channel, data);
            }
        }
    }
}
