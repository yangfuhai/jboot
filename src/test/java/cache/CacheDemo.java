/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package cache;

import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


@RequestMapping("/cache")
public class CacheDemo extends JbootController {

    @Inject
    private CacheService service;

    public static void main(String[] args)  {

        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");
        Jboot.setBootArg("jboot.cache.type", "ehredis");
        Jboot.setBootArg("jboot.cache.redis.host", "127.0.0.1");
        Jboot.setBootArg("jboot.cache.redis.password", "123456");

        Jboot.setBootArg("jboot.mq.type", "redis");
        Jboot.setBootArg("jboot.mq.redis.host", "127.0.0.1");
        Jboot.setBootArg("jboot.mq.redis.password", "123456");
        Jboot.setBootArg("jboot.mq.redis.channel", "message-channel");
        Jboot.run(args);
    }

    public void enable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("abc", "enable");
        String result = service.cacheAble("key", map);
        renderText(result);
    }

    public void enableLive() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("abc", "live");
        String result = service.cacheAble("key", map);
        renderText(result);
    }

    public void putLive() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("abc", "put");
        String result = service.putCache("key", map);
        renderText(result);
    }

    public void evict() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("abc", "enable");
        service.cacheEvict("key", map);
        renderText("ok");
    }

}
