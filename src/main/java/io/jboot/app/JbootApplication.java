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
package io.jboot.app;

import com.jfinal.server.undertow.UndertowServer;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.undertow.JbootUndertowConfig;

public class JbootApplication {

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {

        JbootConfigManager.me().parseArgs(args);

        JbootApplicationConfig applicationConfig = JbootConfigManager.me().get(JbootApplicationConfig.class);
        JbootUndertowConfig undertowConfig = new JbootUndertowConfig(applicationConfig.getJfinalConfig());

        printBannerInfo(applicationConfig);

        UndertowServer.create(undertowConfig).start();
    }


    private static void printBannerInfo(JbootApplicationConfig applicationConfig) {
        if (!applicationConfig.isBannerEnable()) {
            return ;
        }

        System.out.println(JbootApplicationKits.getBannerText(applicationConfig.getBannerFile()));
    }




    public static void setBootArg(String key, Object value) {
        JbootConfigManager.me().setBootArg(key, value.toString());
    }


}
