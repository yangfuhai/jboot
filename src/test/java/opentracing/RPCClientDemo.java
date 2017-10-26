/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package opentracing;

import io.jboot.Jboot;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * Created by michael on 2017/5/5.
 */
@RequestMapping("/opentracing")
public class RPCClientDemo extends JbootController {

    public static void main(String[] args) throws InterruptedException {

        Jboot.setBootArg("jboot.server.port", "8088");
        Jboot.setBootArg("jboot.rpc.type", "motan");
        Jboot.setBootArg("jboot.rpc.callMode", "redirect");//直连模式，默认为注册中心
        Jboot.setBootArg("jboot.rpc.directUrl", "localhost:8002");//直连模式的url地址


        Jboot.setBootArg("jboot.tracing.type", "zipkin");//直连模式，默认为注册中心
        Jboot.setBootArg("jboot.tracing.serviceName", "RPCClientDemo");//直连模式，默认为注册中心
        Jboot.setBootArg("jboot.tracing.url", "http://127.0.0.1:9411/api/v2/spans");//直连模式，默认为注册中心
        Jboot.run(args);


    }


    public void index() {
        Jbootrpc factory = Jboot.me().getRpc();

        long time = System.currentTimeMillis();
        ITestRpcService service = factory.serviceObtain(ITestRpcService.class, "jboot", "1.0");
        ITest1RpcService service1 = factory.serviceObtain(ITest1RpcService.class, "jboot", "1.0");

//        // 使用服务
        System.out.println("obtain:" + (System.currentTimeMillis() - time) + "---" + service);

        for (int i = 0; i < 10; i++) {
            System.out.println(service.hello("海哥" + i));
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(service1.hello("海哥" + i));
        }


        renderText("ok");
    }



}
