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


public class ZbusMqServerDemo {


    public static void main(String[] args) throws InterruptedException {


        Jboot.setBootArg("jboot.mq.type", "zbus");
        Jboot.setBootArg("jboot.mq.channel", "myChannel,myChannel1,myChannel2");

        Jboot.setBootArg("jboot.mq.zbus.broker", "127.0.0.1:15555");

        Jboot.run(args);

        System.out.println("ZbusMqServerDemo started...");


        int i = 0;
        for (; ; ) {
            Jboot.me().getMq().publish("message" + (i++), "myChannel1");
            Thread.sleep(1000);
        }


    }
}
