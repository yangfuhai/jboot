/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.JbootConsts;
import io.jboot.exception.JbootExceptionHolder;
import io.jboot.web.session.JbootServletRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于对 request 封装 和 CPATH 的设置
 */
public class JbootHandler extends Handler {


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        JbootServletRequestWrapper requestWrapper = new JbootServletRequestWrapper(request, response);
        try {
            doHandle(target, requestWrapper, response, isHandled);
        } finally {
            JbootExceptionHolder.release();
            requestWrapper.refreshSession();
        }
    }


    private void doHandle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        request.setAttribute(JbootConsts.ATTR_CONTEXT_PATH, request.getContextPath());
        next.handle(target, request, response, isHandled);
    }


}
