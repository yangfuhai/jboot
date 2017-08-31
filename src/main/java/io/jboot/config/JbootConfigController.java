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
package io.jboot.config;

import com.jfinal.aop.Clear;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.Ret;
import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Arrays;

/**
 * 配置文件的Controller，用于给其他应用提供分布式配置读取功能
 */
@Clear
@RequestMapping("/jboot/config")
public class JbootConfigController extends JbootController {


    JbootConfigConfig config = Jboot.config(JbootConfigConfig.class);


    public void index(@Para(value = "propertie", defaultValue = "jboot.properties") String propertie, String prefix) {

        if (!config.isConfigServer()) {
            renderJson(Ret.fail("msg", "sorry,  you have no permission to visit this page. "));
            return;
        }
        
    }


    /**
     * 列出本地目录下的文件信息
     */
    public void list() {
        if (!config.isConfigServer()) {
            renderJson(Ret.fail("msg", "sorry,  you have no permission to visit this page. "));
            return;
        }

        renderJson(Arrays.toString(config.getConfigFile().split(
                ","
        )));

    }
}
