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
package cache;

import io.jboot.Jboot;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import service.UserService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


@RequestMapping("/cache")
public class CacheDemo extends JbootController {

    @Inject
    private CacheService service;

    /**
     * 请先启动 MotanServerDemo 后，再启动
     * @param args
     */
    public static void main(String[] args)  {

        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");
        Jboot.setBootArg("jboot.cache.type", "redis");
        Jboot.setBootArg("jboot.cache.redis.host", "127.0.0.1");
        Jboot.setBootArg("jboot.cache.redis.password", "123456");
        Jboot.run(args);
    }

    public void index() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("abc", "cba");
        String result = service.cacheKeyTest("tttttt", map);
        renderText(result);
    }

    public void index2() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("abc", "cbaaa");
        String result = service.cacheKeyTest("tttttt", map);
        renderText(result);
    }


}
