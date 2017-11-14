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
package io.jboot.core.mq;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jfinal.log.Log;
import io.jboot.utils.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class JbootmqBase implements Jbootmq {

    private static final Log log = Log.getLog(JbootmqBase.class);

    private List<JbootmqMessageListener> allChannelListeners = new CopyOnWriteArrayList<>();
    private Multimap<String, JbootmqMessageListener> listenersMap = ArrayListMultimap.create();


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
            try {
                listener.onMessage(channel, message);
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }
    }
}
