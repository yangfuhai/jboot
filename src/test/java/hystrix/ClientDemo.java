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
package hystrix;

import io.jboot.Jboot;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import service.UserService;


@RequestMapping("/hystrixtest")
public class ClientDemo extends JbootController {


    /**
     * 请先启动 ServerDemo 后，再启动
     * 然后通过 http://127.0.0.1:8088/hystrixtest 访问生产数据
     * 然后通过 Hystrix Dashboard 来查看数据，
     * 具体文档请看 ：https://gitee.com/fuhai/jboot/blob/master/DOC.md#%E5%AE%B9%E9%94%99%E4%B8%8E%E9%9A%94%E7%A6%BB
     * @param args
     */
    public static void main(String[] args) {

        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");

        //RPC 调用配置
        Jboot.setBootArg("jboot.rpc.type", "motan");
        Jboot.setBootArg("jboot.rpc.callMode", "redirect");//直连模式，默认为注册中心
        Jboot.setBootArg("jboot.rpc.directUrl", "localhost:8002");//直连模式的url地址

        //hystrix配置
        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.html");//配置 Hystrix Dashboard 的监控路径

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
