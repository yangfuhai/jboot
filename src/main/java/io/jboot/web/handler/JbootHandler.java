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
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import io.jboot.Jboot;
import io.jboot.JbootConstants;
import io.jboot.component.metric.JbootMetricConfig;
import io.jboot.exception.JbootExceptionHolder;
import io.jboot.web.JbootRequestContext;
import io.jboot.web.session.JbootServletRequestWrapper;
import io.jboot.web.websocket.JbootWebsocketManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class JbootHandler extends Handler {


    private static JbootMetricConfig metricsConfig = Jboot.config(JbootMetricConfig.class);

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (target.indexOf('.') != -1
                || JbootWebsocketManager.me().isWebsokcetEndPoint(target)
                || metricsConfig.getUrl() != null && target.startsWith(metricsConfig.getUrl())) {
            return;
        }


        /**
         * 通过 JbootRequestContext 去保存 request，然后可以在当前线程的任何地方
         * 通过 JbootRequestContext.getRequest() 去获取。
         */
        JbootServletRequestWrapper jbootServletRequest = new JbootServletRequestWrapper(request, response);
        JbootRequestContext.handle(jbootServletRequest, response);


        /**
         * 初始化 当前线程的 Hystrix
         */
        HystrixRequestContext context = HystrixRequestContext.initializeContext();

        /**
         * 初始化 异常记录器，用于记录异常信息，然后在页面输出
         */
        JbootExceptionHolder.init();


        try {
            /**
             * 执行请求逻辑
             */
            doHandle(target, jbootServletRequest, response, isHandled);

        } finally {
            JbootExceptionHolder.release();
            context.shutdown();
            JbootRequestContext.release();

            jbootServletRequest.refreshSession();
        }

    }

    private void doHandle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        request.setAttribute(JbootConstants.ATTR_REQUEST, request);
        request.setAttribute(JbootConstants.ATTR_CONTEXT_PATH, request.getContextPath());
        next.handle(target, request, response, isHandled);
    }


}
