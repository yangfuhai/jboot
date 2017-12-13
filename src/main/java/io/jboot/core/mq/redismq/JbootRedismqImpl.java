/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.mq.redismq;

import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.component.redis.JbootRedis;
import io.jboot.component.redis.JbootRedisManager;
import io.jboot.core.mq.Jbootmq;
import io.jboot.core.mq.JbootmqBase;
import io.jboot.exception.JbootIllegalConfigException;
import redis.clients.jedis.BinaryJedisPubSub;


public class JbootRedismqImpl extends JbootmqBase implements Jbootmq, Runnable {

    private static final Log LOG = Log.getLog(JbootRedismqImpl.class);

    private JbootRedis redis;
    private Thread dequeueThread;

    public JbootRedismqImpl() {
        
        initChannels();

        JbootmqRedisConfig redisConfig = Jboot.config(JbootmqRedisConfig.class);
        if (redisConfig.isConfigOk()) {
            redis = JbootRedisManager.me().getRedis(redisConfig);
        } else {
            redis = Jboot.me().getRedis();
        }

        if (redis == null) {
            throw new JbootIllegalConfigException("can not get redis,please check your jboot.properties");
        }

        Object[] channels = this.channels.toArray();

        redis.subscribe(new BinaryJedisPubSub() {
            @Override
            public void onMessage(byte[] channel, byte[] message) {
                notifyListeners(redis.bytesToKey(channel), Jboot.me().getSerializer().deserialize(message));
            }
        }, redis.keysToBytesArray(channels));

        dequeueThread = new Thread(this);
        dequeueThread.start();
    }


    @Override
    public void enqueue(Object message, String toChannel) {
        ensureChannelExist(toChannel);
        redis.lpush(toChannel, message);
    }


    @Override
    public void publish(Object message, String toChannel) {
        ensureChannelExist(toChannel);
        redis.publish(redis.keyToBytes(toChannel), Jboot.me().getSerializer().serialize(message));
    }


    @Override
    public void run() {
        for (; ; ) {
            try {
                doExecuteDequeue();
                Thread.sleep(100);
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
