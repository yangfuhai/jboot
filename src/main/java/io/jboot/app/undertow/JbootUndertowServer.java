/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.alibaba.dubbo.config.DubboShutdownHook;
import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.server.undertow.hotswap.HotSwapResolver;

import javax.servlet.ServletException;

/**
 * 修复 dubbo 下热加载的问题
 */
public class JbootUndertowServer extends UndertowServer {

    protected JbootUndertowServer(UndertowConfig undertowConfig) {
        super(undertowConfig);
    }

    @Override
    protected void doStop() throws ServletException {
        super.doStop();
        HotSwapResolver resolver = getUndertowConfig().getHotSwapResolver();
        if (resolver.isHotSwapClass("org.apache.dubbo")
                || resolver.isHotSwapClass("com.alibaba")) {
            DubboShutdownHook.getDubboShutdownHook().destroyAll();
        }
    }
}
