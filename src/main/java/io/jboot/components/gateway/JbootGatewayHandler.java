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
package io.jboot.components.gateway;

import com.jfinal.handler.Handler;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/22
 */
public class JbootGatewayHandler extends Handler {


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        JbootGatewayConfig config = JbootGatewayManager.me().matchingConfig(request);
        if (config != null) {
            doProcessGateway(config, request, response);
            isHandled[0] = true;
        } else {
            next.handle(target, request, response, isHandled);
        }
    }

    private void doProcessGateway(JbootGatewayConfig config, HttpServletRequest request, HttpServletResponse response) {
        Runnable runnable = () -> {
            StringBuilder url = new StringBuilder(config.getUri());
            if (StrUtil.isNotBlank(request.getRequestURI())) {
                url.append(request.getRequestURI());
            }
            if (StrUtil.isNotBlank(request.getQueryString())) {
                url.append("?").append(request.getQueryString());
            }

            new GatewayHttpProxy(config).sendRequest(url.toString(), request, response);
        };

        if (config.isSentinelEnable()) {
            new GatewaySentinelProcesser().process(runnable, config, request, response);
        } else {
            runnable.run();
        }
    }
}
