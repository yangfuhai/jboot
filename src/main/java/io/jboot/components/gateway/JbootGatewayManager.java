/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.gateway;

import io.jboot.app.config.JbootConfigUtil;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/21
 */
public class JbootGatewayManager {

    private static JbootGatewayManager me = new JbootGatewayManager();

    public static JbootGatewayManager me() {
        return me;
    }

    private Map<String, JbootGatewayConfig> configMap;

    public void init() {
        Map<String, JbootGatewayConfig> configMap = JbootConfigUtil.getConfigModels(JbootGatewayConfig.class, "jboot.gateway");
        if (configMap != null && !configMap.isEmpty()) {

            for (Map.Entry<String, JbootGatewayConfig> e : configMap.entrySet()) {
                JbootGatewayConfig config = e.getValue();
                if (config.isConfigOk() && config.isEnable()) {
                    if (StrUtil.isNotBlank(config.getName())) {
                        config.setName(e.getKey());
                    }
                    registerConfig(config);
                }
            }
        }
    }


    public synchronized void registerConfig(JbootGatewayConfig config) {
        if (configMap == null) {
            configMap = new ConcurrentHashMap<>();
        }
        configMap.put(config.getName(), config);
    }


    public JbootGatewayConfig getConfig(String name) {
        return configMap == null ? null : configMap.get(name);
    }


    public Map<String, JbootGatewayConfig> getConfigMap() {
        return configMap;
    }

    public JbootGatewayConfig matchingConfig(HttpServletRequest req) {
        if (configMap != null && !configMap.isEmpty()) {
            Iterator<JbootGatewayConfig> iterator = configMap.values().iterator();
            while (iterator.hasNext()) {
                JbootGatewayConfig config = iterator.next();
                if (config.matches(req)) {
                    return config;
                }
            }
        }
        return null;
    }


}
