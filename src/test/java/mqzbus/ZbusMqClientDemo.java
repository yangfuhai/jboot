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
package mqzbus;

import io.jboot.Jboot;
import io.jboot.core.mq.JbootmqMessageListener;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/zbusmq")
public class ZbusMqClientDemo extends JbootController {


    private static String message;

    /**
     * 请先启动 MotanServerDemo 后，再启动
     *
     * @param args
     */
    public static void main(String[] args) {

        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");

        //RPC配置
        Jboot.setBootArg("jboot.mq.type", "zbus");
        Jboot.setBootArg("jboot.mq.zbus.broker", "127.0.0.1:15555");
        Jboot.setBootArg("jboot.mq.zbus.channel", "myChannel,myChannel1,myChannel2");

        Jboot.me().getMq().addMessageListener(new JbootmqMessageListener() {
            @Override
            public void onMessage(String channel, Object message) {
                System.out.println("listener:" + message + "     channel:" + channel);
                ZbusMqClientDemo.message = (String) message;
            }
        });

        Jboot.run(args);
    }


    public void index() {

        renderText("message:" + message);
    }


}
