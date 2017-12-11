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
package hystrixexception;

import io.jboot.Jboot;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import service.UserService;


@RequestMapping("/hystrixex")
public class ClientDemo extends JbootController {


    /**
     * 请先启动 ServerDemo 后，再启动
     *
     * @param args
     */
    public static void main(String[] args) {

        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");

        //RPC 调用配置
        Jboot.setBootArg("jboot.rpc.type", "motan");
        Jboot.setBootArg("jboot.rpc.callMode", "redirect");//直连模式，默认为注册中心
        Jboot.setBootArg("jboot.rpc.directUrl", "localhost:8002");//直连模式的url地址
        Jboot.setBootArg("jboot.rpc.hystrixTimeout", "10");//直连模式的url地址
//        Jboot.setBootArg("jboot.rpc.hystrixEnable", false);//直连模式的url地址
//        Jboot.setBootArg("jboot.rpc.hystrixFallbackListener", MyHystrixFallbackListener.class.getName());//直连模式的url地址

        Jboot.run(args);
    }


    public void index() {
        Jbootrpc jbootrpc = Jboot.me().getRpc();

        UserService service = jbootrpc.serviceObtain(UserService.class, "jboot", "1.0");


        System.out.println(service.hello("海哥"));


        renderText("ok");
    }


}
