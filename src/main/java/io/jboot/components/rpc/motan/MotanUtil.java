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
package io.jboot.components.rpc.motan;

import com.weibo.api.motan.config.*;
import com.weibo.api.motan.util.MotanFrameworkUtil;
import io.jboot.app.config.JbootConfigUtil;
import io.jboot.components.rpc.JbootrpcReferenceConfig;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.components.rpc.Utils;
import io.jboot.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/20
 */
public class MotanUtil {

    private static Map<String, ProtocolConfig> protocolConfigMap = new ConcurrentHashMap<>();
    private static Map<String, RegistryConfig> registryConfigMap = new ConcurrentHashMap<>();
    private static Map<String, BasicRefererInterfaceConfig> baseRefererConfigMap = new ConcurrentHashMap<>();
    private static Map<String, BasicServiceInterfaceConfig> baseServiceConfigMap = new ConcurrentHashMap<>();


    public static void initMotan() {

        //protocol 配置
        Map<String, ProtocolConfig> protocolConfigs = configs(ProtocolConfig.class, "jboot.rpc.motan.protocol");
        if (protocolConfigs != null && !protocolConfigs.isEmpty()) {
            protocolConfigMap.putAll(protocolConfigs);
        } else {
            protocolConfigMap.put("default", MotanFrameworkUtil.getDefaultProtocolConfig());
        }

        //registry 配置
        Map<String, RegistryConfig> registryConfigs = configs(RegistryConfig.class, "jboot.rpc.motan.registry");
        if (registryConfigs != null && !registryConfigs.isEmpty()) {
            registryConfigMap.putAll(registryConfigs);
        } else {
            registryConfigMap.put("default", MotanFrameworkUtil.getDefaultRegistryConfig());
        }

        //methodConfig 配置
        Map<String, MethodConfig> methodConfigs = configs(MethodConfig.class, "jboot.rpc.motan.method");


        //baseService 配置
        Map<String, BasicServiceInterfaceConfig> serviceConfigs = configs(BasicServiceInterfaceConfig.class, "jboot.rpc.motan.service");
        Utils.setChildConfig(serviceConfigs, methodConfigs, "jboot.rpc.motan.service", "method");
        Utils.setChildConfig(serviceConfigs, protocolConfigs, "jboot.rpc.motan.service", "protocol");
        Utils.setChildConfig(serviceConfigs, registryConfigs, "jboot.rpc.motan.service", "registry");


        if (serviceConfigs != null && !serviceConfigs.isEmpty()) {
            baseServiceConfigMap.putAll(serviceConfigs);
        }

        //baseReferer 配置
        Map<String, BasicRefererInterfaceConfig> refererConfigs = configs(BasicRefererInterfaceConfig.class, "jboot.rpc.motan.referer");
        Utils.setChildConfig(refererConfigs, methodConfigs, "jboot.rpc.motan.referer", "method");
        Utils.setChildConfig(refererConfigs, protocolConfigs, "jboot.rpc.motan.referer", "protocol");
        Utils.setChildConfig(refererConfigs, registryConfigs, "jboot.rpc.motan.referer", "registry");

        if (refererConfigs != null && !refererConfigs.isEmpty()) {
            baseRefererConfigMap.putAll(refererConfigs);
        }


    }


    public static RefererConfig toRefererConfig(JbootrpcReferenceConfig rc) {
        RefererConfig refererConfig = new RefererConfig();
        Utils.copyFields(rc, refererConfig);


        //referer protocol
        if (StrUtil.isNotBlank(rc.getProtocol())) {
            List<ProtocolConfig> protocolConfigs = new ArrayList<>();
            Set<String> protocolNames = StrUtil.splitToSetByComma(rc.getRegistry());
            for (String protocalName : protocolNames) {
                ProtocolConfig registryConfig = protocolConfigMap.get(protocalName);
                if (registryConfig != null) {
                    protocolConfigs.add(registryConfig);
                }
            }
            if (!protocolConfigs.isEmpty()) {
                refererConfig.setProtocols(protocolConfigs);
            }
        } else {
            refererConfig.setProtocols(toList(protocolConfigMap));
        }


        //referer registry
        if (StrUtil.isNotBlank(rc.getRegistry())) {
            List<RegistryConfig> registryConfigs = new ArrayList<>();
            Set<String> registryNames = StrUtil.splitToSetByComma(rc.getRegistry());
            for (String registryName : registryNames) {
                RegistryConfig registryConfig = registryConfigMap.get(registryName);
                if (registryConfig != null) {
                    registryConfigs.add(registryConfig);
                }
            }
            if (!registryConfigs.isEmpty()) {
                refererConfig.setRegistries(registryConfigs);
            }
        } else {
            refererConfig.setRegistries(toList(registryConfigMap));
        }


        return refererConfig;
    }


    public static ServiceConfig toServiceConfig(JbootrpcServiceConfig sc) {
        ServiceConfig serviceConfig = new ServiceConfig();
        Utils.copyFields(sc, serviceConfig);


        //service protocol
        if (StrUtil.isNotBlank(sc.getProtocol())) {
            List<ProtocolConfig> protocolConfigs = new ArrayList<>();
            Set<String> protocolNames = StrUtil.splitToSetByComma(sc.getRegistry());
            for (String protocalName : protocolNames) {
                ProtocolConfig registryConfig = protocolConfigMap.get(protocalName);
                if (registryConfig != null) {
                    protocolConfigs.add(registryConfig);
                }
            }
            if (!protocolConfigs.isEmpty()) {
                serviceConfig.setProtocols(protocolConfigs);
            }
        } else {
            serviceConfig.setProtocols(toList(protocolConfigMap));
        }


        //service registry
        if (StrUtil.isNotBlank(sc.getRegistry())) {
            List<RegistryConfig> registryConfigs = new ArrayList<>();
            Set<String> registryNames = StrUtil.splitToSetByComma(sc.getRegistry());
            for (String registryName : registryNames) {
                RegistryConfig registryConfig = registryConfigMap.get(registryName);
                if (registryConfig != null) {
                    registryConfigs.add(registryConfig);
                }
            }
            if (!registryConfigs.isEmpty()) {
                serviceConfig.setRegistries(registryConfigs);
            }
        } else {
            serviceConfig.setRegistries(toList(registryConfigMap));
        }


        return serviceConfig;
    }


    public static BasicRefererInterfaceConfig getBaseReferer(String name) {
        return baseRefererConfigMap.get(name);
    }


    public static BasicServiceInterfaceConfig getBaseService(String name) {
        return baseServiceConfigMap.get(name);
    }

    private static <T> Map<String, T> configs(Class<T> clazz, String prefix) {
        return JbootConfigUtil.getConfigModels(clazz, prefix);
    }

    private static <T> List<T> toList(Map<String, T> map) {
        List<T> list = new ArrayList<>(map.size());
        for (Map.Entry<String, T> entry : map.entrySet()) {
            AbstractConfig config = (AbstractConfig) entry.getValue();
            config.setId(entry.getKey());
            list.add((T) config);
        }
        return list;
    }
}
