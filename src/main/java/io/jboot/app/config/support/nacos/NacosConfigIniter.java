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

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;

import java.util.concurrent.Executor;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/9
 */
public class NacosConfigIniter {

    private NacosConfigManager manager;


    public NacosConfigIniter(NacosConfigManager manager) {
        this.manager = manager;
    }

    public void initListener(ConfigService configService, NacosServerConfig config) {
        try {
            configService.addListener(config.getDataId(), config.getGroup()
                    , new Listener() {
                        @Override
                        public Executor getExecutor() {
                            return null;
                        }

                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            manager.doReceiveConfigInfo(configInfo);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
