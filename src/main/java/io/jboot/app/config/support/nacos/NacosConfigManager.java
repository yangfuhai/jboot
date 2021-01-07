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
package io.jboot.app.config.support.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import io.jboot.app.config.ConfigUtil;
import io.jboot.app.config.JbootConfigManager;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.Properties;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/8
 */
public class NacosConfigManager {

    private static final NacosConfigManager ME = new NacosConfigManager();

    public static NacosConfigManager me() {
        return ME;
    }


    private Properties contentProperties;

    /**
     * 初始化 nacos 配置监听
     */
    public void init(JbootConfigManager configManager) {

        NacosServerConfig nacosServerConfig = configManager.get(NacosServerConfig.class);
        if (!nacosServerConfig.isEnable() || !nacosServerConfig.isConfigOk()) {
            return;
        }

        try {

            ConfigService configService = NacosFactory.createConfigService(nacosServerConfig.toProperties());
            String content = configService.getConfig(nacosServerConfig.getDataId()
                    , nacosServerConfig.getGroup(), 3000);

            if (ConfigUtil.isNotBlank(content)) {
                contentProperties = str2Properties(content);
                if (contentProperties != null) {
                    configManager.setRemoteProperties(contentProperties);
                }
            }

            new NacosConfigIniter(this, configManager).initListener(configService, nacosServerConfig);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 接收到 nacos 服务器消息
     * @param configManager
     * @param configInfo
     */
    public void onReceiveConfigInfo(JbootConfigManager configManager, String configInfo) {
        Properties properties = str2Properties(configInfo);
        if (contentProperties == null) {
            contentProperties = properties;
            configManager.setRemoteProperties(properties);
        } else {
            for (Object key : properties.keySet()) {
                String newValue = properties.getProperty(key.toString());
                String oldValue = contentProperties.getProperty(key.toString());

                if (!Objects.equals(newValue, oldValue)) {
                    contentProperties.put(key, newValue);
                    configManager.setRemoteProperty(key.toString(), newValue);

                    configManager.notifyChangeListeners(key.toString(),newValue,oldValue);
                }
            }
        }
    }


    private Properties str2Properties(String content) {
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(content));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
