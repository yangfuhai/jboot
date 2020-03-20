/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.rpc.motan;

import com.weibo.api.motan.config.*;
import io.jboot.app.config.JbootConfigUtil;
import io.jboot.components.rpc.JbootrpcReferenceConfig;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.components.rpc.Utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/20
 */
public class MotanUtil {

    private static Map<String, ProtocolConfig> protocolConfigMap = new ConcurrentHashMap<>();
    private static Map<String, RegistryConfig> registryConfigMap = new ConcurrentHashMap<>();
    private static Map<String, RefererConfig> baseRefererConfigMap = new ConcurrentHashMap<>();
    private static Map<String, ServiceConfig> baseServiceConfigMap = new ConcurrentHashMap<>();
    private static Map<String, MethodConfig> methodConfigMap = new ConcurrentHashMap<>();


    public static void initMotan() {

        //protocol 配置
        Map<String, ProtocolConfig> protocolConfigs = configs(ProtocolConfig.class, "jboot.rpc.motan.protocol");
        if (protocolConfigs != null && !protocolConfigs.isEmpty()) {
            protocolConfigMap.putAll(protocolConfigs);
        }

        //registry 配置
        Map<String, RegistryConfig> registryConfigs = configs(RegistryConfig.class, "jboot.rpc.motan.registry");
        if (registryConfigs != null && !registryConfigs.isEmpty()) {
            registryConfigMap.putAll(registryConfigs);
        }

        //baseReferer 配置
        Map<String, RefererConfig> refererConfigs = configs(RefererConfig.class, "jboot.rpc.motan.referer");
        if (refererConfigs != null && !refererConfigs.isEmpty()) {
            baseRefererConfigMap.putAll(refererConfigs);
        }

        //baseService 配置
        Map<String, ServiceConfig> serviceConfigs = configs(ServiceConfig.class, "jboot.rpc.motan.service");
        if (serviceConfigs != null && !serviceConfigs.isEmpty()) {
            baseServiceConfigMap.putAll(serviceConfigs);
        }

        //methodConfig 配置
        Map<String, MethodConfig> methodConfigs = configs(MethodConfig.class, "jboot.rpc.motan.method");
        if (methodConfigs != null && !methodConfigs.isEmpty()) {
            methodConfigMap.putAll(methodConfigs);
        }


    }


    public static RefererConfig toRefererConfig(JbootrpcReferenceConfig rc) {
        RefererConfig referenceConfig = new RefererConfig();
        Utils.copyFields(rc, referenceConfig);
        return referenceConfig;
    }


    public static ServiceConfig toServiceConfig(JbootrpcServiceConfig sc) {
        ServiceConfig serviceConfig = new ServiceConfig();
        Utils.copyFields(sc, serviceConfig);
        return serviceConfig;
    }


    private static <T> Map<String, T> configs(Class<T> clazz, String prefix) {
        return JbootConfigUtil.getConfigModels(clazz, prefix);
    }
}
