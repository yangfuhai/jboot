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
package io.jboot.web.handler;

import com.jfinal.handler.Handler;
import io.jboot.Jboot;
import io.jboot.component.hystrix.JbootHystrixConfig;
import io.jboot.component.metric.JbootMetricConfig;
import io.jboot.web.websocket.JbootWebsocketManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class JbootFilterHandler extends Handler {


    private static JbootMetricConfig metricsConfig = Jboot.config(JbootMetricConfig.class);
    private static JbootHystrixConfig hystrixConfig = Jboot.config(JbootHystrixConfig.class);

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (target.indexOf('.') != -1 //static files
                || JbootWebsocketManager.me().isWebsokcetEndPoint(target) //websocket
                || (metricsConfig.isConfigOk() && target.startsWith(metricsConfig.getUrl())) // metrics
                || (hystrixConfig.isConfigOk() && target.startsWith(hystrixConfig.getUrl()))) // hystrix
        {
            return;
        }


        next.handle(target, request, response, isHandled);

    }


}
