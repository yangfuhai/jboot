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
package io.jboot.components.mq;

import io.jboot.Jboot;
import io.jboot.components.mq.aliyunmq.JbootAliyunmqImpl;
import io.jboot.components.mq.local.JbootLocalmqImpl;
import io.jboot.components.mq.qpidmq.JbootQpidmqImpl;
import io.jboot.components.mq.rabbitmq.JbootRabbitmqImpl;
import io.jboot.components.mq.redismq.JbootRedismqImpl;
import io.jboot.components.mq.rocketmq.JbootRocketmqImpl;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.ConfigUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JbootmqManager {

    private static JbootmqManager manager;

    public static JbootmqManager me() {
        if (manager == null) {
            manager = ClassUtil.singleton(JbootmqManager.class);
        }
        return manager;
    }

    private Map<String, Jbootmq> jbootmqMap = new ConcurrentHashMap<>();


    public Jbootmq getJbootmq() {
        Jbootmq defaultMq = jbootmqMap.get("default");
        if (defaultMq == null) {
            synchronized (this) {
                defaultMq = jbootmqMap.get("default");
                if (defaultMq == null) {
                    JbootmqConfig config = Jboot.config(JbootmqConfig.class);
                    defaultMq = getJbootmq(config);
                    if (defaultMq != null) {
                        jbootmqMap.put("default", defaultMq);
                    }
                }
            }
        }
        return getJbootmq("default");
    }

    public Jbootmq getJbootmq(String name) {
        Jbootmq mq = jbootmqMap.get(name);
        if (mq == null) {
            synchronized (this) {
                mq = jbootmqMap.get(name);
                if (mq == null) {
                    Map<String, JbootmqConfig> configModels = ConfigUtil.getConfigModels(JbootmqConfig.class);
                    JbootmqConfig.TYPES.forEach(configModels::remove);

                    if (!configModels.containsKey(name)) {
                        throw new JbootIllegalConfigException("Please config \"jboot.mq." + name + ".type\" in your jboot.properties.");
                    }
                    mq = getJbootmq(configModels.get(name));
                    if (mq != null) {
                        jbootmqMap.put(name, mq);
                    }
                }
            }
        }

        return mq;
    }

    public Jbootmq getJbootmq(JbootmqConfig config) {
        return buildJbootmq(config);
    }

    private Jbootmq buildJbootmq(JbootmqConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }

        if (!config.isConfigOk()) {
            return null;
        }

        switch (config.getType()) {
            case JbootmqConfig.TYPE_REDIS:
                return new JbootRedismqImpl(config);
            case JbootmqConfig.TYPE_ALIYUNMQ:
                return new JbootAliyunmqImpl(config);
            case JbootmqConfig.TYPE_RABBITMQ:
                return new JbootRabbitmqImpl(config);
            case JbootmqConfig.TYPE_ROCKETMQ:
                return new JbootRocketmqImpl(config);
            case JbootmqConfig.TYPE_QPID:
                return new JbootQpidmqImpl(config);
            case JbootmqConfig.TYPE_ACTIVEMQ:
                throw new RuntimeException("not finished!!!!");
            case JbootmqConfig.TYPE_LOCAL:
                return new JbootLocalmqImpl(config);
            default:
                return JbootSpiLoader.load(Jbootmq.class, config.getType(), config);
        }

    }
}
