/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.core.config.server;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import io.jboot.Jboot;
import io.jboot.core.config.JbootConfigConfig;
import io.jboot.core.config.JbootConfigManager;
import io.jboot.core.config.PropInfoMap;
import io.jboot.kits.StrUtils;
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
        if (StrUtils.isBlank(id)) {
            renderJson(JbootConfigManager.me().getPropInfoMap());
            return;
        } else {
            PropInfoMap propInfos = JbootConfigManager.me().getPropInfoMap();
            for (PropInfoMap.Entry<String, PropInfoMap.PropInfo> entry : propInfos.entrySet()) {
                if (id.equals(entry.getKey())) {
                    renderJson(PropInfoMap.create(entry.getKey(), entry.getValue()));
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
        PropInfoMap propInfos = JbootConfigManager.me().getPropInfoMap();
        for (PropInfoMap.Entry<String, PropInfoMap.PropInfo> entry : propInfos.entrySet()) {
            HashMap<String, String> prop = new HashMap<>();
            prop.put("id", entry.getKey());
            prop.put("version", entry.getValue().getVersion());
            props.add(prop);
        }
        renderJson(props.toArray());
    }
}
