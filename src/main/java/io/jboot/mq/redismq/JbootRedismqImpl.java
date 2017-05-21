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
package io.jboot.mq.redismq;

import io.jboot.Jboot;
import io.jboot.mq.Jbootmq;
import io.jboot.mq.JbootmqBase;
import io.jboot.utils.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.codec.FstCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;


public class JbootRedismqImpl<M> extends JbootmqBase<M> implements Jbootmq<M>, MessageListener<M> {

    RedissonClient redissonClient;
    RTopic<M> topic;

    public JbootRedismqImpl() {
        JbootmqRedisConfig redisConfig = Jboot.config(JbootmqRedisConfig.class);


        Config redissionConfig = new Config();
        redissionConfig.setCodec(new FstCodec());

        if (redisConfig.isCluster()) {
            ClusterServersConfig clusterServersConfig = redissionConfig.useClusterServers();
            clusterServersConfig.addNodeAddress(redisConfig.getAddress().split(","));
            if (StringUtils.isNotBlank(redisConfig.getPassword())) {
                clusterServersConfig.setPassword(redisConfig.getPassword());
            }
        } else {
            SingleServerConfig singleServerConfig = redissionConfig.useSingleServer();
            singleServerConfig.setAddress(redisConfig.getAddress());
            if (StringUtils.isNotBlank(redisConfig.getPassword())) {
                singleServerConfig.setPassword(redisConfig.getPassword());
            }
        }

        redissonClient = Redisson.create(redissionConfig);
        topic = redissonClient.getTopic(JbootRedismqImpl.class.getName());
        topic.addListener(this);
    }

    @Override
    public void publish(M message) {
        topic.publish(message);
    }


    @Override
    public void onMessage(String channel, M message) {
        notifyListeners(channel, message);
    }
}
