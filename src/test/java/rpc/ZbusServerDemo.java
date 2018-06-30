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
package rpc;

import io.jboot.Jboot;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.core.rpc.JbootrpcServiceConfig;
import service.CategoryService;
import service.CategoryServiceImpl;
import service.UserService;
import service.UserServiceImpl;


public class ZbusServerDemo {


    public static void main(String[] args) throws InterruptedException {


        Jboot.setBootArg("jboot.rpc.type", "zbus");
        Jboot.setBootArg("jboot.rpc.callMode", "registry");//注册中心模式
        Jboot.setBootArg("jboot.rpc.registryType", "zbus");//注册中心的类型：zbus
        Jboot.setBootArg("jboot.rpc.registryAddress", "127.0.0.1:15555");//注册中心，即zbus的地址
        Jboot.setBootArg("jboot.rpc.zbus.serviceName", "test");//zbus 必须填写serviceName



        Jbootrpc factory = Jboot.me().getRpc();

        factory.serviceExport(UserService.class, new UserServiceImpl(),  new JbootrpcServiceConfig());
        factory.serviceExport(CategoryService.class, new CategoryServiceImpl(),  new JbootrpcServiceConfig());

        Jboot.run(args);

        System.out.println("MotanServerDemo started...");


    }
}
