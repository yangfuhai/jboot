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
package mqqpid;

import io.jboot.Jboot;


public class QpidMqServerDemo {


    public static void main(String[] args) throws InterruptedException {

        Jboot.setBootArg("jboot.mq.type", "qpid");
        Jboot.setBootArg("jboot.mq.channel", "qpid");
        Jboot.setBootArg("jboot.mq.qpid.username", "admin");
        Jboot.setBootArg("jboot.mq.qpid.password", "admin");
        Jboot.setBootArg("jboot.mq.qpid.host", "10.140.22.227:5672,10.140.22.226:5672,10.140.22.225:5672");
        Jboot.setBootArg("jboot.mq.qpid.virtualHost", "my_group");

        Jboot.run(args);

        System.out.println("QpidMqServerDemo started...");

        int i = 0;
        for (; ; ) {
            Jboot.me().getMq().publish("message" + (i++), "qpid");
            Thread.sleep(1000);
        }
    }
}
