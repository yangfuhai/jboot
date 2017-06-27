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
package io.jboot.component.redis;


import io.jboot.Jboot;
import io.jboot.component.redis.impl.JbootClusterRedisImpl;
import io.jboot.component.redis.impl.JbootRedisImpl;

/**
 * 参考： com.jfinal.plugin.redis
 * JbootRedis 命令文档: http://redisdoc.com/
 */
public class JbootRedisManager {

    private static JbootRedisManager manager = new JbootRedisManager();

    private JbootRedisManager() {
    }

    public static JbootRedisManager me() {
        return manager;
    }

    private JbootRedis redis;

    public JbootRedis getReidis() {
        if (redis == null) {
            JbootRedisConfig config = Jboot.config(JbootRedisConfig.class);
            redis = getReidis(config);
        }

        return redis;
    }

    public JbootRedis getReidis(JbootRedisConfig config) {
        if (config == null || !config.isConfigOk()) {
            return null;
        }
        if (config.isCluster()) {
            return new JbootClusterRedisImpl(config);
        } else {
            return new JbootRedisImpl(config);
        }
    }


}






