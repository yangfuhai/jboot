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
package opentracing;

import io.jboot.Jboot;
import io.jboot.component.opentracing.EnableTracing;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.core.rpc.JbootrpcServiceConfig;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import service.CategoryService;
import service.UserService;


@RequestMapping("/opentracing")
public class ClientDemo extends JbootController {


    /**
     * 请先启动 ServerDemo 后，再启动
     * 然后通过 http://127.0.0.1:8088/opentracing 访问生产数据
     *
     * @param args
     */

    public static void main(String[] args) {

        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");

        Jboot.setBootArg("jboot.rpc.type", "motan");
        Jboot.setBootArg("jboot.rpc.callMode", "redirect");//直连模式，默认为注册中心
        Jboot.setBootArg("jboot.rpc.directUrl", "localhost:8000");//直连模式的url地址


        Jboot.setBootArg("jboot.tracing.type", "zipkin");//opentracing的类型
        Jboot.setBootArg("jboot.tracing.serviceName", "ClientDemo");//opentracing的本应用服务名称
        Jboot.setBootArg("jboot.tracing.url", "http://127.0.0.1:9411/api/v2/spans");//zipkin的服务器

        Jboot.run(args);
    }

    @EnableTracing
    public void index() {
        Jbootrpc jbootrpc = Jboot.me().getRpc();

        long time = System.currentTimeMillis();
        UserService service = jbootrpc.serviceObtain(UserService.class, new JbootrpcServiceConfig());
        System.out.println("obtain:" + (System.currentTimeMillis() - time) + "---" + service);


        System.out.println(service.hello("海哥"));

        CategoryService service1 = Jboot.service(CategoryService.class);
        System.out.println(service1.hello("海哥"));


        renderText("ok");
    }


}
