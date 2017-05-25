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
package rpc;

import io.jboot.Jboot;
import io.jboot.rpc.Jbootrpc;

/**
 * Created by michael on 2017/5/5.
 */
public class RPCClientDemo {


    public static void main(String[] args) throws InterruptedException {

        Jbootrpc factory = Jboot.getRpc();

        long time = System.currentTimeMillis();
        ITestRpcService service = factory.serviceObtain(ITestRpcService.class, "jboot", "1.0");
        ITest1RpcService service1 = factory.serviceObtain(ITest1RpcService.class, "jboot", "1.0");

//        // 使用服务
        System.out.println("obtain:"+(System.currentTimeMillis()-time)+"---"+service);

        for (int i = 0; i < 10; i++) {
            System.out.println(service.hello("海哥" + i));
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(service1.hello("海哥" + i));
        }


        System.exit(0);

    }
}
