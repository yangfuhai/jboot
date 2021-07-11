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
package io.jboot.components.gateway.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.jboot.Jboot;
import io.jboot.app.config.support.nacos.NacosServerConfig;
import io.jboot.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NacosGatewayDiscovery implements GatewayDiscovery {

    private GatewayDiscoveryConfig discoveryConfig;
    private NacosServerConfig nacosServerConfig;
    private NamingService namingService;


    public NacosGatewayDiscovery() {
        this.discoveryConfig = GatewayDiscoveryManager.me().getDiscoveryConfig();
        this.nacosServerConfig = Jboot.config(NacosServerConfig.class, "jboot.gateway.discovery.nacos");
        try {
            this.namingService = NamingFactory.createNamingService(nacosServerConfig.toProperties());
        } catch (NacosException e) {
            throw new RuntimeException("Can not create Nacos NamingService for gateway discovry. ", e);
        }
    }

    @Override
    public void registerInstance(GatewayInstance instance) {
        try {
            namingService.registerInstance(instance.getServiceName(), discoveryConfig.getGroup(), gatewayInstance2NacosInstance(instance));
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deregisterInstance(GatewayInstance instance) {
        try {
            namingService.deregisterInstance(instance.getServiceName(), discoveryConfig.getGroup(), gatewayInstance2NacosInstance(instance));
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<GatewayInstance> getAllInstances(String serviceName) {
        try {
            List<Instance> nacosInstanceList = namingService.getAllInstances(serviceName, discoveryConfig.getGroup());
            if (nacosInstanceList != null && !nacosInstanceList.isEmpty()) {
                List<GatewayInstance> retList = new ArrayList<>();
                for (Instance nacosInstance : nacosInstanceList) {
                    retList.add(nacosInstance2GatewayInstance(nacosInstance));
                }
                return retList;
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<GatewayInstance> selectInstances(String serviceName, boolean healthy) {
        try {
            List<Instance> nacosInstanceList = namingService.selectInstances(serviceName, discoveryConfig.getGroup(), healthy);
            if (nacosInstanceList != null && !nacosInstanceList.isEmpty()) {
                List<GatewayInstance> retList = new ArrayList<>();
                for (Instance nacosInstance : nacosInstanceList) {
                    retList.add(nacosInstance2GatewayInstance(nacosInstance));
                }
                return retList;
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void subscribe(String serviceName, GatewayDiscoveryListener listener) {
        try {
            namingService.subscribe(serviceName, discoveryConfig.getGroup(), event -> {
                GatewayDiscoveryListener.EventInfo eventInfo = new GatewayDiscoveryListener.EventInfo();
                eventInfo.setServiceName(((NamingEvent) event).getServiceName());
                List<Instance> nacosInstanceList = ((NamingEvent) event).getInstances();
                if (nacosInstanceList != null && !nacosInstanceList.isEmpty()) {
                    for (Instance nacosInstance : nacosInstanceList) {
                        GatewayInstance instance = nacosInstance2GatewayInstance(nacosInstance);
                        instance.setServiceName(eventInfo.getServiceName());
                        eventInfo.addInstances(instance);
                    }
                }
                listener.onEvent(eventInfo);
            });
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    private GatewayInstance nacosInstance2GatewayInstance(Instance nacosInstance) {
        if (nacosInstance == null) {
            return null;
        }
        GatewayInstance instance = new GatewayInstance();
        instance.setHost(nacosInstance.getIp());
        instance.setPort(nacosInstance.getPort());
        instance.setServiceName(instance.getServiceName());
        instance.setHealthy(nacosInstance.isHealthy());

        Map<String, String> metadata = nacosInstance.getMetadata();
        if (metadata != null && metadata.containsKey("uri")) {
            instance.setUri(metadata.get("uri"));
        }

        return instance;
    }

    private Instance gatewayInstance2NacosInstance(GatewayInstance gatewayInstance) {
        if (gatewayInstance == null) {
            return null;
        }
        Instance instance = new Instance();
        instance.setIp(gatewayInstance.getHost());
        instance.setPort(gatewayInstance.getPort());
        instance.setServiceName(instance.getServiceName());
        instance.setHealthy(gatewayInstance.isHealthy());

        if (StrUtil.isNotBlank(gatewayInstance.getUri())) {
            instance.addMetadata("uri", gatewayInstance.getUri());
        }

        return instance;
    }
}
