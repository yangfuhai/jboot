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
package io.jboot.mq;

import io.jboot.Jboot;
import io.jboot.mq.redismq.JbootRedismqImpl;
import io.jboot.utils.ClassNewer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JbootmqManager {

    private static JbootmqManager manager;

    private JbootmqManager() {
    }

    public static JbootmqManager me() {
        if (manager == null) {
            manager = ClassNewer.singleton(JbootmqManager.class);
        }
        return manager;
    }


    private Map<Class, Jbootmq> jbootmqMap = new ConcurrentHashMap<>();

    public <T> Jbootmq<T> getJbootmq(Class<T> clazz) {
        Jbootmq<T> jbootmq = jbootmqMap.get(clazz);
        if (jbootmq == null) {
            jbootmq = buildJbootmq(clazz);
            jbootmqMap.put(clazz, jbootmq);
        }
        return jbootmq;
    }

    private <T> Jbootmq<T> buildJbootmq(Class<T> clazz) {
        JbootmqConfig config = Jboot.config(JbootmqConfig.class);

        switch (config.getType()) {
            case JbootmqConfig.TYPE_REDIS:
                return new JbootRedismqImpl<T>();
            case JbootmqConfig.TYPE_ACTIVEMQ:
            case JbootmqConfig.TYPE_ALIYUNMQ:
            case JbootmqConfig.TYPE_HORNETQ:
            case JbootmqConfig.TYPE_RABBITMQ:
                throw new RuntimeException("not finished!!!!");
            default:
                return new JbootRedismqImpl<T>();
        }

    }
}
