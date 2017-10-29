/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.config.web;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import io.jboot.Jboot;
import io.jboot.config.JbootConfigConfig;
import io.jboot.config.JbootConfigManager;
import io.jboot.config.PropInfos;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 配置文件的Controller，用于给其他应用提供分布式配置读取功能
 */
@Clear
@RequestMapping("/jboot/config")
@Before(JbootConfigInterceptor.class)
public class JbootConfigController extends JbootController {


    JbootConfigConfig config = Jboot.config(JbootConfigConfig.class);


    public void index() {
        String id = getPara();
        if (StringUtils.isBlank(id)) {
            renderJson(JbootConfigManager.me().getPropInfos());
            return;
        } else {
            PropInfos propInfos = JbootConfigManager.me().getPropInfos();
            for (PropInfos.Entry<String, PropInfos.PropInfo> entry : propInfos.entrySet()) {
                if (id.equals(entry.getKey())) {
                    renderJson(PropInfos.create(entry.getKey(), entry.getValue()));
                    return;
                }
            }
        }
        renderJson("{}");
    }


    /**
     * 列出本地目录下的文件信息
     */
    public void list() {
        List<HashMap<String, String>> props = new ArrayList<>();
        PropInfos propInfos = JbootConfigManager.me().getPropInfos();
        for (PropInfos.Entry<String, PropInfos.PropInfo> entry : propInfos.entrySet()) {
            HashMap<String, String> prop = new HashMap<>();
            prop.put("id", entry.getKey());
            prop.put("version", entry.getValue().getVersion());
            props.add(prop);
        }
        renderJson(props.toArray());
    }
}
