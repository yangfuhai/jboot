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
package rpcconsul;

import io.jboot.Jboot;
import io.jboot.core.rpc.Jbootrpc;
import service.CategoryService;
import service.CategoryServiceImpl;
import service.UserService;
import service.UserServiceImpl;


public class MotanServerDemo {


    /**
     * 在执行main方法之前，请先启动 consul
     * 启动 consul 命令：./consul agent -dev
     * 正常启动：./consul agent -server -bootstrap -bind=0.0.0.0
     * @param args
     * @throws InterruptedException
     */

    public static void main(String[] args) throws InterruptedException {


        //RPC配置
        Jboot.setBootArg("jboot.rpc.type", "motan");
        Jboot.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
        Jboot.setBootArg("jboot.rpc.registryType", "consul");//注册中心的类型：consul
        Jboot.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:8500");//注册中心，即consul的地址


        Jboot.run(args);

        Jbootrpc factory = Jboot.me().getRpc();

        factory.serviceExport(UserService.class, new UserServiceImpl(), "jboot", "1.0", 8002);
        factory.serviceExport(CategoryService.class, new CategoryServiceImpl(), "jboot", "1.0", 8002);


        System.out.println("MotanServerDemo started...");


    }
}
