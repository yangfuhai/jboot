/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package limitation;

import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.limitation.LimitRenderType;
import io.jboot.web.limitation.annotation.EnableIpRateLimit;
import io.jboot.web.limitation.annotation.EnableRequestRateLimit;
import io.jboot.web.limitation.annotation.EnableUserRateLimit;


@RequestMapping("/limitation")
public class LimitationDemo extends JbootController {


    public static void main(String[] args) {
        Jboot.run(args);
    }


    public void index() {
        renderText("render ok");
    }

    /**
     * 所有的请求，每1秒钟只能访问一次
     */
    @EnableRequestRateLimit(rate = 1)
    public void request() {
        renderText("request() render ok");
    }

    /**
     * 所有的请求，每1秒钟只能访问一次
     * 被限制的请求，自动跳转到 /limitation/request2
     */
    @EnableRequestRateLimit(rate = 1, renderType = LimitRenderType.REDIRECT, renderContent = "/limitation/request2")
    public void request1() {
        renderText("request1() render ok");
    }


    public void request2() {
        renderText("request2() render ok");
    }


    /**
     * 每个用户，每5秒钟只能访问一次
     */
    @EnableUserRateLimit(rate = 0.2)
    public void user() {
        renderText("user() render ok");
    }

    /**
     * 每个用户，每5秒钟只能访问一次
     * 被限制的请求，渲染文本内容 "被限制啦"
     */
    @EnableUserRateLimit(rate = 0.2, renderType = LimitRenderType.TEXT, renderContent = "被限制啦")
    public void user1() {
        renderText("user1() render ok");
    }


    /**
     * 每个IP地址，每5秒钟只能访问一次
     */
    @EnableIpRateLimit(rate = 0.2)
    public void ip() {
        renderText("ip() render ok");
    }


}
