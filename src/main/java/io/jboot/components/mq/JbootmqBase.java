/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.components.serializer.JbootSerializer;
import io.jboot.utils.NamedThreadPools;
import io.jboot.utils.StrUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;


public abstract class JbootmqBase implements Jbootmq {

    private static final Log LOG = Log.getLog(JbootmqBase.class);

    protected final JbootmqConfig config;

    private List<JbootmqMessageListener> globalListeners = new CopyOnWriteArrayList<>();
    private Map<String, List<JbootmqMessageListener>> channelListeners = new ConcurrentHashMap<>();

    protected Set<String> channels = new HashSet<>();
    protected Set<String> syncReceiveMessageChannels = new HashSet<>();
    protected JbootSerializer serializer;

    private ExecutorService threadPool = NamedThreadPools.newFixedThreadPool("jbootmq");

    public JbootmqBase(JbootmqConfig config) {
        this.config = config;
        String channelString = config.getChannel();
        if (StrUtil.isBlank(channelString)) {
            return;
        }

        this.channels.addAll(StrUtil.splitToSet(channelString, ","));

        if (StrUtil.isNotBlank(config.getSyncRecevieMessageChannel())) {
            this.syncReceiveMessageChannels.addAll(StrUtil.splitToSet(config.getSyncRecevieMessageChannel(), ","));
        }
    }


    @Override
    public void addMessageListener(JbootmqMessageListener listener) {
        globalListeners.add(listener);
    }


    @Override
    public void addMessageListener(JbootmqMessageListener listener, String forChannel) {
        String[] forChannels = forChannel.split(",");
        for (String channel : forChannels) {
            if (StrUtil.isNotBlank(channel)) {
                addChannelListener(channel.trim(), listener);
            }
        }
    }

    public final synchronized void addChannelListener(String channel, JbootmqMessageListener listener) {
        List<JbootmqMessageListener> listeners = channelListeners.get(channel);
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
            channelListeners.put(channel, listeners);
        }
        listeners.add(listener);
        channels.add(channel);
    }


    @Override
    public void removeListener(JbootmqMessageListener listener) {
        globalListeners.remove(listener);
        for (List<JbootmqMessageListener> listeners : channelListeners.values()) {
            listeners.remove(listener);
        }
    }

    @Override
    public void removeAllListeners() {
        globalListeners.clear();
        channelListeners.forEach((s, list) -> list.clear());
        channelListeners.clear();
    }


    @Override
    public Collection<JbootmqMessageListener> getGlobalListeners() {
        return globalListeners;
    }


    @Override
    public Collection<JbootmqMessageListener> getListenersByChannel(String channel) {
        return channelListeners.get(channel);
    }

    public void notifyListeners(String channel, Object message, MessageContext context) {

        boolean globalResult = notifyListeners(channel, message, context, globalListeners);
        boolean channelResult = notifyListeners(channel, message, context, channelListeners.get(channel));

        if (!globalResult && !channelResult) {
            LOG.warn("Jboot has received mq message, But it has no listener to process. channel:" +
                    channel + "  message:" + message);
        }
    }


    protected boolean notifyListeners(String channel, Object message, MessageContext context, Collection<JbootmqMessageListener> listeners) {
        if (listeners == null || listeners.size() == 0) {
            return false;
        }

        if (syncReceiveMessageChannels.contains(channel)) {
            for (JbootmqMessageListener listener : listeners) {
                try {
                    listener.onMessage(channel, message, context);
                } catch (Throwable ex) {
                    LOG.warn("listener[" + listener.getClass().getName() + "] execute mq message is error. channel:" +
                            channel + "  message:" + message);
                }
            }
        } else {
            for (JbootmqMessageListener listener : listeners) {
                threadPool.execute(() -> {
                    listener.onMessage(channel, message, context);
                });
            }
        }

        return true;
    }


    public JbootSerializer getSerializer() {
        if (serializer == null) {
            serializer = StrUtil.isNotBlank(config.getSerializer())
                    ? Jboot.getSerializer(config.getSerializer())
                    : Jboot.getSerializer();
        }
        return serializer;
    }


    protected boolean isStarted = false;

    @Override
    public boolean startListening() {
        if (isStarted) {
            return true;
        }

        if (channels == null || channels.isEmpty()) {
            LogKit.warn("Jboot MQ started fail. because it's channels is empty, please config channels. " +
                    "MQ name: {}, type:{}", config.getName(), config.getType());
            return false;
        }

        try {
            isStarted = true;
            onStartListening();
        } catch (Exception ex) {
            LogKit.error("Jboot MQ start fail!", ex);
            isStarted = false;
            return false;
        }

        return true;
    }


    @Override
    public boolean stopListening() {
        if (!isStarted) {
            return true;
        }

        try {
            isStarted = false;
            onStopListening();
        } catch (Exception ex) {
            LogKit.error("Jboot MQ stop fail!", ex);
            isStarted = true;
            return false;
        }

        return true;
    }

    public boolean isStarted() {
        return isStarted;
    }

    protected abstract void onStartListening();

    protected abstract void onStopListening();


    @Override
    public JbootmqConfig getConfig() {
        return config;
    }

    public void setSerializer(JbootSerializer serializer) {
        this.serializer = serializer;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }
}
