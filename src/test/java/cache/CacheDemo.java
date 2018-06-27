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
package cache;

import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.inject.Inject;


@RequestMapping("/cache")
public class CacheDemo extends JbootController {

    @Inject
    private CacheService service;

    public static void main(String[] args)  {

        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");
//        Jboot.setBootArg("jboot.cache.type", "redis");
//        Jboot.setBootArg("jboot.redis.host", "127.0.0.1");


        Jboot.run(args);
    }

    public void enable() {
        String key = getPara("key","mykey");
        String result = service.cacheAble(key);
        renderText(result);

        // @Cacheable(name = "mycache", key = "#(key)")
//        Jboot.me().getCache().get("mycache", key, new IDataLoader() {
//            @Override
//            public Object load() {
//                return service.cacheAble(key);
//            }
//        });
    }

    public void enableLive() {
        String result = service.cacheAbleLive("mykey1");
        renderText(result);
    }

    public void putLive() {
        String result = service.putCache("mykey");
        renderText(result);
    }

    public void evict() {
        service.cacheEvict("mykey");
        renderText("evict ok");
    }
    public void evict1() {
        service.cacheEvict1("mykey");
        renderText("evict ok");
    }

}
