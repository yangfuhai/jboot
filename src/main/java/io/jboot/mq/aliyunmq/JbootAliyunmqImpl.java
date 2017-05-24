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
package io.jboot.mq.aliyunmq;

import com.aliyun.openservices.ons.api.*;
import io.jboot.Jboot;
import io.jboot.cache.ehredis.JbootEhredisCacheImpl;
import io.jboot.exception.JbootException;
import io.jboot.mq.Jbootmq;
import io.jboot.mq.JbootmqBase;
import io.jboot.utils.StringUtils;
import org.nustaq.serialization.FSTConfiguration;

import java.util.Properties;


public class JbootAliyunmqImpl extends JbootmqBase implements Jbootmq, MessageListener {

    private Producer producer;
    private Consumer consumer;
    static FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

    public void JbootAliyunmq() {

        JbootAliyunmqConfig config = Jboot.config(JbootAliyunmqConfig.class);

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, config.getAccessKey());//AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, config.getSecretKey());//SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.ProducerId, config.getProducerId());//您在控制台创建的Producer ID
        properties.put(PropertyKeyConst.ONSAddr, config.getAddr());
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, config.getSendMsgTimeoutMillis());//设置发送超时时间，单位毫秒

        producer = ONSFactory.createProducer(properties);
        consumer = ONSFactory.createConsumer(properties);

        String channel = config.getChannel();
        if (StringUtils.isBlank(channel)) {
            throw new JbootException("jboot.mq.aliyun.channel config cannot empty in jboot.properties");
        }

        String[] channels = channel.split(",");
        for (String c : channels) {
            consumer.subscribe(c, "*", this);
        }

        /**
         * 阿里云需要提前注册缓存通知使用的通道
         */
        consumer.subscribe(JbootEhredisCacheImpl.DEFAULT_NOTIFY_CHANNEL, "*", this);

        producer.start();
        consumer.start();
    }

    @Override
    public void publish(Object message, String toChannel) {
        byte[] bytes = fst.asByteArray(message);
        Message onsMessage = new Message(toChannel, "*", bytes);
        producer.send(onsMessage);
    }

    @Override
    public Action consume(Message message, ConsumeContext context) {
        byte[] bytes = message.getBody();
        Object object = fst.asObject(bytes);
        notifyListeners(message.getTopic(), object);
        return Action.CommitMessage;
    }
}
