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
package io.jboot.app.config.support.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import io.jboot.Jboot;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.utils.StrUtil;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

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
    public void init() {

        NacosServerConfig nacosServerConfig = Jboot.config(NacosServerConfig.class);
        if (!nacosServerConfig.isEnable() || !nacosServerConfig.isConfigOk()) {
            return;
        }

        try {

            ConfigService configService = NacosFactory.createConfigService(nacosServerConfig.toProperties());
            String content = configService.getConfig(nacosServerConfig.getDataId()
                    , nacosServerConfig.getGroup(), 3000);

            if (StrUtil.isNotBlank(content)) {
                contentProperties = str2Properties(content);
                if (contentProperties != null) {
                    JbootConfigManager.me().setRemoteProperties(contentProperties);
                }
            }

            new NacosConfigIniter(this).initListener(configService, nacosServerConfig);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 接收的 nacos 服务器消息
     *
     * @param configInfo
     */
    public void doReceiveConfigInfo(String configInfo) {
        Properties properties = str2Properties(configInfo);
        Set<String> changedKeys = new HashSet<>();
        if (contentProperties == null) {
            contentProperties = properties;

            for (Object key : properties.keySet()) {
                changedKeys.add(key.toString());
            }

            JbootConfigManager.me().setRemoteProperties(properties);
            JbootConfigManager.me().notifyChangeListeners(changedKeys);

        } else {

            for (Object key : properties.keySet()) {
                String newValue = properties.getProperty(key.toString());
                String oldValue = contentProperties.getProperty(key.toString());

                if (!Objects.equals(newValue, oldValue)) {
                    changedKeys.add(key.toString());
                    contentProperties.put(key, newValue);
                    JbootConfigManager.me().setRemoteProperty(key.toString(), newValue);
                }
            }

            JbootConfigManager.me().notifyChangeListeners(changedKeys);
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
