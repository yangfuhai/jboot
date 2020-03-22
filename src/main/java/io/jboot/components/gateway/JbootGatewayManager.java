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
package io.jboot.components.gateway;

import io.jboot.app.config.JbootConfigUtil;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/21
 */
public class JbootGatewayManager {

    private static JbootGatewayManager me = new JbootGatewayManager();

    public static JbootGatewayManager me() {
        return me;
    }

    private Set<JbootGatewayConfig> configs;

    public void init() {
        Map<String, JbootGatewayConfig> configMap = JbootConfigUtil.getConfigModels(JbootGatewayConfig.class, "jboot.gateway");
        if (configMap != null && !configMap.isEmpty()) {
            configs = new HashSet<>();
            for (Map.Entry<String, JbootGatewayConfig> e : configMap.entrySet()) {
                JbootGatewayConfig config = e.getValue();
                if (config.isConfigOk() && config.isEnable()) {
                    if (StrUtil.isNotBlank(config.getName())) {
                        config.setName(e.getKey());
                    }
                    configs.add(config);
                }
            }
        }
    }


    public JbootGatewayConfig matchingConfig(HttpServletRequest req) {
        if (configs != null && !configs.isEmpty()) {
            Iterator<JbootGatewayConfig> iterator = configs.iterator();
            while (iterator.hasNext()) {
                JbootGatewayConfig config = iterator.next();
                if (config.matches(req)) {
                    return config;
                }
            }
        }
        return null;
    }


}
