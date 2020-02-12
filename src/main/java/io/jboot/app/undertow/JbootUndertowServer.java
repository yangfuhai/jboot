/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.app.undertow;

import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowServer;

import javax.servlet.ServletException;


public class JbootUndertowServer extends UndertowServer {

    public JbootUndertowServer(UndertowConfig undertowConfig) {
        super(undertowConfig);
    }

    @Override
    protected void init() {
        super.init();

        //让 undertow 支持 音视频在线播放
//        HttpContentTypes.init(deploymentInfo);
    }

    @Override
    protected void doStop() throws ServletException {
        super.doStop();
    }

}
