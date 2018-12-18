/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.core.http;

import io.jboot.Jboot;
import io.jboot.core.http.jboot.JbootHttpImpl;
import io.jboot.core.http.okhttp.OKHttpImpl;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.kits.ClassKits;

public class JbootHttpManager {

    private static JbootHttpManager manager;


    public static JbootHttpManager me() {
        if (manager == null) {
            manager = ClassKits.singleton(JbootHttpManager.class);
        }
        return manager;
    }


    private JbootHttp jbootHttp;

    public JbootHttp getJbootHttp() {
        if (jbootHttp == null) {
            jbootHttp = buildJbootHttp();
        }
        return jbootHttp;
    }


    private JbootHttp buildJbootHttp() {
        JbootHttpConfig config = Jboot.config(JbootHttpConfig.class);

        switch (config.getType()) {
            case JbootHttpConfig.TYPE_DEFAULT:
                return new JbootHttpImpl();
            case JbootHttpConfig.TYPE_OKHTTP:
                return new OKHttpImpl();
            case JbootHttpConfig.TYPE_HTTPCLIENT:
                throw new RuntimeException("not finished!!!!");
            default:
                return JbootSpiLoader.load(JbootHttp.class, config.getType());
        }

    }


}
