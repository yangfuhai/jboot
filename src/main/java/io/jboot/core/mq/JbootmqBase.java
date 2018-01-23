/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.mq;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;


public abstract class JbootmqBase implements Jbootmq {

    private static final Log LOG = Log.getLog(JbootmqBase.class);

    private List<JbootmqMessageListener> allChannelListeners = new CopyOnWriteArrayList<>();
    private Multimap<String, JbootmqMessageListener> listenersMap = ArrayListMultimap.create();
    protected JbootmqConfig config = Jboot.config(JbootmqConfig.class);

    protected Set<String> channels = Sets.newHashSet();

    private final ExecutorService threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());


    @Override
    public void addMessageListener(JbootmqMessageListener listener) {
        allChannelListeners.add(listener);
    }

    @Override
    public void addMessageListener(JbootmqMessageListener listener, String forChannel) {
        String[] forChannels = forChannel.split(",");
        for (String channel : forChannels) {
            if (StringUtils.isBlank(channel)) {
                continue;
            }
            listenersMap.put(channel.trim(), listener);
        }
    }

    @Override
    public void removeListener(JbootmqMessageListener listener) {
        allChannelListeners.remove(listener);
        for (String channel : listenersMap.keySet()) {
            listenersMap.remove(channel, listener);
        }
    }

    @Override
    public void removeAllListeners() {
        allChannelListeners.clear();
        listenersMap.clear();
    }

    protected void initChannels() {
        String channelString = config.getChannel();
        if (StringUtils.isBlank(channelString)) {
            LOG.warn("jboot.mq.channel is blank or null, please config mq channels when you use.");
            return;
        }

        this.channels.addAll(StringUtils.splitToSet(channelString, ","));
    }

    protected void ensureChannelExist(String toChannel) {
        if (!this.channels.contains(toChannel)) {
            throw new JbootIllegalConfigException(toChannel + " not exist, please config jboot.mq.channel to set the channel.");
        }
    }


    @Override
    public Collection<JbootmqMessageListener> getAllChannelListeners() {
        return allChannelListeners;
    }


    @Override
    public Collection<JbootmqMessageListener> getListenersByChannel(String channel) {
        return listenersMap.get(channel);
    }

    public void notifyListeners(String channel, Object message) {
        notifyAll(channel, message, allChannelListeners);
        notifyAll(channel, message, listenersMap.get(channel));
    }


    private void notifyAll(String channel, Object message, Collection<JbootmqMessageListener> listeners) {
        if (listeners == null || listeners.size() == 0) {
            return;
        }

        for (JbootmqMessageListener listener : listeners) {
            threadPool.execute(() -> {
                listener.onMessage(channel, message);
            });
        }
    }
}
