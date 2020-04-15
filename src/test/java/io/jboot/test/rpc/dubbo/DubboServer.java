/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.test.rpc.dubbo;


import io.jboot.app.JbootApplication;
import io.jboot.app.JbootSimpleApplication;

public class DubboServer {

    public static void main(String[] args) throws InterruptedException {


        JbootApplication.setBootArg("jboot.rpc.type", "dubbo");

        // 开启 @RPCBean 自动暴露功能，默认情况下是开启的，无需配置，
        // 但是此测试代码的 jboot.properties 文件关闭了，这里需要开启下
        JbootApplication.setBootArg("jboot.rpc.autoExportEnable", true);

        //dubbo 的通信协议配置
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.name", "dubbo");
        JbootApplication.setBootArg("jboot.rpc.dubbo.protocol.port", "28080");


        JbootSimpleApplication.run(args);

        System.out.println("DubboServer started...");

    }
}
