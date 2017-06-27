/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.core.mq.redismq;

import io.jboot.Jboot;
import io.jboot.component.redis.JbootRedisManager;
import io.jboot.core.cache.ehredis.JbootEhredisCacheImpl;
import io.jboot.component.redis.JbootRedis;
import io.jboot.exception.JbootException;
import io.jboot.core.mq.Jbootmq;
import io.jboot.core.mq.JbootmqBase;
import io.jboot.utils.StringUtils;
import redis.clients.jedis.BinaryJedisPubSub;


public class JbootRedismqImpl extends JbootmqBase implements Jbootmq {

    JbootRedis redis;

    public JbootRedismqImpl() {
        JbootmqRedisConfig redisConfig = Jboot.config(JbootmqRedisConfig.class);
        if (redisConfig.isConfigOk()) {
            redis = JbootRedisManager.me().getReidis(redisConfig);
        } else {
            redis = Jboot.getRedis();
        }

        if (redis == null) {
            throw new JbootException("can not get redis,please check your jboot.properties");
        }

        String channelString = redisConfig.getChannel();
        if (StringUtils.isBlank(channelString)) {
            throw new JbootException("channel config cannot empty in jboot.properties");
        }

        if (channelString.endsWith(",")) {
            channelString += JbootEhredisCacheImpl.DEFAULT_NOTIFY_CHANNEL;
        } else {
            channelString += "," + JbootEhredisCacheImpl.DEFAULT_NOTIFY_CHANNEL;
        }


        String[] channels = channelString.split(",");
        redis.subscribe(new BinaryJedisPubSub() {
            @Override
            public void onMessage(byte[] channel, byte[] message) {
                notifyListeners(redis.bytesToKey(channel), Jboot.getSerializer().deserialize(message));
            }
        }, redis.keysToBytesArray(channels));

    }


    @Override
    public void publish(Object message, String toChannel) {
        redis.publish(redis.keyToBytes(toChannel), Jboot.getSerializer().serialize(message));
    }
}
