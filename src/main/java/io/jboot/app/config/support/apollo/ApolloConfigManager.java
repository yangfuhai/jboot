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
package io.jboot.app.config.support.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import io.jboot.app.config.JbootConfigKit;
import io.jboot.app.config.JbootConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/8
 */
public class ApolloConfigManager {

    private static final ApolloConfigManager ME = new ApolloConfigManager();

    public static ApolloConfigManager me() {
        return ME;
    }

    public void init(JbootConfigManager configManager) {

        ApolloServerConfig apolloServerConfig = configManager.get(ApolloServerConfig.class);
        if (!apolloServerConfig.isEnable() || !apolloServerConfig.isConfigOk()) {
            return;
        }

        //apollo 配置
        System.setProperty("app.id", apolloServerConfig.getAppId());
        System.setProperty("apollo.meta", apolloServerConfig.getMeta());


        Config config = getDefaultConfig(configManager);

        Set<String> propNames = config.getPropertyNames();
        if (propNames != null && !propNames.isEmpty()) {
            Map properties = new HashMap();
            for (String name : propNames) {
                String value = config.getProperty(name, null);
                properties.put(name,value);
            }
            configManager.setRemoteProperties(properties);

        }

        config.addChangeListener(changeEvent -> {
            for (String key : changeEvent.changedKeys()) {
                ConfigChange change = changeEvent.getChange(key);
                if (change.getChangeType() == PropertyChangeType.DELETED) {
                    configManager.removeRemoteProperty(change.getPropertyName());
                } else {
                    configManager.setRemoteProperty(change.getPropertyName(), change.getNewValue());
                }
                configManager.notifyChangeListeners(change.getPropertyName(), change.getNewValue(), change.getOldValue());
            }
        });


    }

    private Config getDefaultConfig(JbootConfigManager configManager) {
        ApolloServerConfig apolloServerConfig = configManager.get(ApolloServerConfig.class);
        if (JbootConfigKit.isNotBlank(apolloServerConfig.getDefaultNamespace())) {
            return ConfigService.getConfig(apolloServerConfig.getDefaultNamespace());
        } else {
            return ConfigService.getAppConfig();
        }
    }
}
