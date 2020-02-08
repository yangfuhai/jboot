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
package io.jboot.app.config.support.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import io.jboot.Jboot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/8
 */
public class NacosConfigManager implements Listener {

    private static final NacosConfigManager ME = new NacosConfigManager();

    public static NacosConfigManager me() {
        return ME;
    }


    private ConfigService configService;

    public void init() {

        NacosServerConfig nacosServerConfig = Jboot.config(NacosServerConfig.class);
        if (!nacosServerConfig.isEnable() || !nacosServerConfig.isConfigOk()) {
            return;
        }


        Properties properties = new Properties();
        properties.put("serverAddr", nacosServerConfig.getServerAddr());
        try {
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            e.printStackTrace();
        }

        if (configService != null) {
            try {
                String content = configService.getConfig(nacosServerConfig.getDataId()
                        , nacosServerConfig.getGroup(), 5000);

                configService.addListener(nacosServerConfig.getDataId()
                        , nacosServerConfig.getGroup()
                        , this);


            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
    }


    private Properties str2Properties(String content) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(content.getBytes()));
        return properties;
    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void receiveConfigInfo(String configInfo) {

    }
}
