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
package rpczookeeper;

import io.jboot.Jboot;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import service.UserService;


@RequestMapping("/dubbozookeeperrpc")
public class DubboClientZookeeperDemo extends JbootController {


    /**
     * 请先启动 DubboServer1ZookeeperDemo 后，再启动
     * @param args
     */
    public static void main(String[] args)  {

        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");

        //RPC配置
        Jboot.setBootArg("jboot.rpc.type", "dubbo");
        Jboot.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
        Jboot.setBootArg("jboot.rpc.registryType", "zookeeper");//注册中心的类型：zookeeper
        Jboot.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:2181");//注册中心，即zookeeper的地址

        Jboot.run(args);
    }


    public void index() {
        Jbootrpc jbootrpc = Jboot.me().getRpc();

        long time = System.currentTimeMillis();
        UserService service = jbootrpc.serviceObtain(UserService.class, "jboot", "1.0");
        System.out.println("obtain:" + (System.currentTimeMillis() - time) + "---" + service);


        for (int i = 0; i < 10; i++) {
            // 使用服务
            System.out.println(service.hello("海哥" + i));
        }


        renderText("ok");
    }


}
