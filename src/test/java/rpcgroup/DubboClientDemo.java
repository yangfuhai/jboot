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
package rpcgroup;

import io.jboot.Jboot;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import service.UserService;


@RequestMapping("/dubborpcgroup")
public class DubboClientDemo extends JbootController {


    /**
     * 请先启动 DubboServerDemo 后，再启动
     * @param args
     */
    public static void main(String[] args) {


        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");

        //RPC配置
        Jboot.setBootArg("jboot.rpc.type", "dubbo");
        Jboot.setBootArg("jboot.rpc.callMode", "redirect");//直连模式，默认为注册中心
        Jboot.setBootArg("jboot.rpc.directUrl", "localhost:8002");//直连模式的url地址

        Jboot.run(args);
    }


    public void index() {
        Jbootrpc jbootrpc = Jboot.me().getRpc();

        UserService service1 = jbootrpc.serviceObtain(UserService.class, "mygroup", "1.0");
        UserService service2 = jbootrpc.serviceObtain(UserService.class, "jbobbot", "1.0");

        // 使用服务
        System.out.println(service1.hello("service1" ));

        //这个会抛异常，因为没有叫jbobbot的组
        System.out.println(service2.hello("service2" ));


        renderText("ok");
    }


}
