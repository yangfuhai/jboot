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

import io.jboot.config.annotation.PropertieConfig;


@PropertieConfig(prefix = "jboot.mq")
public class JbootmqConfig {
    public static final String TYPE_REDIS = "redis";
    public static final String TYPE_ACTIVEMQ = "activemq";
    public static final String TYPE_ALIYUNMQ = "aliyunmq";
    public static final String TYPE_HORNETQ = "hornetq";
    public static final String TYPE_RABBITMQ = "rabbitmq";

    public String type = TYPE_REDIS;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
