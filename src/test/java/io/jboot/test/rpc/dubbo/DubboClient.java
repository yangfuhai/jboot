/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.test.rpc.dubbo;


import io.jboot.app.JbootApplication;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.test.rpc.commons.BlogService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dubbo")
public class DubboClient extends JbootController {

    public static void main(String[] args) {


        //Undertow端口号配置
        JbootApplication.setBootArg("undertow.port", "9999");

        //RPC配置
        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", false);

        JbootApplication.setBootArg("jboot.rpc.dubbo.method.name", "findById");
        JbootApplication.setBootArg("jboot.rpc.dubbo.method.oninvoke", "callback.oninvoke");
        JbootApplication.setBootArg("jboot.rpc.dubbo.method.onthrow", "callback.onthrow");


        //设置直连模式，方便调试，默认为注册中心
        JbootApplication.setBootArg("jboot.rpc.urls", "io.jboot.test.rpc.commons.BlogService:127.0.0.1:28080");


        JbootApplication.run(args);
    }


    @RPCInject(timeout = 3300,  check = false)
    private BlogService blogService;

    //    @Before(DubboInterceptor.class)
    public void index() {

        System.out.println("blogService:" + blogService);

        renderText("blogId : " + blogService.findById());
    }


}
