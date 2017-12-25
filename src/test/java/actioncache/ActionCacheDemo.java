/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package actioncache;

import io.jboot.Jboot;
import io.jboot.web.cache.ActionCacheClear;
import io.jboot.web.cache.EnableActionCache;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/actionCache")
public class ActionCacheDemo extends JbootController {


    public static void main(String[] args) {

        Jboot.run(args);
    }

    public void index() {
        System.out.println("index() invoke!!!!");
        renderHtml("index");
    }

    @ActionCacheClear("test")
    public void clear() {
        System.out.println("clear() invoke!!!!");
        renderHtml("clear ok!!!");

    }

    @EnableActionCache(group = "test#(id)")
    public void cache() {
        System.out.println("cache() invoke!!!!");
        renderHtml("render ok");
    }


    @EnableActionCache(group = "test#(id)")
    public void template() {
        System.out.println("template() invoke!!!!");
        renderTemplate("/test.html");
    }

    @EnableActionCache(group = "test")
    public void json() {
        System.out.println("json() invoke!!!!");
        setAttr("user", "Michael Yang");
        renderJson();
    }


    @EnableActionCache(group = "test", liveSeconds = 5)
    public void time() {
        System.out.println("json() invoke!!!!");
        setAttr("user", "Michael Yang");
        renderJson();
    }

}
