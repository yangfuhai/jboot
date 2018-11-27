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
package arp;

import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/arp")
public class ArpDemo extends JbootController {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.datasource.type", "mysql");
        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jboot");
        Jboot.setBootArg("jboot.datasource.user", "root");
        Jboot.setBootArg("jboot.datasource.password", "");
        Jboot.setBootArg("jboot.datasource.activeRecordPluginClass", "arp.MyPlugin");



        Jboot.run(args);
    }


    public void index() {
        renderText("ok");
    }



}
