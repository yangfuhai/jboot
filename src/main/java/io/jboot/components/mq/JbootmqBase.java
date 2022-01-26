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

import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.components.serializer.JbootSerializer;
import io.jboot.exception.JbootException;
import io.jboot.utils.NamedThreadFactory;
import io.jboot.utils.StrUtil;

import java.util.*;
import java.util.concurrent.*;


public abstract class JbootmqBase implements Jbootmq {

    private static final Log LOG = Log.getLog(JbootmqBase.class);

    private List<JbootmqMessageListener> globalListeners = new CopyOnWriteArrayList<>();
    private Map<String, List<JbootmqMessageListener>> channelListeners = new ConcurrentHashMap<>();

    protected Set<String> channels = new HashSet<>();
    protected Set<String> syncRecevieMessageChannels = new HashSet<>();
    protected JbootSerializer serializer;

    protected final JbootmqConfig config;

    private final ExecutorService threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new NamedThreadFactory("jbootmq"));


    public JbootmqBase(JbootmqConfig config) {
        this.config = config;
        String channelString = config.getChannel();
        if (StrUtil.isBlank(channelString)) {
            return;
        }

        this.channels.addAll(StrUtil.splitToSet(channelString, ","));

        if (StrUtil.isNotBlank(config.getSyncRecevieMessageChannel())) {
            this.syncRecevieMessageChannels.addAll(StrUtil.splitToSet(config.getSyncRecevieMessageChannel(), ","));
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

    private synchronized void addChannelListener(String channel, JbootmqMessageListener listener) {
        List<JbootmqMessageListener> listeners = channelListeners.get(channel);
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
            channelListeners.put(channel, listeners);
        }
        listeners.add(listener);
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
            LOG.warn("Jboot has recevied mq message, But it has no listener to process. channel:" +
                    channel + "  message:" + message);
        }
    }


    protected boolean notifyListeners(String channel, Object message, MessageContext context, Collection<JbootmqMessageListener> listeners) {
        if (listeners == null || listeners.size() == 0) {
            return false;
        }

        if (syncRecevieMessageChannels.contains(channel)) {
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
            throw new JbootException("Jboot MQ has started.");
        }

        if (channels == null || channels.isEmpty()) {
            throw new JbootException("Jboot MQ's channels is null or empty, Please config channels.");
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


    protected abstract void onStartListening();


    @Override
    public JbootmqConfig getConfig() {
        return config;
    }
}
