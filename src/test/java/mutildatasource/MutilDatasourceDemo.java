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
package mutildatasource;

import io.jboot.Jboot;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import sharding.UserModel;


@RequestMapping("/mds")
public class MutilDatasourceDemo extends JbootController {


    public static void main(String[] args) {
        Jboot.setBootArg("jboot.datasource.type", "mysql");
        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootsharding");
        Jboot.setBootArg("jboot.datasource.user", "root");
        Jboot.setBootArg("jboot.datasource.password", "");

        Jboot.setBootArg("jboot.datasource.a1.type", "mysql");
        Jboot.setBootArg("jboot.datasource.a1.url", "jdbc:mysql://127.0.0.1:3306/jbootsharding");
        Jboot.setBootArg("jboot.datasource.a1.user", "root");
        Jboot.setBootArg("jboot.datasource.a1.password", "");

        Jboot.run(args);
    }


    public void index() {
        String id = StrUtils.uuid();

        UserModel user = new UserModel();
        user.setName("Michael yang");
        user.setId(id);

        //增加
        user.save();

        System.out.println("保存成功！");

        user.setName("fuhai yang");

        //更新
        user.update();

        //试用其他数据源查询
        UserModel findModel = new UserModel().use("a1").findById(id);
        renderJson(findModel);
    }



}
