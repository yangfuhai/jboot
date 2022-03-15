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
package io.jboot.app.config.support.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.jfinal.log.Log;
import io.jboot.app.config.JbootConfigKit;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.utils.ConfigUtil;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/8
 */
public class NacosConfigManager {

    private static final Log LOG = Log.getLog(NacosConfigManager.class);
    private static final NacosConfigManager ME = new NacosConfigManager();

    public static NacosConfigManager me() {
        return ME;
    }


    private Properties contentProperties;

    /**
     * 初始化 nacos 配置监听
     */
    public void init(JbootConfigManager configManager) {
        Map<String, NacosServerConfig> configModels = ConfigUtil.getConfigModels(configManager, NacosServerConfig.class);
        configModels.forEach((s, nacosConfig) -> initConfig(configManager, nacosConfig));
    }

    private void initConfig(JbootConfigManager configManager, NacosServerConfig nacosServerConfig) {
        if (nacosServerConfig == null
                || !nacosServerConfig.isEnable()
                || !nacosServerConfig.isConfigOk()) {
            return;
        }

        try {

            ConfigService configService = NacosFactory.createConfigService(nacosServerConfig.toProperties());
            String content = configService.getConfig(nacosServerConfig.getDataId()
                    , nacosServerConfig.getGroup(), 3000);

            if (JbootConfigKit.isNotBlank(content)) {
                contentProperties = str2Properties(content);
                if (contentProperties != null) {
                    configManager.setRemoteProperties(contentProperties);
                }
            }

            new NacosConfigInitializer(this, configManager).initListener(configService, nacosServerConfig);

        } catch (Exception e) {

            LOG.error(e.toString(), e);
        }
    }


    /**
     * 接收到 nacos 服务器消息
     *
     * @param configManager
     * @param configInfo
     */
    public void onReceiveConfigInfo(JbootConfigManager configManager, String configInfo) {
        Properties properties = str2Properties(configInfo);
        if (properties != null) {
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

                        configManager.notifyChangeListeners(key.toString(), newValue, oldValue);
                    }
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
