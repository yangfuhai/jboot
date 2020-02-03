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
package io.jboot.test.sentinel;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/3
 *
 * 使用方法：
 * 第一步，启动 sentinel dashboard
 *   java -jar sentinel-dashboard-1.7.1.jar
 *
 * 第二步：在 resource/sentinel.properties 配置相关信息
 */
@RequestMapping("/sentinel")
public class SentinelController extends JbootController {

    @SentinelResource
    public void index(){
        renderText("sentinel index...");
    }
}
