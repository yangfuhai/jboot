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
import io.jboot.core.rpc.Jbootrpc;
import service.CategoryService;
import service.CategoryServiceImpl;
import service.UserService;
import service.UserServiceImpl;


public class DubboServerDemo {


    public static void main(String[] args) throws InterruptedException {


        Jboot.setBootArg("jboot.rpc.type", "dubbo");
        Jboot.setBootArg("jboot.rpc.callMode", "redirect");//直连模式，默认为注册中心
        Jboot.setBootArg("jboot.rpc.directUrl", "localhost:8002");//直连模式的url地址


        Jboot.run(args);

        Jbootrpc factory = Jboot.me().getRpc();

        factory.serviceExport(UserService.class, new UserServiceImpl(), "jboot", "1.0", 8002);
        factory.serviceExport(CategoryService.class, new CategoryServiceImpl(), "jboot", "1.0", 8002);


        System.out.println("DubboServerDemo started...");


    }
}
