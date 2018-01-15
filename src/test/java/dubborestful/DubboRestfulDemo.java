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
package dubborestful;

import io.jboot.Jboot;
import io.jboot.core.rpc.Jbootrpc;


public class DubboRestfulDemo {


    /**
     * 使用 dubbo 的 restful ，请配置 jboot.rpc.dubbo.protocolName 为 rest
     * 具体文档：http://dangdangdotcom.github.io/dubbox/rest.html
     * <p>
     * 针对某个Service 单独暴露 restful 的配置晚点支持，目前针对单个 Service，
     * 目前可以通过 父子系统的方式来实现，父子系统通信走RPC，子系统对外暴露 restful
     * <p>
     * 运行 main() 方法后，访问： http://127.0.0.1:8002/users/get 来查看效果
     * <p>
     * 具体代码在： UserServiceImpl 里
     *
     * @param args
     */
    public static void main(String[] args) {


        Jboot.setBootArg("jboot.rpc.type", "dubbo");
        Jboot.setBootArg("jboot.rpc.callMode", "redirect");//直连模式，默认为注册中心
        Jboot.setBootArg("jboot.rpc.directUrl", "localhost:8002");//直连模式的url地址

        Jboot.setBootArg("jboot.rpc.dubbo.protocolName", "rest");
//        Jboot.setBootArg("jboot.rpc.dubbo.protocolServer","netty"); //netty tomcat jetty
//        Jboot.setBootArg("jboot.rpc.dubbo.protocolContextPath","/mypath");


        Jboot.run(args);

        Jbootrpc factory = Jboot.me().getRpc();

        factory.serviceExport(UserService.class, new UserServiceImpl(), "jboot", "1.0", 8002);


        System.out.println("server started...");


    }
}
