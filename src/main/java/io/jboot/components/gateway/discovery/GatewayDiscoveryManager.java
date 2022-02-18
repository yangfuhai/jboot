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
package io.jboot.components.gateway.discovery;


import io.jboot.Jboot;
import io.jboot.utils.ConfigUtil;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.utils.NetUtil;

import java.util.Map;

public class GatewayDiscoveryManager {

    private static GatewayDiscoveryManager manager = new GatewayDiscoveryManager();

    private GatewayDiscoveryManager() {
    }

    public static GatewayDiscoveryManager me() {
        return manager;
    }

    private GatewayDiscoveryConfig discoveryConfig;
    private GatewayDiscovery gatewayDiscovery;
    private boolean isInited = false;


    public void init() {
        discoveryConfig = Jboot.config(GatewayDiscoveryConfig.class);

        gatewayDiscovery = createDiscovery(discoveryConfig);

        exportLocalInstance(gatewayDiscovery);

        isInited = true;
    }

    /**
     * 暴露本地的示例到 nacos 等服务注册中心
     *
     * @param gatewayDiscovery
     */
    private void exportLocalInstance(GatewayDiscovery gatewayDiscovery) {
        if (gatewayDiscovery == null) {
            return;
        }

        Map<String, GatewayInstanceConfig> instanceConfigMap = ConfigUtil.getConfigModels(GatewayInstanceConfig.class, "jboot.gateway.instance");
        for (GatewayInstanceConfig instanceConfig : instanceConfigMap.values()) {

            GatewayInstance instance = new GatewayInstance();
            instance.setHealthy(true);
            instance.setServiceName(instanceConfig.getName());
            instance.setUri(instanceConfig.toUri());

            instance.setHost(NetUtil.getLocalIpAddress());
            instance.setPort(Integer.parseInt(Jboot.configValue("undertow.port", "8080")));

            gatewayDiscovery.registerInstance(instance);
        }
    }

    public GatewayDiscovery getGatewayDiscovery() {
        if (!isInited){
            init();
        }
        return gatewayDiscovery;
    }

    public GatewayDiscoveryConfig getDiscoveryConfig() {
        return discoveryConfig;
    }

    public GatewayDiscovery createDiscovery(GatewayDiscoveryConfig config) {
        if (config == null || !config.isConfigOk() || !config.isEnable()) {
            return null;
        }

        switch (config.getType()) {
            case GatewayDiscoveryConfig.TYPE_NACOS:
                return new NacosGatewayDiscovery();
            default:
                return JbootSpiLoader.load(GatewayDiscovery.class, config.getType());
        }
    }


}






