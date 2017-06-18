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

import com.jfinal.log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class JbootmqBase implements Jbootmq {

    private static final Log log = Log.getLog(JbootmqBase.class);

    private List<JbootmqMessageListener> listeners = new CopyOnWriteArrayList<>();
    private Map<String, List<JbootmqMessageListener>> listenerMap = new ConcurrentHashMap<>();


    @Override
    public void addMessageListener(JbootmqMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void addMessageListener(JbootmqMessageListener listener, String forChannel) {
        synchronized (listenerMap) {
            String[] forChannels = forChannel.split(",");
            for (String channel : forChannels) {
                List<JbootmqMessageListener> list = listenerMap.get(channel);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(listener);
                listenerMap.put(channel, list);
            }
        }
    }

    @Override
    public void removeListener(JbootmqMessageListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        listeners.clear();
    }

    @Override
    public List<JbootmqMessageListener> getListeners() {
        return listeners;
    }

    public void notifyListeners(String channel, Object message) {
        notifyAll(channel, message, listeners);
        notifyAll(channel, message, listenerMap.get(channel));
    }


    private void notifyAll(String channel, Object message, List<JbootmqMessageListener> listeners) {
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
