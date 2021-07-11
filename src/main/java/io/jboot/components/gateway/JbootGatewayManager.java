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
package io.jboot.components.gateway;

import io.jboot.app.config.JbootConfigUtil;
import io.jboot.components.gateway.discovery.GatewayDiscovery;
import io.jboot.components.gateway.discovery.GatewayDiscoveryManager;
import io.jboot.components.gateway.discovery.GatewayInstance;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private GatewayErrorRender gatewayErrorRender;
    private GatewayDiscovery discovery;


    private JbootGatewayManager() {

        initDiscovery();

        initConfigs();
    }

    private void initConfigs() {
        Map<String, JbootGatewayConfig> configMap = JbootConfigUtil.getConfigModels(JbootGatewayConfig.class, "jboot.gateway");
        for (Map.Entry<String, JbootGatewayConfig> entry : configMap.entrySet()) {
            if ("discovery".equals(entry.getKey()) || "instance".equals(entry.getKey())) {
                continue;
            }
            JbootGatewayConfig config = entry.getValue();
            if (StrUtil.isBlank(config.getName())) {
                config.setName(entry.getKey());
            }
            registerConfig(config);
        }
    }

    /**
     * 初始化服务发现
     */
    private void initDiscovery() {
        this.discovery = GatewayDiscoveryManager.me().getGatewayDiscovery();
    }

    public boolean isConfigOk() {
        return configMap != null && !configMap.isEmpty();
    }


    /**
     * 动态注册新的路由配置
     *
     * @param config 配置信息
     */
    public void registerConfig(JbootGatewayConfig config) {
        if (configMap == null) {
            configMap = new ConcurrentHashMap<>();
        }
        configMap.put(config.getName(), config);

        if (discovery != null) {
            List<GatewayInstance> healthyInstances = discovery.selectInstances(config.getName(), true);
            syncDiscoveryUris(healthyInstances, config);

            discovery.subscribe(config.getName(), eventInfo -> {
                List<GatewayInstance> changedInstances = eventInfo.getInstances();
                syncDiscoveryUris(changedInstances, config);
            });
        }

        if (config.isEnable()) {
            JbootGatewayHealthChecker.me().start();
        }
    }

    private void syncDiscoveryUris(List<GatewayInstance> instances, JbootGatewayConfig config) {
        if (instances == null) {
            config.syncDiscoveryUris(null);
        } else {
            Set<String> uris = new HashSet<>();
            instances.forEach(instance -> {
                if (instance.isHealthy()) {
                    uris.add(instance.getUri());
                }
            });
            config.syncDiscoveryUris(uris);
        }
    }


    /**
     * 动态移除路由配置
     *
     * @param name 配置名称
     * @return 被移除的配置信息
     */
    public JbootGatewayConfig removeConfig(String name) {
        return configMap == null ? null : configMap.remove(name);
    }


    /**
     * 获取某个配置信息
     *
     * @param name 配置名称
     * @return 配置信息
     */
    public JbootGatewayConfig getConfig(String name) {
        return configMap == null ? null : configMap.get(name);
    }


    /**
     * 获取所有的配置信息
     *
     * @return
     */
    public Map<String, JbootGatewayConfig> getConfigMap() {
        return configMap;
    }

    /**
     * 匹配可用的网关
     *
     * @param req 请求
     * @return 返回匹配到的网关配置
     */
    public JbootGatewayConfig matchingConfig(HttpServletRequest req) {
        if (configMap != null && !configMap.isEmpty()) {
            for (JbootGatewayConfig config : configMap.values()) {
                if (config.isEnable() && config.matches(req)) {
                    return config;
                }
            }
        }
        return null;
    }

    public GatewayErrorRender getGatewayErrorRender() {
        return gatewayErrorRender;
    }

    public void setGatewayErrorRender(GatewayErrorRender gatewayErrorRender) {
        this.gatewayErrorRender = gatewayErrorRender;
    }


}
